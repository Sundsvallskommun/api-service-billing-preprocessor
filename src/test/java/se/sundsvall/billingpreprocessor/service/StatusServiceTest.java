package se.sundsvall.billingpreprocessor.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.Month;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.billingpreprocessor.integration.db.InvoiceFileRepository;
import se.sundsvall.billingpreprocessor.integration.db.model.InvoiceFileEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.enums.InvoiceFileStatus;

@ExtendWith(MockitoExtension.class)
class StatusServiceTest {

	@Mock
	private InvoiceFileRepository invoiceFileRepositoryMock;

	@InjectMocks
	private StatusService statusService;

	@AfterEach
	void tearDown() {
		verifyNoMoreInteractions(invoiceFileRepositoryMock);
	}

	@Test
	void getInvoiceFilesForMonth() {
		var municipalityId = "municipalityId";
		var year = 2024;
		var month = Month.JUNE;

		var created = OffsetDateTime.now();
		var sent = OffsetDateTime.now();

		var invoiceFile = new InvoiceFileEntity()
			.withCreated(created)
			.withSent(sent)
			.withName("name")
			.withMunicipalityId(municipalityId)
			.withStatus(InvoiceFileStatus.SEND_SUCCESSFUL)
			.withType("EXTERNAL");

		when(invoiceFileRepositoryMock.findAllCreatedInMonth(eq(municipalityId), any(OffsetDateTime.class), any(OffsetDateTime.class)))
			.thenReturn(List.of(invoiceFile));

		var result = statusService.getInvoiceFilesForMonth(municipalityId, year, month);

		assertThat(result).isNotNull().hasSize(1).allSatisfy(fileStatus -> {
			assertThat(fileStatus.status()).isEqualTo(InvoiceFileStatus.SEND_SUCCESSFUL.toString());
			assertThat(fileStatus.municipalityId()).isEqualTo(municipalityId);
			assertThat(fileStatus.name()).isEqualTo("name");
			assertThat(fileStatus.type()).isEqualTo("EXTERNAL");
			assertThat(fileStatus.createdAt()).isEqualTo(created);
			assertThat(fileStatus.sentAt()).isEqualTo(sent);
		});

		verify(invoiceFileRepositoryMock).findAllCreatedInMonth(eq(municipalityId), any(OffsetDateTime.class), any(OffsetDateTime.class));
	}

}
