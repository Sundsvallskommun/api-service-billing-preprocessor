package se.sundsvall.billingpreprocessor.service;

import java.time.Month;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;
import org.springframework.stereotype.Service;
import se.sundsvall.billingpreprocessor.api.model.InvoiceFileStatus;
import se.sundsvall.billingpreprocessor.integration.db.InvoiceFileRepository;

@Service
public class StatusService {

	private final InvoiceFileRepository invoiceFileRepository;

	public StatusService(final InvoiceFileRepository invoiceFileRepository) {
		this.invoiceFileRepository = invoiceFileRepository;
	}

	public List<InvoiceFileStatus> getInvoiceFilesForMonth(final String municipalityId, final Integer year, final Month month) {
		var yearMonth = YearMonth.of(year, month);

		var start = yearMonth.atDay(1)
			.atStartOfDay(ZoneId.systemDefault())
			.toOffsetDateTime();
		var end = start.plusMonths(1);

		return invoiceFileRepository.findAllCreatedInMonth(municipalityId, start, end).stream()
			.map(invoiceFile -> new InvoiceFileStatus(String.valueOf(invoiceFile.getId()), invoiceFile.getName(), invoiceFile.getType(),
				invoiceFile.getStatus().name(), invoiceFile.getMunicipalityId(),
				invoiceFile.getCreated(), invoiceFile.getSent()))
			.toList();
	}
}
