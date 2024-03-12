package se.sundsvall.billingpreprocessor.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zalando.problem.ThrowableProblem;

import se.sundsvall.billingpreprocessor.integration.db.InvoiceFileConfigurationRepository;
import se.sundsvall.billingpreprocessor.integration.db.model.InvoiceFileConfigurationEntity;

@ExtendWith(MockitoExtension.class)
class InvoiceFileConfigurationServiceTest {

	private static final long MOCKED_TIME_AS_EPOCH_TIMESTAMP = 1704110430000L; // 2024-01-01T13:00:15+01:00

	@Mock
	private InvoiceFileConfigurationRepository invoiceFileConfigurationRepositoryMock;

	@InjectMocks
	private InvoiceFileConfigurationService service;

	@BeforeEach
	void setup() {
		setField(service, "clock", Clock.fixed(Instant.ofEpochMilli(MOCKED_TIME_AS_EPOCH_TIMESTAMP), ZoneId.systemDefault()));
	}

	@Test
	void generateInvoiceFileNameForDatePattern() {

		// Arrange
		final var type = "SOME_TYPE";
		final var categoryTag = "SOME_CATEGORY";
		final var fileNamePattern = "FILENAME_{yyyyMMdd}";
		final var entity = InvoiceFileConfigurationEntity.create()
			.withType(type)
			.withCategoryTag(categoryTag)
			.withFileNamePattern(fileNamePattern);

		when(invoiceFileConfigurationRepositoryMock.findByTypeAndCategoryTag(any(), any())).thenReturn(Optional.of(entity));

		// Act
		final var result = service.getInvoiceFileNameBy(type, categoryTag);

		// Assert
		assertThat(result).isEqualTo("FILENAME_20240101");
		verify(invoiceFileConfigurationRepositoryMock).findByTypeAndCategoryTag(type, categoryTag);
		verifyNoMoreInteractions(invoiceFileConfigurationRepositoryMock);
	}

	@Test
	void generateInvoiceFileNameForDateTimePattern() {

		// Arrange
		final var type = "SOME_TYPE";
		final var categoryTag = "SOME_CATEGORY";
		final var fileNamePattern = "FILENAME_{yyyyMMddHHmmss}";
		final var entity = InvoiceFileConfigurationEntity.create()
			.withType(type)
			.withCategoryTag(categoryTag)
			.withFileNamePattern(fileNamePattern);

		when(invoiceFileConfigurationRepositoryMock.findByTypeAndCategoryTag(any(), any())).thenReturn(Optional.of(entity));

		// Act
		final var result = service.getInvoiceFileNameBy(type, categoryTag);

		// Assert
		assertThat(result).isEqualTo("FILENAME_20240101130030");
		verify(invoiceFileConfigurationRepositoryMock).findByTypeAndCategoryTag(type, categoryTag);
		verifyNoMoreInteractions(invoiceFileConfigurationRepositoryMock);
	}

	@Test
	void generateInvoiceFileNameWhenConfigEntityIsNotFound() {

		// Arrange
		final var type = "SOME_TYPE";
		final var categoryTag = "SOME_CATEGORY";

		when(invoiceFileConfigurationRepositoryMock.findByTypeAndCategoryTag(any(), any())).thenReturn(Optional.empty());

		// Act
		final var exception = assertThrows(ThrowableProblem.class, () -> service.getInvoiceFileNameBy(type, categoryTag));

		// Assert
		assertThat(exception).isNotNull();
		assertThat(exception.getMessage()).isEqualTo("Internal Server Error: No invoice file configuration found by type: 'SOME_TYPE' and categoryTag: 'SOME_CATEGORY'");
		verify(invoiceFileConfigurationRepositoryMock).findByTypeAndCategoryTag(type, categoryTag);
		verifyNoMoreInteractions(invoiceFileConfigurationRepositoryMock);
	}
}
