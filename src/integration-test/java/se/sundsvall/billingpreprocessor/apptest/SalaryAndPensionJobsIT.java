package se.sundsvall.billingpreprocessor.apptest;

import static java.nio.file.Files.readString;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.apache.commons.text.StringEscapeUtils.unescapeJava;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.util.ResourceUtils.getFile;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.InvoiceFileStatus.GENERATED;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.Type.EXTERNAL;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.Type.INTERNAL;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import se.sundsvall.billingpreprocessor.Application;
import se.sundsvall.billingpreprocessor.integration.db.InvoiceFileRepository;
import se.sundsvall.billingpreprocessor.service.creator.config.InvoiceCreatorProperties;
import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;

@WireMockAppTestSuite(files = "classpath:/SalaryAndPensionJobsIT/", classes = Application.class)
@Sql({
	"/db/scripts/truncate.sql",
	"/db/scripts/testdata-salaryandpension-it.sql"
})
class SalaryAndPensionJobsIT extends AbstractAppTest {
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyMMddHHmm");

	@Autowired
	private InvoiceFileRepository repository;

	@Autowired
	private InvoiceCreatorProperties properties;

	@Test
	void test01_createInvoiceFiles() {
		assertThat(repository.findAll()).isEmpty();

		setupCall()
			.withServicePath("/2281/jobs/filecreator")
			.withHttpMethod(POST)
			.withExpectedResponseStatus(ACCEPTED)
			.withExpectedResponseBodyIsNull()
			.sendRequestAndVerifyResponse();

		Awaitility.await()
			.atMost(Duration.of(5, SECONDS))
			.ignoreExceptions()
			.until(this::assertFileEntries);
	}

	private boolean assertFileEntries() {
		assertThat(repository.findAll()).hasSize(2)
			.allSatisfy(file -> {
				assertThat(file.getCreated()).isCloseTo(OffsetDateTime.now(), within(2, SECONDS));
				assertThat(file.getSent()).isNull();
				assertThat(file.getStatus()).isEqualTo(GENERATED);
				assertThat(file.getEncoding()).isEqualTo(StandardCharsets.ISO_8859_1.toString());
				assertThat(file.getMunicipalityId()).isEqualTo("2281");
			})
			.satisfiesExactlyInAnyOrder(file -> {
				assertThat(file.getContent()).isEqualTo(getResource("/filecontent/expected_internal_content.txt"));
				assertThat(file.getName()).isEqualTo("ipklop_%s.txt".formatted(LocalDateTime.now().format(DATE_TIME_FORMATTER)));
				assertThat(file.getType()).isEqualTo(INTERNAL.name());
			}, file -> {
				assertThat(file.getContent()).isEqualTo(getResource("/filecontent/expected_external_content.txt").replace("yyMMdd", LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"))));
				assertThat(file.getName()).isEqualTo("krlope_%s.txt".formatted(LocalDateTime.now().format(DATE_TIME_FORMATTER)));
				assertThat(file.getType()).isEqualTo(EXTERNAL.name());
			});

		return true;
	}

	private String getResource(final String filePath) throws IOException {
		final var path = getFile(getTestDirectoryPath() + filePath).toPath();
		return readString(path, StandardCharsets.ISO_8859_1)
			.replaceAll(System.lineSeparator(), unescapeJava(properties.recordTerminator()));
	}
}
