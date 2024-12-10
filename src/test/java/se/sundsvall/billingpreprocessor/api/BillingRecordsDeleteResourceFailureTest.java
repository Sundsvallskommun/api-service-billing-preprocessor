package se.sundsvall.billingpreprocessor.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.zalando.problem.Status.BAD_REQUEST;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.zalando.problem.violations.ConstraintViolationProblem;
import org.zalando.problem.violations.Violation;
import se.sundsvall.billingpreprocessor.Application;
import se.sundsvall.billingpreprocessor.service.BillingRecordService;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("junit")
class BillingRecordsDeleteResourceFailureTest {
	private static final String PATH = "/{municipalityId}/billingrecords/{id}";

	@Autowired
	private WebTestClient webTestClient;

	@MockitoBean
	private BillingRecordService serviceMock;

	@Test
	void updateBillingRecordWithInvalidUuid() {
		// Call
		final var response = webTestClient.delete().uri(builder -> builder.path(PATH).build(Map.of("id", "invalid-uuid", "municipalityId", "2281")))
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations()).extracting(Violation::getField, Violation::getMessage).containsExactlyInAnyOrder(
			tuple("deleteBillingRecord.id", "not a valid UUID"));

		// Verification
		verifyNoInteractions(serviceMock);
	}

	@Test
	void updateBillingRecordWithInvalidMunicipalityId() {
		// Call
		final var response = webTestClient.delete().uri(builder -> builder.path(PATH).build(Map.of("id", "c9242a01-e7bd-4f59-b4cd-66210c427904", "municipalityId", "666")))
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations()).extracting(Violation::getField, Violation::getMessage).containsExactlyInAnyOrder(
			tuple("deleteBillingRecord.municipalityId", "not a valid municipality ID"));

		// Verification
		verifyNoInteractions(serviceMock);
	}
}
