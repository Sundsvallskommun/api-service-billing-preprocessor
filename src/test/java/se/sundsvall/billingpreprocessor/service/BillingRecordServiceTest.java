package se.sundsvall.billingpreprocessor.service;

import static java.util.Collections.emptyList;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.data.domain.Sort.Direction.DESC;
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

import com.turkraft.springfilter.boot.FilterSpecification;

import se.sundsvall.billingpreprocessor.api.model.AccountInformation;
import se.sundsvall.billingpreprocessor.api.model.BillingRecord;
import se.sundsvall.billingpreprocessor.api.model.Invoice;
import se.sundsvall.billingpreprocessor.api.model.InvoiceRow;
import se.sundsvall.billingpreprocessor.api.model.enums.Status;
import se.sundsvall.billingpreprocessor.integration.db.BillingRecordRepository;
import se.sundsvall.billingpreprocessor.integration.db.model.BillingRecordEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.InvoiceEntity;

@ExtendWith(MockitoExtension.class)
class BillingRecordServiceTest {
	private final static String ID = randomUUID().toString();

	@Mock
	private BillingRecordRepository repositoryMock;

	@InjectMocks
	private BillingRecordService service;

	@Test
	void createBillingRecord() {
		// Setup
		final var record = createBillingRecordInstance();

		// Mock
		when(repositoryMock.save(any(BillingRecordEntity.class))).thenReturn(BillingRecordEntity.create().withId(ID));

		// Call
		final var result = service.createBillingRecord(record);

		// Assertions and verifications
		assertThat(result).isEqualTo(ID);
		verify(repositoryMock).save(any(BillingRecordEntity.class));
		verifyNoMoreInteractions(repositoryMock);
	}

	@Test
	void findBillingRecordsWithMatches() {
		// Setup
		final Specification<BillingRecordEntity> filter = new FilterSpecification<>("id: '" + ID + "'");
		final var sort = Sort.by(DESC, "attribute.1", "attribute.2");
		final Pageable pageable = PageRequest.of(1, 2, sort);

		// Mock
		when(repositoryMock.findAll(filter, pageable)).thenReturn(new PageImpl<>(List.of(createBillingRecordEntityInstance(), createBillingRecordEntityInstance()), pageable, 2L));
		when(repositoryMock.count(filter)).thenReturn(10L);

		// Call
		final var matches = service.findBillingIRecords(filter, pageable);

		// Assertions and verifications
		assertThat(matches.getContent()).isNotEmpty().hasSize(2);
		assertThat(matches.getNumberOfElements()).isEqualTo(2);
		assertThat(matches.getTotalElements()).isEqualTo(10);
		assertThat(matches.getTotalPages()).isEqualTo(5);
		assertThat(matches.getPageable()).usingRecursiveComparison().isEqualTo(pageable);
		assertThat(matches.getSort()).usingRecursiveComparison().isEqualTo(sort);

		verify(repositoryMock).findAll(filter, pageable);
		verify(repositoryMock).count(filter);
		verifyNoMoreInteractions(repositoryMock);
	}

	@Test
	void findBillingRecordsWithoutMatches() {
		// Setup
		final Specification<BillingRecordEntity> filter = new FilterSpecification<>("id: '" + ID + "'");
		final var sort = Sort.by(DESC, "attribute.1", "attribute.2");
		final Pageable pageable = PageRequest.of(3, 7, sort);

		// Mock
		when(repositoryMock.findAll(filter, pageable)).thenReturn(new PageImpl<>(emptyList()));

		// Call
		final var matches = service.findBillingIRecords(filter, pageable);

		// Assertions and verifications
		assertThat(matches.getContent()).isEmpty();
		assertThat(matches.getNumberOfElements()).isZero();
		assertThat(matches.getTotalElements()).isZero();
		assertThat(matches.getTotalPages()).isZero();
		assertThat(matches.getPageable()).usingRecursiveComparison().isEqualTo(pageable);
		assertThat(matches.getSort()).usingRecursiveComparison().isEqualTo(sort);

		verify(repositoryMock).findAll(filter, pageable);
		verify(repositoryMock).count(filter);
		verifyNoMoreInteractions(repositoryMock);
	}

	@Test
	void readExistingBillingRecord() {
		// Mock
		when(repositoryMock.existsById(ID)).thenReturn(true);
		when(repositoryMock.getReferenceById(ID)).thenReturn(createBillingRecordEntityInstance());

		// Call
		final var result = service.readBillingRecord(ID);

		// Assertions and verifications
		assertThat(result.getId()).isEqualTo(ID);
		verify(repositoryMock).existsById(ID);
		verify(repositoryMock).getReferenceById(ID);
		verifyNoMoreInteractions(repositoryMock);
	}

	@Test
	void readNonExistingBillingRecord() {
		// Call
		final var exception = assertThrows(ThrowableProblem.class, () -> service.readBillingRecord(ID));

		// Assertions and verifications
		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getTitle()).isEqualTo(NOT_FOUND.getReasonPhrase());
		assertThat(exception.getMessage()).isEqualTo("Not Found: A billing record with id " + ID + " could not be found");

		verify(repositoryMock).existsById(ID);
		verifyNoMoreInteractions(repositoryMock);
	}

	@Test
	void updateExistingBillingRecord() {
		// Setup
		final var entity = createBillingRecordEntityInstance();

		// Mock
		when(repositoryMock.existsById(ID)).thenReturn(true);
		when(repositoryMock.getReferenceById(ID)).thenReturn(entity);
		when(repositoryMock.save(entity)).thenReturn(entity);

		// Call
		final var response = service.updateBillingRecord(ID, createBillingRecordInstance());

		// Assertions and verifications
		assertThat(response.getId()).isEqualTo(ID);

		verify(repositoryMock).existsById(ID);
		verify(repositoryMock).getReferenceById(ID);
		verify(repositoryMock).save(entity);
		verifyNoMoreInteractions(repositoryMock);
	}

	@Test
	void updateNonExistingBillingRecord() {
		// Setup
		final var record = BillingRecord.create();

		// Call
		final var exception = assertThrows(ThrowableProblem.class, () -> service.updateBillingRecord(ID, record));

		// Assertions and verifications
		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getTitle()).isEqualTo(NOT_FOUND.getReasonPhrase());
		assertThat(exception.getMessage()).isEqualTo("Not Found: A billing record with id " + ID + " could not be found");

		verify(repositoryMock).existsById(ID);
		verifyNoMoreInteractions(repositoryMock);
	}

	@Test
	void deleteBillingRecordWithDeletableStatus() {
		// Mock
		when(repositoryMock.existsById(ID)).thenReturn(true);
		when(repositoryMock.getReferenceById(ID)).thenReturn(createBillingRecordEntityInstance().withStatus(NEW));

		// Call
		service.deleteBillingRecord(ID);

		// Assertions and verifications
		verify(repositoryMock).existsById(ID);
		verify(repositoryMock).getReferenceById(ID);
		verify(repositoryMock).deleteById(ID);
		verifyNoMoreInteractions(repositoryMock);
	}

	@ParameterizedTest
	@EnumSource(value = Status.class, names = "NEW", mode = EXCLUDE)
	void deleteBillingRecordWithNonDeletableStatus(Status status) {
		// Mock
		when(repositoryMock.existsById(ID)).thenReturn(true);
		when(repositoryMock.getReferenceById(ID)).thenReturn(createBillingRecordEntityInstance().withStatus(status));

		// Call
		final var exception = assertThrows(ThrowableProblem.class, () -> service.deleteBillingRecord(ID));

		// Assertions and verifications
		assertThat(exception.getStatus()).isEqualTo(METHOD_NOT_ALLOWED);
		assertThat(exception.getTitle()).isEqualTo(METHOD_NOT_ALLOWED.getReasonPhrase());
		assertThat(exception.getMessage()).isEqualTo("Method Not Allowed: The billing record does not have status NEW and is therefore not possible to delete");

		verify(repositoryMock).existsById(ID);
		verifyNoMoreInteractions(repositoryMock);
	}

	@Test
	void deleteNonExistingBillingRecord() {
		// Call
		final var exception = assertThrows(ThrowableProblem.class, () -> service.deleteBillingRecord(ID));

		// Assertions and verifications
		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getTitle()).isEqualTo(NOT_FOUND.getReasonPhrase());
		assertThat(exception.getMessage()).isEqualTo("Not Found: A billing record with id " + ID + " could not be found");

		verify(repositoryMock).existsById(ID);
		verifyNoMoreInteractions(repositoryMock);
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
				.withDescriptions(List.of("Beställare: joh01doe 22940338"))
				.withQuantity(0),
			InvoiceRow.create()
				.withDescriptions(List.of("Användare: Johan Doe joh01doe"))
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
			.withSubaccount("936100");
	}

	private static BillingRecordEntity createBillingRecordEntityInstance() {
		return BillingRecordEntity.create().withId(ID).withInvoice(InvoiceEntity.create());
	}
}
