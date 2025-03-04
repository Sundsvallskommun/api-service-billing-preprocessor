package se.sundsvall.billingpreprocessor.api;

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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
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
	private static final String MUNICIPALITY_ID = "2281";
	private static final String PATH = String.format("/%s/billingrecords", MUNICIPALITY_ID);

	@Autowired
	private WebTestClient webTestClient;

	@MockitoBean
	private BillingRecordService serviceMock;

	@Captor
	private ArgumentCaptor<BillingRecord> billingRecordCaptor;

	@Captor
	private ArgumentCaptor<List<BillingRecord>> billingRecordsCaptor;

	@Test
	void createBillingRecord() {
		// Parameter values
		final var uuid = "c9242a01-e7bd-4f59-b4cd-66210c427904";
		final var instance = createBillingRecordInstance();

		// Mock
		when(serviceMock.createBillingRecord(any(), any())).thenReturn(uuid);

		// Call
		webTestClient.post().uri(PATH).contentType(APPLICATION_JSON)
			.bodyValue(instance)
			.exchange()
			.expectStatus().isCreated()
			.expectHeader().contentType(ALL)
			.expectHeader().location(PATH + "/" + uuid)
			.expectBody().isEmpty();

		// Verification
		verify(serviceMock).createBillingRecord(billingRecordCaptor.capture(), eq(MUNICIPALITY_ID));
		assertThat(billingRecordCaptor.getValue()).usingRecursiveComparison().isEqualTo(instance);
	}

	@Test
	void createBillingRecords() {
		// Parameter values
		final var uuid = "c9242a01-e7bd-4f59-b4cd-66210c427904";
		final var instance = createBillingRecordInstance();
		final var instance2 = createBillingRecordInstance();
		final var instance3 = createBillingRecordInstance();
		final var instance4 = createBillingRecordInstance();

		final List<BillingRecord> billingRecords = new ArrayList<>();
		billingRecords.add(instance);
		billingRecords.add(instance2);
		billingRecords.add(instance3);
		billingRecords.add(instance4);

		// Mock
		when(serviceMock.createBillingRecords(any(), any())).thenReturn(List.of(uuid, uuid, uuid, uuid));

		// Call
		final var result = webTestClient.post().uri(PATH.concat("/batch")).contentType(APPLICATION_JSON)
			.bodyValue(billingRecords)
			.exchange()
			.expectStatus().isCreated()
			.expectHeader().contentType(ALL)
			.expectHeader().location(PATH + "/")
			.expectBody(String[].class)
			.returnResult().getResponseBody();

		// Verification
		verify(serviceMock).createBillingRecords(billingRecordsCaptor.capture(), eq(MUNICIPALITY_ID));
		assertThat(billingRecordsCaptor.getValue()).usingRecursiveComparison().isEqualTo(billingRecords);
		assertThat(result).isNotNull().hasSize(4).contains(uuid, uuid, uuid, uuid);
	}

	@Test
	void readBillingRecord() {
		// Parameter values
		final var uuid = randomUUID().toString();
		final var billingRecord = BillingRecord.create().withId(uuid);

		// Mock
		when(serviceMock.readBillingRecord(any(), any())).thenReturn(billingRecord);

		// Call
		final var response = webTestClient.get().uri(builder -> builder.path(PATH + "/{id}").build(Map.of("id", uuid)))
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(BillingRecord.class)
			.returnResult()
			.getResponseBody();

		// Verification
		verify(serviceMock).readBillingRecord(uuid, MUNICIPALITY_ID);
		assertThat(response).isNotNull().isEqualTo(billingRecord);
	}

	@Test
	void findBillingRecordsWithNoFilter() {
		// Parameter values
		final var pageable = PageRequest.of(0, 20);
		final var matches = new PageImpl<>(List.of(BillingRecord.create()), pageable, 1);

		// Mock
		when(serviceMock.findBillingRecords(Mockito.<Specification<BillingRecordEntity>>any(), eq(pageable), any())).thenReturn(matches);

		// Call
		final var response = webTestClient.get().uri(builder -> builder.path(PATH).build(emptyMap()))
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(new ParameterizedTypeReference<Page<BillingRecord>>() {})
			.returnResult()
			.getResponseBody();

		// Verification
		verify(serviceMock).findBillingRecords(Mockito.<Specification<BillingRecordEntity>>any(), eq(pageable), eq(MUNICIPALITY_ID));
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
		when(serviceMock.findBillingRecords(ArgumentMatchers.<Specification<BillingRecordEntity>>any(), eq(pageable), any())).thenReturn(matches);

		// Call
		final var response = webTestClient.get().uri(builder -> builder.path(PATH)
			.queryParam("filter", filter)
			.queryParam("page", page)
			.queryParam("size", size).build(emptyMap()))
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(new ParameterizedTypeReference<Page<BillingRecord>>() {})
			.returnResult()
			.getResponseBody();

		// Verification
		verify(serviceMock).findBillingRecords(ArgumentMatchers.<Specification<BillingRecordEntity>>any(), eq(pageable), eq(MUNICIPALITY_ID));
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
		when(serviceMock.updateBillingRecord(eq(uuid), any(), any())).thenReturn(updatedInstance);

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
		verify(serviceMock).updateBillingRecord(eq(uuid), billingRecordCaptor.capture(), eq(MUNICIPALITY_ID));
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
		verify(serviceMock).deleteBillingRecord(uuid, MUNICIPALITY_ID);
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
			.withCustomerReference("joh01doe")
			.withInvoiceRows(createInvoiceRowInstances());
	}

	private static List<InvoiceRow> createInvoiceRowInstances() {
		return List.of(
			InvoiceRow.create()
				.withDescriptions(List.of("Ordernummer: azi-330c-3fne-33"))
				.withQuantity(BigDecimal.ZERO),
			InvoiceRow.create()
				.withDescriptions(List.of("Beställare: joh01doe 22940338"))
				.withQuantity(BigDecimal.ZERO),
			InvoiceRow.create()
				.withDescriptions(List.of("Användare: Johan Doe joh01doe"))
				.withQuantity(BigDecimal.ZERO),
			InvoiceRow.create()
				.withDescriptions(List.of("Passerkort utan foto"))
				.withAccountInformation(List.of(createAccountInformationInstance()))
				.withCostPerUnit(BigDecimal.valueOf(150d))
				.withQuantity(BigDecimal.ONE));
	}

	private static AccountInformation createAccountInformationInstance() {
		return AccountInformation.create()
			.withActivity("5247000")
			.withAmount(BigDecimal.valueOf(100d))
			.withDepartment("910300")
			.withCostCenter("1620000")
			.withSubaccount("936100")
			.withCounterpart("counterPart");
	}
}
