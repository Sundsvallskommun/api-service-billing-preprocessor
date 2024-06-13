package se.sundsvall.billingpreprocessor.apptest;

import static java.nio.file.Files.readString;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.apache.commons.text.StringEscapeUtils.unescapeJava;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.awaitility.Awaitility.await;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.util.ResourceUtils.getFile;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.InvoiceFileStatus.GENERATED;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.InvoiceFileStatus.SEND_SUCCESSFUL;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.Type.EXTERNAL;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.Type.INTERNAL;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.MountableFile;

import se.sundsvall.billingpreprocessor.Application;
import se.sundsvall.billingpreprocessor.integration.db.InvoiceFileRepository;
import se.sundsvall.billingpreprocessor.integration.db.model.InvoiceFileEntity;
import se.sundsvall.billingpreprocessor.service.creator.config.InvoiceCreatorProperties;
import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;

/**
 * Jobs IT tests.
 */
@WireMockAppTestSuite(files = "classpath:/JobsIT/", classes = Application.class)
@Sql({
	"/db/scripts/truncate.sql",
	"/db/scripts/testdata-it.sql"
})
class JobsIT extends AbstractAppTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(JobsIT.class);

	@Container
	private static final GenericContainer<?> SFTP_SERVER = new GenericContainer<>("atmoz/sftp:alpine-3.7")
		.withCopyFileToContainer(MountableFile.forClasspathResource("keys/ssh_host_rsa_key", 0600), "/etc/ssh/")
		.withCopyFileToContainer(MountableFile.forClasspathResource("keys/ssh_host_rsa_key.pub", 0600), "/etc/ssh/")
		.withExposedPorts(22)
		.withCommand("user:pass:1001::upload");

	@DynamicPropertySource
	static void registerProperties(final DynamicPropertyRegistry registry) throws IOException {
		SFTP_SERVER.start();
		SFTP_SERVER.followOutput(new Slf4jLogConsumer(LOGGER));

		final var key = readString(getFile("classpath:keys/ssh_host_rsa_key.pub").toPath());
		registry.add("integration.sftp.host", SFTP_SERVER::getHost);
		registry.add("integration.sftp.port", () -> SFTP_SERVER.getMappedPort(22));
		registry.add("integration.sftp.knownHosts", () -> String.format("[%s]:%s %s", SFTP_SERVER.getHost(), SFTP_SERVER.getMappedPort(22), key));
	}

	@Autowired
	private InvoiceFileRepository repository;

	@Autowired
	private InvoiceCreatorProperties properties;

	@Test
	void test01_createInvoiceFiles() {
		assertThat(repository.findAll()).isEmpty();

		setupCall()
			.withServicePath("/jobs/filecreator")
			.withHttpMethod(POST)
			.withExpectedResponseStatus(ACCEPTED)
			.withExpectedResponseBodyIsNull()
			.sendRequestAndVerifyResponse();

		final var invoiceFiles = repository.findAll();

		assertThat(invoiceFiles).hasSize(2)
			.allSatisfy(file -> {
				assertThat(file.getCreated()).isCloseTo(OffsetDateTime.now(), within(2, SECONDS));
				assertThat(file.getSent()).isNull();
				assertThat(file.getStatus()).isEqualTo(GENERATED);
			})
			.satisfiesExactlyInAnyOrder(file -> {
				assertThat(file.getContent()).isEqualTo(getResource("/filecontent/expected_internal_content.txt"));
				assertThat(file.getName()).isEqualTo("IPKISYCASE_%s.txt".formatted(LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)));
				assertThat(file.getType()).isEqualTo(INTERNAL.name());
			}, file -> {
				assertThat(file.getContent()).isEqualTo(getResource("/filecontent/expected_external_content.txt").replace("yyMMdd", LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"))));
				assertThat(file.getName()).isEqualTo("KRISYCASE_%s.txt".formatted(LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)));
				assertThat(file.getType()).isEqualTo(EXTERNAL.name());
			});
	}

	@Test
	@Sql("/db/scripts/testdata-it-add-generated-file-entries.sql")
	void test02_transferInvoiceFiles() {
		assertThat(repository.findAll())
			.map(InvoiceFileEntity::getStatus)
			.allMatch(status -> Objects.equals(status, GENERATED));

		setupCall()
			.withServicePath("/jobs/filetransferrer")
			.withHttpMethod(POST)
			.withExpectedResponseStatus(ACCEPTED)
			.withExpectedResponseBodyIsNull()
			.sendRequestAndVerifyResponse();

		await()
			.atMost(10, TimeUnit.SECONDS)
			.until(() -> {
				return repository.findAll().stream()
					.map(InvoiceFileEntity::getStatus)
					.allMatch(s -> Objects.equals(s, SEND_SUCCESSFUL));
		});
	}

	@AfterAll
	static void tearDown() {
		SFTP_SERVER.stop();
	}

	private String getResource(final String filePath) throws IOException {
		final var path = getFile(getTestDirectoryPath() + filePath).toPath();
		return readString(path, StandardCharsets.UTF_8)
			.replaceAll(System.lineSeparator(), unescapeJava(properties.recordTerminator()));
	}
}
