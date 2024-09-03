package se.sundsvall.billingpreprocessor.api;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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
class JobsResourceTest {
	private static final String MUNICIPALITY_ID = "2281";
	private static final String BASE_PATH = String.format("/%s/jobs", MUNICIPALITY_ID);

	@Autowired
	private WebTestClient webTestClient;

	@MockBean
	private AsyncExecutorService serviceMock;

	@Test
	void createFiles() {

		// Call
		final var requestId = webTestClient.post().uri(BASE_PATH + "/filecreator").contentType(APPLICATION_JSON)
			.exchange()
			.expectStatus().isAccepted()
			.expectBody().isEmpty()
			.getResponseHeaders()
			.get("x-request-id")
			.getFirst();

		// Verification
		verify(serviceMock).createFiles(requestId, MUNICIPALITY_ID);
		verifyNoMoreInteractions(serviceMock);
	}

	@Test
	void transferFiles() {

		// Call
		final var requestId = webTestClient.post().uri(BASE_PATH + "/filetransferrer").contentType(APPLICATION_JSON)
			.exchange()
			.expectStatus().isAccepted()
			.expectBody().isEmpty()
			.getResponseHeaders()
			.get("x-request-id")
			.getFirst();

		// Verification
		verify(serviceMock).transferFiles(requestId, MUNICIPALITY_ID);
		verifyNoMoreInteractions(serviceMock);
	}
}
