package se.sundsvall.billingpreprocessor.apptest;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.apache.commons.text.StringEscapeUtils.unescapeJava;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.InvoiceFileStatus.GENERATED;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.Type.EXTERNAL;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.Type.INTERNAL;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import se.sundsvall.billingpreprocessor.Application;
import se.sundsvall.billingpreprocessor.integration.db.InvoiceFileRepository;
import se.sundsvall.billingpreprocessor.service.creator.config.InvoiceCreatorProperties;
import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;

/**
 * Invoice files IT tests.
 */
@WireMockAppTestSuite(files = "classpath:/JobsIT/", classes = Application.class)
@Sql({
	"/db/scripts/truncate.sql",
	"/db/scripts/testdata-it.sql"
})
class JobsIT extends AbstractAppTest {
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
			.sendRequestAndVerifyResponse();

		final var invoiceFiles = repository.findAll();

		assertThat(invoiceFiles).hasSize(2)
			.allSatisfy(file -> {
				assertThat(file.getCreated()).isCloseTo(OffsetDateTime.now(), within(2, SECONDS));
				assertThat(file.getSent()).isNull();
				assertThat(file.getStatus()).isEqualTo(GENERATED);
			})
			.satisfiesExactlyInAnyOrder(file -> {
				assertThat(file.getContent()).isEqualTo(getResource("expected_internal_content.txt"));
				assertThat(file.getName()).isEqualTo("IPKISYCASE_%s.txt".formatted(LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)));
				assertThat(file.getType()).isEqualTo(INTERNAL.name());
			}, file -> {
				assertThat(file.getContent()).isEqualTo(getResource("expected_external_content.txt").replace("yyMMdd", LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"))));
				assertThat(file.getName()).isEqualTo("KRISYCASE_%s.txt".formatted(LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)));
				assertThat(file.getType()).isEqualTo(EXTERNAL.name());
			});
	}

	private String getResource(final String fileName) throws IOException, URISyntaxException {
		var path = getTestDirectoryPath().replaceFirst("classpath:", "") + "/filecontent/";
		return Files.readString(Paths.get(getClass().getClassLoader().getResource(path + fileName).toURI()), StandardCharsets.UTF_8)
			.replaceAll(System.lineSeparator(), unescapeJava(properties.recordTerminator()));
	}
}
