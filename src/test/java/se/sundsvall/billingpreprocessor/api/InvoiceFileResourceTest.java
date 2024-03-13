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
import se.sundsvall.billingpreprocessor.service.InvoiceFileService;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("junit")
class InvoiceFileResourceTest {
	private static final String PATH = "/invoicefiles";

	@Autowired
	private WebTestClient webTestClient;

	@MockBean
	private InvoiceFileService serviceMock;

	@Test
	void triggerInvoiceFileCreation() {

		// Call
		webTestClient.post().uri(PATH).contentType(APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectBody().isEmpty();

		// Verification
		verify(serviceMock).createFileEntities();
	}
}
