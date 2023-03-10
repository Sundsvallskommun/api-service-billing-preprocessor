package se.sundsvall.billingpreprocessor.api;

import static com.fasterxml.jackson.annotation.JsonCreator.Mode.PROPERTIES;
import static java.util.Collections.emptyMap;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.ALL;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static se.sundsvall.billingpreprocessor.api.model.enums.Status.NEW;
import static se.sundsvall.billingpreprocessor.api.model.enums.Type.INTERNAL;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import se.sundsvall.billingpreprocessor.Application;
import se.sundsvall.billingpreprocessor.api.model.AccountInformation;
import se.sundsvall.billingpreprocessor.api.model.BillingRecord;
import se.sundsvall.billingpreprocessor.api.model.Invoice;
import se.sundsvall.billingpreprocessor.api.model.InvoiceRow;
import se.sundsvall.billingpreprocessor.integration.db.model.BillingRecordEntity;
import se.sundsvall.billingpreprocessor.service.BillingRecordService;


@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("junit")
class BillingRecordsResourceTest {
	private static final String PATH = "/billingrecords";

	@Autowired
	private WebTestClient webTestClient;

	@LocalServerPort
	private int port;

	@MockBean
	private BillingRecordService serviceMock;

	@Captor
	private ArgumentCaptor<BillingRecord> billingRecordCaptor;

	@Test
	void createBillingRecord() {
		// Parameter values
		final var uuid = "c9242a01-e7bd-4f59-b4cd-66210c427904";
		final var instance = createBillingRecordInstance();

		// Mock
		when(serviceMock.createBillingRecord(any())).thenReturn(uuid);

		// Call
		webTestClient.post().uri(PATH).contentType(APPLICATION_JSON)
			.bodyValue(instance)
			.exchange()
			.expectStatus().isCreated()
			.expectHeader().contentType(ALL)
			.expectHeader().location("http://localhost:".concat(String.valueOf(port)).concat(PATH).concat("/").concat(uuid))
			.expectBody().isEmpty();

		// Verification
		verify(serviceMock).createBillingRecord(billingRecordCaptor.capture());
		assertThat(billingRecordCaptor.getValue()).usingRecursiveComparison().isEqualTo(instance);
	}

	@Test
	void readBillingRecord() {
		// Parameter values
		final var uuid = randomUUID().toString();
		final var billingRecord = BillingRecord.create().withId(uuid);

		// Mock
		when(serviceMock.readBillingRecord(uuid)).thenReturn(billingRecord);

		// Call
		final var response = webTestClient.get().uri(builder -> builder.path(PATH + "/{id}").build(Map.of("id", uuid)))
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(BillingRecord.class)
			.returnResult()
			.getResponseBody();

		// Verification
		verify(serviceMock).readBillingRecord(uuid);
		assertThat(response).isNotNull().isEqualTo(billingRecord);
	}

	@Test
	void findBillingRecordsWithNoFilter() {
		// Parameter values
		final var pageable = PageRequest.of(0, 20);
		final var matches = new PageImpl<>(List.of(BillingRecord.create()), pageable, 1);

		// Mock
		when(serviceMock.findBillingIRecords(null, pageable)).thenReturn(matches);

		// Call
		final var response = webTestClient.get().uri(builder -> builder.path(PATH).build(emptyMap()))
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.returnResult(new ParameterizedTypeReference<RestResponsePage<BillingRecord>>() {})
			.getResponseBody()
			.blockFirst();

		// Verification
		verify(serviceMock).findBillingIRecords(null, pageable);
		assertThat(response).isNotNull().isEqualTo(matches);
		assertThat(response.getContent()).hasSize(1);
	}

	@Test
	void findBillingRecordsWithFilter() {
		// Parameter values
		final var page = 13;
		final var size = 37;
		final var pageable = PageRequest.of(page, size);
		final var matches = new PageImpl<>(List.of(BillingRecord.create()), pageable, 1);
		final var filter = "category:'ACCESS_CARD' and status:'NEW'";

		// Mock
		when(serviceMock.findBillingIRecords(ArgumentMatchers.<Specification<BillingRecordEntity>>any(), eq(pageable))).thenReturn(matches);

		// Call
		final var response = webTestClient.get().uri(builder -> builder.path(PATH)
			.queryParam("filter", filter)
			.queryParam("page", page)
			.queryParam("size", size).build(emptyMap()))
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.returnResult(new ParameterizedTypeReference<RestResponsePage<BillingRecord>>() {})
			.getResponseBody()
			.blockFirst();

		// Verification
		verify(serviceMock).findBillingIRecords(ArgumentMatchers.<Specification<BillingRecordEntity>>any(), eq(pageable));
		assertThat(response).isNotNull().isEqualTo(matches);
		assertThat(response.getContent()).hasSize(1);
	}

	@Test
	void updateBillingRecord() {
		// Parameter values
		final var uuid = randomUUID().toString();
		final var instance = createBillingRecordInstance();
		final var updatedInstance = BillingRecord.create().withId(uuid);

		// Mock
		when(serviceMock.updateBillingRecord(eq(uuid), any())).thenReturn(updatedInstance);

		// Call
		final var response = webTestClient.put().uri(builder -> builder.path(PATH + "/{id}").build(Map.of("id", uuid)))
			.contentType(APPLICATION_JSON)
			.bodyValue(instance)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(BillingRecord.class)
			.returnResult()
			.getResponseBody();

		// Verification
		verify(serviceMock).updateBillingRecord(eq(uuid), billingRecordCaptor.capture());
		assertThat(billingRecordCaptor.getValue()).usingRecursiveComparison().isEqualTo(instance);
		assertThat(response).isEqualTo(updatedInstance);
	}

	@Test
	void deleteBillingRecord() {
		final var uuid = randomUUID().toString();
		webTestClient.delete().uri(builder -> builder.path(PATH + "/{id}").build(Map.of("id", uuid)))
			.exchange()
			.expectStatus().isNoContent()
			.expectBody().isEmpty();

		// Verification
		verify(serviceMock).deleteBillingRecord(uuid);
	}

	private static BillingRecord createBillingRecordInstance() {
		return BillingRecord.create()
			.withCategory("ACCESS_CARD")
			.withInvoice(createInvoiceInstance())
			.withStatus(NEW)
			.withType(INTERNAL);
	}

	private static Invoice createInvoiceInstance() {
		return Invoice.create()
			.withCustomerId("16")
			.withDescription("Errand number: 2113-01784")
			.withOurReference("Johan Doe")
			.withReferenceId("22940338")
			.withInvoiceRows(createInvoiceRowInstances());
	}

	private static List<InvoiceRow> createInvoiceRowInstances() {
		return List.of(
			InvoiceRow.create()
				.withDescriptions(List.of("Ordernummer: azi-330c-3fne-33"))
				.withQuantity(0),
			InvoiceRow.create()
				.withDescriptions(List.of("Best??llare: joh01doe 22940338"))
				.withQuantity(0),
			InvoiceRow.create()
				.withDescriptions(List.of("Anv??ndare: Johan Doe joh01doe"))
				.withQuantity(0),
			InvoiceRow.create()
				.withDescriptions(List.of("Passerkort utan foto"))
				.withAccountInformation(createAccountInformationInstance())
				.withCostPerUnit(150f)
				.withQuantity(1));
	}

	private static AccountInformation createAccountInformationInstance() {
		return AccountInformation.create()
			.withActivity("5247000")
			.withDepartment("910300")
			.withCostCenter("1620000")
			.withSubaccount("936100")
			.withCounterpart("counterPart");
	}

	// Helper implementation of Page
	@JsonIgnoreProperties(ignoreUnknown = true)
	private static class RestResponsePage<T> extends PageImpl<T> {
		private static final long serialVersionUID = -7361702892303169935L;

		@JsonCreator(mode = PROPERTIES)
		public RestResponsePage(@JsonProperty("content") final List<T> content, @JsonProperty("number") final int number, @JsonProperty("size") final int size,
			@JsonProperty("totalElements") final Long totalElements, @JsonProperty("pageable") final JsonNode pageable, @JsonProperty("last") final boolean last,
			@JsonProperty("totalPages") final int totalPages, @JsonProperty("sort") final JsonNode sort, @JsonProperty("first") final boolean first,
			@JsonProperty("numberOfElements") final int numberOfElements) {
			super(content, PageRequest.of(number, size), totalElements);
		}

		public RestResponsePage(final List<T> content, final Pageable pageable, final long total) {
			super(content, pageable, total);
		}

		public RestResponsePage(final List<T> content) {
			super(content);
		}

		public RestResponsePage() {
			super(new ArrayList<>());
		}
	}
}
