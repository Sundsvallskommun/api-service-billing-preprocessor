package se.sundsvall.billingpreprocessor.api;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.zalando.problem.Status.BAD_REQUEST;
import static se.sundsvall.billingpreprocessor.api.BillingRecordRequestUtil.createAddressDetailsInstance;
import static se.sundsvall.billingpreprocessor.api.BillingRecordRequestUtil.createBillingRecordInstance;
import static se.sundsvall.billingpreprocessor.api.BillingRecordRequestUtil.createInvoiceInstance;
import static se.sundsvall.billingpreprocessor.api.BillingRecordRequestUtil.createInvoiceRowInstance;
import static se.sundsvall.billingpreprocessor.api.BillingRecordRequestUtil.createRecipientInstance;
import static se.sundsvall.billingpreprocessor.api.model.enums.Type.EXTERNAL;
import static se.sundsvall.billingpreprocessor.api.model.enums.Type.INTERNAL;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.zalando.problem.Problem;
import org.zalando.problem.violations.ConstraintViolationProblem;
import org.zalando.problem.violations.Violation;
import se.sundsvall.billingpreprocessor.Application;
import se.sundsvall.billingpreprocessor.api.model.BillingRecord;
import se.sundsvall.billingpreprocessor.api.model.Recipient;
import se.sundsvall.billingpreprocessor.api.model.enums.Type;
import se.sundsvall.billingpreprocessor.service.BillingRecordService;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("junit")
class BillingRecordsUpdateResourceFailureTest {
	private static final String PATH = "/{municipalityId}/billingrecords/{id}";

	@Autowired
	private WebTestClient webTestClient;

	@MockitoBean
	private BillingRecordService serviceMock;

	@Test
	void updateBillingRecordWithInvalidUuid() {
		// Parameter values
		final var request = createBillingRecordInstance(EXTERNAL, true)
			.withInvoice(createInvoiceInstance(true, EXTERNAL).withInvoiceRows(List.of(createInvoiceRowInstance(true, EXTERNAL))))
			.withRecipient(createRecipientInstance(true).withAddressDetails(createAddressDetailsInstance(true)));

		// Call
		final var response = webTestClient.put().uri(builder -> builder.path(PATH).build(Map.of("id", "invalid-uuid", "municipalityId", "2281")))
			.contentType(APPLICATION_JSON)
			.bodyValue(request)
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations()).extracting(Violation::getField, Violation::getMessage).containsExactlyInAnyOrder(
			tuple("updateBillingRecord.id", "not a valid UUID"));

		// Verification
		verifyNoInteractions(serviceMock);
	}

	@Test
	void updateBillingRecordWithNullBody() {
		// Parameter values
		final var uuid = randomUUID().toString();

		// Call
		final var response = webTestClient.put().uri(builder -> builder.path(PATH).build(Map.of("id", uuid, "municipalityId", "2281")))
			.contentType(APPLICATION_JSON)
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(Problem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Bad Request");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getDetail()).isEqualTo(
			"Required request body is missing: org.springframework.http.ResponseEntity<se.sundsvall.billingpreprocessor.api.model.BillingRecord> se.sundsvall.billingpreprocessor.api.BillingRecordsResource.updateBillingRecord(java.lang.String,java.lang.String,se.sundsvall.billingpreprocessor.api.model.BillingRecord)");

		// Verification
		verifyNoInteractions(serviceMock);
	}

	@Test
	void updateBillingRecordWithEmptyBody() {
		// Parameter values
		final var uuid = randomUUID().toString();

		// Call
		final var response = webTestClient.put().uri(builder -> builder.path(PATH).build(Map.of("id", uuid, "municipalityId", "2281")))
			.contentType(APPLICATION_JSON)
			.bodyValue(BillingRecord.create())
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations()).extracting(Violation::getField, Violation::getMessage).containsExactlyInAnyOrder(
			tuple("category", "must not be null"),
			tuple("invoice", "must not be null"),
			tuple("status", "must not be null"),
			tuple("type", "must not be null"));

		// Verification
		verifyNoInteractions(serviceMock);
	}

	@Test
	void updateInternalBillingRecordWithTotallyInvalidInstance() {
		// Parameter values
		final var uuid = randomUUID().toString();
		final var request = createBillingRecordInstance(INTERNAL, false)
			.withInvoice(createInvoiceInstance(false, INTERNAL).withInvoiceRows(List.of(createInvoiceRowInstance(false, INTERNAL))))
			.withRecipient(Recipient.create().withPartyId("invalid").withLegalId("invalid").withAddressDetails(createAddressDetailsInstance(false)));

		// Call
		final var response = webTestClient.put().uri(builder -> builder.path(PATH).build(Map.of("id", uuid, "municipalityId", "666")))
			.contentType(APPLICATION_JSON)
			.bodyValue(request)
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations()).extracting(Violation::getField, Violation::getMessage).containsExactlyInAnyOrder(
			tuple("billingRecord", "can not contain vat code information on invoice rows when billing record is of type INTERNAL"),
			tuple("billingRecord", "invoice.ourReference is mandatory when billing record is of type INTERNAL"),
			tuple("billingRecord", "amount, costCenter, subaccount, department and counterpart must be present for invoice rows containing accountInformation"),
			tuple("category", "must be one of ACCESS_CARD, CUSTOMER_INVOICE, SALARY_AND_PENSION or ISYCASE"),
			tuple("approved", "must be null"),
			tuple("created", "must be null"),
			tuple("id", "must be null"),
			tuple("invoice.invoiceRows[0].descriptions[0]", "size must be between 1 and 30"),
			tuple("invoice.invoiceRows[0].totalAmount", "must be null"),
			tuple("invoice.totalAmount", "must be null"),
			tuple("recipient.partyId", "not a valid UUID"),
			tuple("modified", "must be null"));

		// Verification
		verifyNoInteractions(serviceMock);
	}

	@Test
	void updateExternalBillingRecordWithTotallyInvalidInstance() {
		// Parameter values
		final var uuid = randomUUID().toString();
		final var request = createBillingRecordInstance(EXTERNAL, false)
			.withInvoice(createInvoiceInstance(false, EXTERNAL).withInvoiceRows(List.of(createInvoiceRowInstance(false, EXTERNAL))))
			.withRecipient(Recipient.create().withAddressDetails(createAddressDetailsInstance(false)));

		// Call
		final var response = webTestClient.put().uri(builder -> builder.path(PATH).build(Map.of("id", uuid, "municipalityId", "666")))
			.contentType(APPLICATION_JSON)
			.bodyValue(request)
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations()).extracting(Violation::getField, Violation::getMessage).containsExactlyInAnyOrder(
			tuple("billingRecord", "Street, postal code and city must all be present in recipient.addressDetails for EXTERNAL billing record"),
			tuple("billingRecord", "recipient must either have an organization name or a first and last name defined"),
			tuple("billingRecord", "recipient must have partyId or legalId when billing record is of type EXTERNAL"),
			tuple("billingRecord", "must contain vat code information on invoice rows when billing record is of type EXTERNAL"),
			tuple("billingRecord", "amount, costCenter, subaccount, department and counterpart must be present for invoice rows containing accountInformation"),
			tuple("category", "must be one of ACCESS_CARD, CUSTOMER_INVOICE, SALARY_AND_PENSION or ISYCASE"),
			tuple("approved", "must be null"),
			tuple("created", "must be null"),
			tuple("id", "must be null"),
			tuple("invoice.customerReference", "must not be blank"),
			tuple("invoice.invoiceRows[0].descriptions[0]", "size must be between 1 and 30"),
			tuple("invoice.invoiceRows[0].totalAmount", "must be null"),
			tuple("invoice.totalAmount", "must be null"),
			tuple("modified", "must be null"));

		// Verification
		verifyNoInteractions(serviceMock);
	}

	@ParameterizedTest
	@EnumSource(value = Type.class)
	void updateBillingRecordWithStatusApprovedAndNoApprovedBy(Type type) {
		// Parameter values
		final var uuid = randomUUID().toString();
		final var request = createBillingRecordInstance(type, true)
			.withApprovedBy(null)
			.withInvoice(createInvoiceInstance(true, type).withInvoiceRows(List.of(createInvoiceRowInstance(true, type))))
			.withRecipient(createRecipientInstance(true).withAddressDetails(createAddressDetailsInstance(true)));

		// Call
		final var response = webTestClient.put().uri(builder -> builder.path(PATH).build(Map.of("id", uuid, "municipalityId", "2281")))
			.contentType(APPLICATION_JSON)
			.bodyValue(request)
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations()).extracting(Violation::getField, Violation::getMessage).containsExactlyInAnyOrder(
			tuple("billingRecord", "approvedBy must be present when status is APPROVED"));

		// Verification
		verifyNoInteractions(serviceMock);
	}

	@Test
	void updateExternalBillingRecordWithNoRecipient() {
		// Parameter values
		final var uuid = randomUUID().toString();
		final var request = createBillingRecordInstance(EXTERNAL, true)
			.withInvoice(createInvoiceInstance(true, EXTERNAL).withInvoiceRows(List.of(createInvoiceRowInstance(true, EXTERNAL))));

		// Call
		final var response = webTestClient.put().uri(builder -> builder.path(PATH).build(Map.of("id", uuid, "municipalityId", "2281")))
			.contentType(APPLICATION_JSON)
			.bodyValue(request)
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations()).extracting(Violation::getField, Violation::getMessage).containsExactlyInAnyOrder(
			tuple("billingRecord", "Street, postal code and city must all be present in recipient.addressDetails for EXTERNAL billing record"),
			tuple("billingRecord", "recipient can not be null when billing record is of type EXTERNAL"));

		// Verification
		verifyNoInteractions(serviceMock);
	}

	@Test
	void updateExternalBillingRecordWithInvalidVatCode() {
		// Parameter values
		final var uuid = randomUUID().toString();
		final var request = createBillingRecordInstance(EXTERNAL, true)
			.withInvoice(createInvoiceInstance(true, EXTERNAL).withInvoiceRows(List.of(createInvoiceRowInstance(true, EXTERNAL).withVatCode("INVALID"))))
			.withRecipient(createRecipientInstance(true).withAddressDetails(createAddressDetailsInstance(true)));

		// Call
		final var response = webTestClient.put().uri(builder -> builder.path(PATH).build(Map.of("id", uuid, "municipalityId", "2281")))
			.contentType(APPLICATION_JSON)
			.bodyValue(request)
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations()).extracting(Violation::getField, Violation::getMessage).containsExactlyInAnyOrder(
			tuple("invoice.invoiceRows[0].vatCode", "must be one of 00, 06, 12 or 25"));

		// Verification
		verifyNoInteractions(serviceMock);
	}

	@Test
	void updateBillingRecordWithNoAddressDetails() {
		// Parameter values
		final var uuid = randomUUID().toString();
		final var request = createBillingRecordInstance(EXTERNAL, true)
			.withInvoice(createInvoiceInstance(true, EXTERNAL).withInvoiceRows(List.of(createInvoiceRowInstance(true, EXTERNAL))))
			.withRecipient(createRecipientInstance(true));

		// Call
		final var response = webTestClient.put().uri(builder -> builder.path(PATH).build(Map.of("id", uuid, "municipalityId", "2281")))
			.contentType(APPLICATION_JSON)
			.bodyValue(request)
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations()).extracting(Violation::getField, Violation::getMessage).containsExactlyInAnyOrder(
			tuple("billingRecord", "Street, postal code and city must all be present in recipient.addressDetails for EXTERNAL billing record"));

		// Verification
		verifyNoInteractions(serviceMock);
	}
}
