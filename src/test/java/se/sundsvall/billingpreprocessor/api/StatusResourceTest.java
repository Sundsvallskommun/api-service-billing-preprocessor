package se.sundsvall.billingpreprocessor.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.Month;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.billingpreprocessor.Application;
import se.sundsvall.billingpreprocessor.api.model.InvoiceFileStatus;
import se.sundsvall.billingpreprocessor.service.StatusService;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("junit")
class StatusResourceTest {

	@MockitoBean
	private StatusService statusServiceMock;

	@Autowired
	private WebTestClient webTestClient;

	@AfterEach
	void tearDown() {
		verifyNoMoreInteractions(statusServiceMock);
	}

	@Test
	void getFileStatusesForMonth() {
		var created = OffsetDateTime.now().minusMonths(1);
		var sent = OffsetDateTime.now().minusDays(3);
		var file1 = new InvoiceFileStatus("1", "invoice1.pdf", "EXTERNAL", "SEND_SUCCESSFUL", "2281", created, sent);
		var file2 = new InvoiceFileStatus("2", "invoice2.pdf", "INTERNAL", "SEND_FAILED", "2281", created, sent);

		when(statusServiceMock.getInvoiceFilesForMonth("2281", 2024, Month.JUNE))
			.thenReturn(List.of(file1, file2));

		var response = webTestClient.get()
			.uri(builder -> builder.path("/2281/status")
				.queryParam("month", "JUNE")
				.queryParam("year", "2024")
				.build())
			.exchange()
			.expectStatus().isOk()
			.expectBodyList(InvoiceFileStatus.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull().hasSize(2)
			.extracting("id", "name", "type", "status", "municipalityId", "createdAt", "sentAt")
			.containsExactlyInAnyOrder(
				tuple("1", "invoice1.pdf", "EXTERNAL", "SEND_SUCCESSFUL", "2281", created, sent),
				tuple("2", "invoice2.pdf", "INTERNAL", "SEND_FAILED", "2281", created, sent));

		verify(statusServiceMock).getInvoiceFilesForMonth("2281", 2024, Month.JUNE);
	}

}
