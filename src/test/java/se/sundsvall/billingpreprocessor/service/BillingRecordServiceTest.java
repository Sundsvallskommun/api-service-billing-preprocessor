package se.sundsvall.billingpreprocessor.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
import se.sundsvall.dept44.problem.ThrowableProblem;

import static java.util.Collections.emptyList;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static se.sundsvall.billingpreprocessor.api.model.enums.Status.NEW;
import static se.sundsvall.billingpreprocessor.api.model.enums.Type.INTERNAL;

@ExtendWith(MockitoExtension.class)
class BillingRecordServiceTest {
	private static final String ID = randomUUID().toString();
	private static final String CATEGORY = "ACCESS_CARD";
	private static final String MUNICIPALITY_ID = "MUNICIPALITY_ID";

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
		final var result = service.createBillingRecord(billingRecord, MUNICIPALITY_ID);

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
		final var e = assertThrows(ThrowableProblem.class, () -> service.createBillingRecord(billingRecord, MUNICIPALITY_ID));

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
		final var result = service.createBillingRecords(List.of(billingRecord), MUNICIPALITY_ID);

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
		final var e = assertThrows(ThrowableProblem.class, () -> service.createBillingRecords(input, MUNICIPALITY_ID));

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
		when(billingRecordRepositoryMock.findAll(specificationMock, pageable)).thenReturn(new PageImpl<>(List.of(createBillingRecordEntityInstance(), createBillingRecordEntityInstance()), pageable, 10L));
		when(specificationMock.and(ArgumentMatchers.<Specification<BillingRecordEntity>>any())).thenReturn(specificationMock);

		// Call
		final var matches = service.findBillingRecords(specificationMock, pageable, MUNICIPALITY_ID);

		// Assertions and verifications
		assertThat(matches.getContent()).isNotEmpty().hasSize(2);
		assertThat(matches.getNumberOfElements()).isEqualTo(2);
		assertThat(matches.getTotalElements()).isEqualTo(10);
		assertThat(matches.getTotalPages()).isEqualTo(5);
		assertThat(matches.getPageable()).usingRecursiveComparison().isEqualTo(pageable);
		assertThat(matches.getSort()).usingRecursiveComparison().isEqualTo(sort);

		verify(billingRecordRepositoryMock).findAll(specificationMock, pageable);
		verify(specificationMock).and(ArgumentMatchers.<Specification<BillingRecordEntity>>any());
		verifyNoMoreInteractions(billingRecordRepositoryMock, specificationMock);
	}

	@Test
	void findBillingRecordsWithoutMatches() {
		// Setup
		final var sort = Sort.by(DESC, "attribute.1", "attribute.2");
		final Pageable pageable = PageRequest.of(3, 7, sort);

		// Mock
		when(billingRecordRepositoryMock.findAll(specificationMock, pageable)).thenReturn(new PageImpl<>(emptyList()));
		when(specificationMock.and(ArgumentMatchers.<Specification<BillingRecordEntity>>any())).thenReturn(specificationMock);

		// Call
		final var matches = service.findBillingRecords(specificationMock, pageable, MUNICIPALITY_ID);

		// Assertions and verifications
		assertThat(matches.getContent()).isEmpty();
		assertThat(matches.getNumberOfElements()).isZero();
		assertThat(matches.getTotalElements()).isZero();
		assertThat(matches.getTotalPages()).isZero();
		assertThat(matches.getPageable()).usingRecursiveComparison().isEqualTo(pageable);
		assertThat(matches.getSort()).usingRecursiveComparison().isEqualTo(sort);

		verify(billingRecordRepositoryMock).findAll(specificationMock, pageable);
		verify(specificationMock).and(ArgumentMatchers.<Specification<BillingRecordEntity>>any());
		verifyNoMoreInteractions(billingRecordRepositoryMock, specificationMock);
	}

	@Test
	void readExistingBillingRecord() {
		// Mock
		when(billingRecordRepositoryMock.existsByIdAndMunicipalityId(ID, MUNICIPALITY_ID)).thenReturn(true);
		when(billingRecordRepositoryMock.getReferenceByIdAndMunicipalityId(ID, MUNICIPALITY_ID)).thenReturn(createBillingRecordEntityInstance());

		// Call
		final var result = service.readBillingRecord(ID, MUNICIPALITY_ID);

		// Assertions and verifications
		assertThat(result.getId()).isEqualTo(ID);
		verify(billingRecordRepositoryMock).existsByIdAndMunicipalityId(ID, MUNICIPALITY_ID);
		verify(billingRecordRepositoryMock).getReferenceByIdAndMunicipalityId(ID, MUNICIPALITY_ID);
		verifyNoMoreInteractions(billingRecordRepositoryMock);
	}

	@Test
	void readNonExistingBillingRecord() {
		// Call
		final var exception = assertThrows(ThrowableProblem.class, () -> service.readBillingRecord(ID, MUNICIPALITY_ID));

		// Assertions and verifications
		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getTitle()).isEqualTo(NOT_FOUND.getReasonPhrase());
		assertThat(exception.getMessage()).isEqualTo((String.format("Not Found: A billing record with id '%s' and municipality ID '%s' could not be found!", ID, MUNICIPALITY_ID)));

		verify(billingRecordRepositoryMock).existsByIdAndMunicipalityId(ID, MUNICIPALITY_ID);
		verifyNoMoreInteractions(billingRecordRepositoryMock);
	}

	@Test
	void updateExistingBillingRecord() {
		// Setup
		final var entity = createBillingRecordEntityInstance();

		// Mock
		when(billingRecordRepositoryMock.existsByIdAndMunicipalityId(ID, MUNICIPALITY_ID)).thenReturn(true);
		when(billingRecordRepositoryMock.getReferenceByIdAndMunicipalityId(ID, MUNICIPALITY_ID)).thenReturn(entity);
		when(billingRecordRepositoryMock.save(entity)).thenReturn(entity);

		// Call
		final var response = service.updateBillingRecord(ID, createBillingRecordInstance(), MUNICIPALITY_ID);

		// Assertions and verifications
		assertThat(response.getId()).isEqualTo(ID);

		verify(billingRecordRepositoryMock).existsByIdAndMunicipalityId(ID, MUNICIPALITY_ID);
		verify(billingRecordRepositoryMock).getReferenceByIdAndMunicipalityId(ID, MUNICIPALITY_ID);
		verify(billingRecordRepositoryMock).save(entity);
		verifyNoMoreInteractions(billingRecordRepositoryMock);
	}

	@ParameterizedTest
	@MethodSource("modifiableStateArguments")
	void updateBillingRecordInModifiableState(final Status status, final LocalDate transferDate) {
		// Setup
		final var entity = createBillingRecordEntityInstance().withStatus(status).withTransferDate(transferDate);

		// Mock
		when(billingRecordRepositoryMock.existsByIdAndMunicipalityId(ID, MUNICIPALITY_ID)).thenReturn(true);
		when(billingRecordRepositoryMock.getReferenceByIdAndMunicipalityId(ID, MUNICIPALITY_ID)).thenReturn(entity);
		when(billingRecordRepositoryMock.save(entity)).thenReturn(entity);

		// Call
		final var response = service.updateBillingRecord(ID, createBillingRecordInstance(), MUNICIPALITY_ID);

		// Assertions and verifications
		assertThat(response.getId()).isEqualTo(ID);

		verify(billingRecordRepositoryMock).existsByIdAndMunicipalityId(ID, MUNICIPALITY_ID);
		verify(billingRecordRepositoryMock).getReferenceByIdAndMunicipalityId(ID, MUNICIPALITY_ID);
		verify(billingRecordRepositoryMock).save(entity);
		verifyNoMoreInteractions(billingRecordRepositoryMock);
	}

	@ParameterizedTest
	@MethodSource("nonModifiableStateArguments")
	void updateBillingRecordInNonModifiableState(final Status status, final LocalDate transferDate) {
		// Setup
		final var billingRecord = createBillingRecordInstance();

		// Mock
		when(billingRecordRepositoryMock.existsByIdAndMunicipalityId(ID, MUNICIPALITY_ID)).thenReturn(true);
		when(billingRecordRepositoryMock.getReferenceByIdAndMunicipalityId(ID, MUNICIPALITY_ID)).thenReturn(createBillingRecordEntityInstance().withStatus(status).withTransferDate(transferDate));

		// Call
		final var exception = assertThrows(ThrowableProblem.class, () -> service.updateBillingRecord(ID, billingRecord, MUNICIPALITY_ID));

		// Assertions and verifications
		assertThat(exception.getStatus()).isEqualTo(METHOD_NOT_ALLOWED);
		assertThat(exception.getTitle()).isEqualTo(METHOD_NOT_ALLOWED.getReasonPhrase());
		assertThat(exception.getMessage()).isEqualTo("Method Not Allowed: The billing record has status INVOICED or its transfer date has passed and is therefore not possible to update!");

		verify(billingRecordRepositoryMock).existsByIdAndMunicipalityId(ID, MUNICIPALITY_ID);
		verify(billingRecordRepositoryMock).getReferenceByIdAndMunicipalityId(ID, MUNICIPALITY_ID);
		verifyNoMoreInteractions(billingRecordRepositoryMock);
	}

	@Test
	void updateNonExistingBillingRecord() {
		// Setup
		final var billingRecord = BillingRecord.create();

		// Call
		final var exception = assertThrows(ThrowableProblem.class, () -> service.updateBillingRecord(ID, billingRecord, MUNICIPALITY_ID));

		// Assertions and verifications
		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getTitle()).isEqualTo(NOT_FOUND.getReasonPhrase());
		assertThat(exception.getMessage()).isEqualTo((String.format("Not Found: A billing record with id '%s' and municipality ID '%s' could not be found!", ID, MUNICIPALITY_ID)));

		verify(billingRecordRepositoryMock).existsByIdAndMunicipalityId(ID, MUNICIPALITY_ID);
		verifyNoMoreInteractions(billingRecordRepositoryMock);
	}

	@ParameterizedTest
	@MethodSource("modifiableStateArguments")
	void deleteBillingRecordInModifiableState(final Status status, final LocalDate transferDate) {
		// Mock
		when(billingRecordRepositoryMock.existsByIdAndMunicipalityId(ID, MUNICIPALITY_ID)).thenReturn(true);
		when(billingRecordRepositoryMock.getReferenceByIdAndMunicipalityId(ID, MUNICIPALITY_ID)).thenReturn(createBillingRecordEntityInstance().withStatus(status).withTransferDate(transferDate));

		// Call
		service.deleteBillingRecord(ID, MUNICIPALITY_ID);

		// Assertions and verifications
		verify(billingRecordRepositoryMock).existsByIdAndMunicipalityId(ID, MUNICIPALITY_ID);
		verify(billingRecordRepositoryMock).getReferenceByIdAndMunicipalityId(ID, MUNICIPALITY_ID);
		verify(billingRecordRepositoryMock).deleteByIdAndMunicipalityId(ID, MUNICIPALITY_ID);
		verifyNoMoreInteractions(billingRecordRepositoryMock);
	}

	@ParameterizedTest
	@MethodSource("nonModifiableStateArguments")
	void deleteBillingRecordInNonModifiableState(final Status status, final LocalDate transferDate) {
		// Mock
		when(billingRecordRepositoryMock.existsByIdAndMunicipalityId(ID, MUNICIPALITY_ID)).thenReturn(true);
		when(billingRecordRepositoryMock.getReferenceByIdAndMunicipalityId(ID, MUNICIPALITY_ID)).thenReturn(createBillingRecordEntityInstance().withStatus(status).withTransferDate(transferDate));

		// Call
		final var exception = assertThrows(ThrowableProblem.class, () -> service.deleteBillingRecord(ID, MUNICIPALITY_ID));

		// Assertions and verifications
		assertThat(exception.getStatus()).isEqualTo(METHOD_NOT_ALLOWED);
		assertThat(exception.getTitle()).isEqualTo(METHOD_NOT_ALLOWED.getReasonPhrase());
		assertThat(exception.getMessage()).isEqualTo("Method Not Allowed: The billing record has status INVOICED or its transfer date has passed and is therefore not possible to delete!");

		verify(billingRecordRepositoryMock).existsByIdAndMunicipalityId(ID, MUNICIPALITY_ID);
		verify(billingRecordRepositoryMock).getReferenceByIdAndMunicipalityId(ID, MUNICIPALITY_ID);
		verifyNoMoreInteractions(billingRecordRepositoryMock);
	}

	private static Stream<Arguments> modifiableStateArguments() {
		final var tomorrow = LocalDate.now(ZoneId.systemDefault()).plusDays(1);

		return Stream.of(
			Arguments.of(Status.NEW, tomorrow),
			Arguments.of(Status.APPROVED, tomorrow),
			Arguments.of(Status.REJECTED, tomorrow),
			Arguments.of(Status.NEW, null),
			Arguments.of(Status.APPROVED, null));
	}

	private static Stream<Arguments> nonModifiableStateArguments() {
		final var today = LocalDate.now(ZoneId.systemDefault());

		return Stream.of(
			Arguments.of(Status.INVOICED, today.plusDays(1)),
			Arguments.of(Status.INVOICED, null),
			Arguments.of(Status.NEW, today),
			Arguments.of(Status.APPROVED, today),
			Arguments.of(Status.APPROVED, today.minusDays(1)),
			Arguments.of(Status.REJECTED, today.minusDays(1)));
	}

	@Test
	void deleteNonExistingBillingRecord() {
		// Call
		final var exception = assertThrows(ThrowableProblem.class, () -> service.deleteBillingRecord(ID, MUNICIPALITY_ID));

		// Assertions and verifications
		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getTitle()).isEqualTo(NOT_FOUND.getReasonPhrase());
		assertThat(exception.getMessage()).isEqualTo(String.format("Not Found: A billing record with id '%s' and municipality ID '%s' could not be found!", ID, MUNICIPALITY_ID));

		verify(billingRecordRepositoryMock).existsByIdAndMunicipalityId(ID, MUNICIPALITY_ID);
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
