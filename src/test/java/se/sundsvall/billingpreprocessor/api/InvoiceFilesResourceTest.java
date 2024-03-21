package se.sundsvall.billingpreprocessor.api;

import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import se.sundsvall.billingpreprocessor.Application;
import se.sundsvall.billingpreprocessor.service.AsyncExecutorService;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("junit")
class InvoiceFilesResourceTest {
	private static final String PATH = "/invoicefiles";

	@Autowired
	private WebTestClient webTestClient;

	@MockBean
	private AsyncExecutorService serviceMock;

	@Test
	void triggerInvoiceFileCreation() {

		// Call
		final var requestId = webTestClient.post().uri(PATH).contentType(APPLICATION_JSON)
			.exchange()
			.expectStatus().isNoContent()
			.expectBody().isEmpty()
			.getResponseHeaders()
			.get("x-request-id")
			.getFirst();

		// Verification
		verify(serviceMock).createFileEntities(requestId);
	}
}
