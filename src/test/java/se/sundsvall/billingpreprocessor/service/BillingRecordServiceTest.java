package se.sundsvall.billingpreprocessor.service;

import static java.util.Collections.emptyList;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.zalando.problem.Status.BAD_REQUEST;
import static org.zalando.problem.Status.METHOD_NOT_ALLOWED;
import static org.zalando.problem.Status.NOT_FOUND;
import static se.sundsvall.billingpreprocessor.api.model.enums.Status.NEW;
import static se.sundsvall.billingpreprocessor.api.model.enums.Type.INTERNAL;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.zalando.problem.ThrowableProblem;

import se.sundsvall.billingpreprocessor.api.model.AccountInformation;
import se.sundsvall.billingpreprocessor.api.model.BillingRecord;
import se.sundsvall.billingpreprocessor.api.model.Invoice;
import se.sundsvall.billingpreprocessor.api.model.InvoiceRow;
import se.sundsvall.billingpreprocessor.integration.db.BillingRecordRepository;
import se.sundsvall.billingpreprocessor.integration.db.InvoiceFileConfigurationRepository;
import se.sundsvall.billingpreprocessor.integration.db.model.BillingRecordEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.InvoiceEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.enums.Status;
import se.sundsvall.billingpreprocessor.integration.db.model.enums.Type;

@ExtendWith(MockitoExtension.class)
class BillingRecordServiceTest {
	private static final String ID = randomUUID().toString();
	private static final String CATEGORY = "ACCESS_CARD";

	@Mock
	private BillingRecordRepository billingRecordRepositoryMock;

	@Mock
	private InvoiceFileConfigurationRepository invoiceFileConfigurationRepositoryMock;

	@Mock
	private Specification<BillingRecordEntity> specificationMock;

	@InjectMocks
	private BillingRecordService service;

	@Test
	void createBillingRecord() {
		// Setup
		final var billingRecord = createBillingRecordInstance();

		// Mock
		when(invoiceFileConfigurationRepositoryMock.existsByTypeAndCategoryTag(billingRecord.getType().name(), billingRecord.getCategory())).thenReturn(true);
		when(billingRecordRepositoryMock.save(any(BillingRecordEntity.class))).thenReturn(BillingRecordEntity.create().withId(ID));

		// Call
		final var result = service.createBillingRecord(billingRecord);

		// Assertions and verifications
		assertThat(result).isEqualTo(ID);
		verify(invoiceFileConfigurationRepositoryMock).existsByTypeAndCategoryTag(INTERNAL.name(), CATEGORY);
		verify(billingRecordRepositoryMock).save(any(BillingRecordEntity.class));
		verifyNoMoreInteractions(invoiceFileConfigurationRepositoryMock, billingRecordRepositoryMock);
	}

	@Test
	void createBillingRecordWithMissingConfiguration() {
		// Setup
		final var billingRecord = createBillingRecordInstance();

		// Call
		final var e = assertThrows(ThrowableProblem.class, () -> service.createBillingRecord(billingRecord));

		// Assertions and verifications
		assertThat(e.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(e.getMessage()).isEqualTo("Bad Request: One or more billing records contain an unknown type or category!");
		verify(invoiceFileConfigurationRepositoryMock).existsByTypeAndCategoryTag(INTERNAL.name(), CATEGORY);
		verifyNoMoreInteractions(invoiceFileConfigurationRepositoryMock, billingRecordRepositoryMock);
	}

	@Test
	void createBillingRecords() {
		// Setup
		final var billingRecord = createBillingRecordInstance();

		// Mock
		when(invoiceFileConfigurationRepositoryMock.existsByTypeAndCategoryTag(billingRecord.getType().name(), billingRecord.getCategory())).thenReturn(true);
		when(billingRecordRepositoryMock.saveAll(any())).thenReturn(List.of(BillingRecordEntity.create().withId(ID)));

		// Call
		final var result = service.createBillingRecords(List.of(billingRecord));

		// Assertions and verifications
		assertThat(result).isNotEmpty().hasSize(1);
		assertThat(result.getFirst()).isEqualTo(ID);
		verify(invoiceFileConfigurationRepositoryMock).existsByTypeAndCategoryTag(INTERNAL.name(), CATEGORY);
		verify(billingRecordRepositoryMock).saveAll(anyList());
		verifyNoMoreInteractions(invoiceFileConfigurationRepositoryMock, billingRecordRepositoryMock);
	}

	@Test
	void createBillingRecordsWithEntryMissingConfiguration() {
		// Setup
		final var unknownCategory = "UNKNOWN_CATEGORY";

		final var input = List.of(
			createBillingRecordInstance(),
			createBillingRecordInstance().withCategory(unknownCategory));

		// Mock
		when(invoiceFileConfigurationRepositoryMock.existsByTypeAndCategoryTag(INTERNAL.name(), CATEGORY)).thenReturn(true);

		// Call
		final var e = assertThrows(ThrowableProblem.class, () -> service.createBillingRecords(input));

		// Assertions and verifications
		assertThat(e.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(e.getMessage()).isEqualTo("Bad Request: One or more billing records contain an unknown type or category!");
		verify(invoiceFileConfigurationRepositoryMock).existsByTypeAndCategoryTag(INTERNAL.name(), CATEGORY);
		verify(invoiceFileConfigurationRepositoryMock).existsByTypeAndCategoryTag(INTERNAL.name(), unknownCategory);
		verifyNoMoreInteractions(invoiceFileConfigurationRepositoryMock, billingRecordRepositoryMock);
	}

	@Test
	void findBillingRecordsWithMatches() {
		// Setup
		final var sort = Sort.by(DESC, "attribute.1", "attribute.2");
		final Pageable pageable = PageRequest.of(1, 2, sort);

		// Mock
		when(billingRecordRepositoryMock.findAll(specificationMock, pageable)).thenReturn(new PageImpl<>(List.of(createBillingRecordEntityInstance(), createBillingRecordEntityInstance()), pageable, 2L));
		when(billingRecordRepositoryMock.count(specificationMock)).thenReturn(10L);

		// Call
		final var matches = service.findBillingRecords(specificationMock, pageable);

		// Assertions and verifications
		assertThat(matches.getContent()).isNotEmpty().hasSize(2);
		assertThat(matches.getNumberOfElements()).isEqualTo(2);
		assertThat(matches.getTotalElements()).isEqualTo(10);
		assertThat(matches.getTotalPages()).isEqualTo(5);
		assertThat(matches.getPageable()).usingRecursiveComparison().isEqualTo(pageable);
		assertThat(matches.getSort()).usingRecursiveComparison().isEqualTo(sort);

		verify(billingRecordRepositoryMock).findAll(specificationMock, pageable);
		verify(billingRecordRepositoryMock).count(specificationMock);
		verifyNoMoreInteractions(billingRecordRepositoryMock);
	}

	@Test
	void findBillingRecordsWithoutMatches() {
		// Setup
		final var sort = Sort.by(DESC, "attribute.1", "attribute.2");
		final Pageable pageable = PageRequest.of(3, 7, sort);

		// Mock
		when(billingRecordRepositoryMock.findAll(specificationMock, pageable)).thenReturn(new PageImpl<>(emptyList()));

		// Call
		final var matches = service.findBillingRecords(specificationMock, pageable);

		// Assertions and verifications
		assertThat(matches.getContent()).isEmpty();
		assertThat(matches.getNumberOfElements()).isZero();
		assertThat(matches.getTotalElements()).isZero();
		assertThat(matches.getTotalPages()).isZero();
		assertThat(matches.getPageable()).usingRecursiveComparison().isEqualTo(pageable);
		assertThat(matches.getSort()).usingRecursiveComparison().isEqualTo(sort);

		verify(billingRecordRepositoryMock).findAll(specificationMock, pageable);
		verify(billingRecordRepositoryMock).count(specificationMock);
		verifyNoMoreInteractions(billingRecordRepositoryMock);
	}

	@Test
	void readExistingBillingRecord() {
		// Mock
		when(billingRecordRepositoryMock.existsById(ID)).thenReturn(true);
		when(billingRecordRepositoryMock.getReferenceById(ID)).thenReturn(createBillingRecordEntityInstance());

		// Call
		final var result = service.readBillingRecord(ID);

		// Assertions and verifications
		assertThat(result.getId()).isEqualTo(ID);
		verify(billingRecordRepositoryMock).existsById(ID);
		verify(billingRecordRepositoryMock).getReferenceById(ID);
		verifyNoMoreInteractions(billingRecordRepositoryMock);
	}

	@Test
	void readNonExistingBillingRecord() {
		// Call
		final var exception = assertThrows(ThrowableProblem.class, () -> service.readBillingRecord(ID));

		// Assertions and verifications
		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getTitle()).isEqualTo(NOT_FOUND.getReasonPhrase());
		assertThat(exception.getMessage()).isEqualTo("Not Found: A billing record with id '" + ID + "' could not be found!");

		verify(billingRecordRepositoryMock).existsById(ID);
		verifyNoMoreInteractions(billingRecordRepositoryMock);
	}

	@Test
	void updateExistingBillingRecord() {
		// Setup
		final var entity = createBillingRecordEntityInstance();

		// Mock
		when(billingRecordRepositoryMock.existsById(ID)).thenReturn(true);
		when(billingRecordRepositoryMock.getReferenceById(ID)).thenReturn(entity);
		when(billingRecordRepositoryMock.save(entity)).thenReturn(entity);

		// Call
		final var response = service.updateBillingRecord(ID, createBillingRecordInstance());

		// Assertions and verifications
		assertThat(response.getId()).isEqualTo(ID);

		verify(billingRecordRepositoryMock).existsById(ID);
		verify(billingRecordRepositoryMock).getReferenceById(ID);
		verify(billingRecordRepositoryMock).save(entity);
		verifyNoMoreInteractions(billingRecordRepositoryMock);
	}

	@Test
	void updateNonExistingBillingRecord() {
		// Setup
		final var billingRecord = BillingRecord.create();

		// Call
		final var exception = assertThrows(ThrowableProblem.class, () -> service.updateBillingRecord(ID, billingRecord));

		// Assertions and verifications
		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getTitle()).isEqualTo(NOT_FOUND.getReasonPhrase());
		assertThat(exception.getMessage()).isEqualTo("Not Found: A billing record with id '" + ID + "' could not be found!");

		verify(billingRecordRepositoryMock).existsById(ID);
		verifyNoMoreInteractions(billingRecordRepositoryMock);
	}

	@Test
	void deleteBillingRecordWithDeletableStatus() {
		// Mock
		when(billingRecordRepositoryMock.existsById(ID)).thenReturn(true);
		when(billingRecordRepositoryMock.getReferenceById(ID)).thenReturn(createBillingRecordEntityInstance().withStatus(Status.NEW));

		// Call
		service.deleteBillingRecord(ID);

		// Assertions and verifications
		verify(billingRecordRepositoryMock).existsById(ID);
		verify(billingRecordRepositoryMock).getReferenceById(ID);
		verify(billingRecordRepositoryMock).deleteById(ID);
		verifyNoMoreInteractions(billingRecordRepositoryMock);
	}

	@ParameterizedTest
	@EnumSource(value = Status.class, names = "NEW", mode = EXCLUDE)
	void deleteBillingRecordWithNonDeletableStatus(Status status) {
		// Mock
		when(billingRecordRepositoryMock.existsById(ID)).thenReturn(true);
		when(billingRecordRepositoryMock.getReferenceById(ID)).thenReturn(createBillingRecordEntityInstance().withStatus(status));

		// Call
		final var exception = assertThrows(ThrowableProblem.class, () -> service.deleteBillingRecord(ID));

		// Assertions and verifications
		assertThat(exception.getStatus()).isEqualTo(METHOD_NOT_ALLOWED);
		assertThat(exception.getTitle()).isEqualTo(METHOD_NOT_ALLOWED.getReasonPhrase());
		assertThat(exception.getMessage()).isEqualTo("Method Not Allowed: The billing record does not have status NEW and is therefore not possible to delete!");

		verify(billingRecordRepositoryMock).existsById(ID);
		verifyNoMoreInteractions(billingRecordRepositoryMock);
	}

	@Test
	void deleteNonExistingBillingRecord() {
		// Call
		final var exception = assertThrows(ThrowableProblem.class, () -> service.deleteBillingRecord(ID));

		// Assertions and verifications
		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getTitle()).isEqualTo(NOT_FOUND.getReasonPhrase());
		assertThat(exception.getMessage()).isEqualTo("Not Found: A billing record with id '" + ID + "' could not be found!");

		verify(billingRecordRepositoryMock).existsById(ID);
		verifyNoMoreInteractions(billingRecordRepositoryMock);
	}

	private static BillingRecord createBillingRecordInstance() {
		return BillingRecord.create()
			.withCategory(CATEGORY)
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
				.withQuantity(0f),
			InvoiceRow.create()
				.withDescriptions(List.of("Beställare: joh01doe 22940338"))
				.withQuantity(0f),
			InvoiceRow.create()
				.withDescriptions(List.of("Användare: Johan Doe joh01doe"))
				.withQuantity(0f),
			InvoiceRow.create()
				.withDescriptions(List.of("Passerkort utan foto"))
				.withAccountInformation(createAccountInformationInstance())
				.withCostPerUnit(150f)
				.withQuantity(1f));
	}

	private static AccountInformation createAccountInformationInstance() {
		return AccountInformation.create()
			.withActivity("5247000")
			.withDepartment("910300")
			.withCostCenter("1620000")
			.withSubaccount("936100");
	}

	private static BillingRecordEntity createBillingRecordEntityInstance() {
		return BillingRecordEntity.create()
			.withId(ID)
			.withInvoice(InvoiceEntity.create())
			.withStatus(Status.APPROVED)
			.withType(Type.EXTERNAL);
	}
}
