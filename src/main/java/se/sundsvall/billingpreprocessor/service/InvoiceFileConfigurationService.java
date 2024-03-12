package se.sundsvall.billingpreprocessor.service;

import static java.time.Instant.now;
import static java.util.Objects.isNull;
import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;
import static se.sundsvall.billingpreprocessor.Constants.ERROR_INVOICE_FILE_NAME_GENERATION_FAILURE;
import static se.sundsvall.billingpreprocessor.Constants.ERROR_NO_INVOICE_FILE_CONFIGURATION_FOUND;

import java.text.SimpleDateFormat;
import java.time.Clock;
import java.util.Date;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;

import se.sundsvall.billingpreprocessor.integration.db.InvoiceFileConfigurationRepository;
import se.sundsvall.billingpreprocessor.integration.db.model.InvoiceFileConfigurationEntity;

@Service
public class InvoiceFileConfigurationService {

	private final Clock clock;
	private final InvoiceFileConfigurationRepository invoiceFileConfigurationRepository;

	public InvoiceFileConfigurationService(InvoiceFileConfigurationRepository invoiceFileConfigurationRepository) {
		this.invoiceFileConfigurationRepository = invoiceFileConfigurationRepository;
		this.clock = Clock.systemDefaultZone();
	}

	public String getInvoiceFileNameBy(String type, String categoryTag) {
		final var fileNamePattern = getInvoiceFileConfigurationBy(type, categoryTag).getFileNamePattern();
		return applyDatePattern(fileNamePattern);
	}

	private InvoiceFileConfigurationEntity getInvoiceFileConfigurationBy(String type, String categoryTag) {
		return invoiceFileConfigurationRepository.findByTypeAndCategoryTag(type, categoryTag)
			.orElseThrow(() -> Problem.valueOf(INTERNAL_SERVER_ERROR, ERROR_NO_INVOICE_FILE_CONFIGURATION_FOUND.formatted(type, categoryTag)));
	}

	private String applyDatePattern(String template) {

		if (isNull(template)) {
			throw Problem.valueOf(INTERNAL_SERVER_ERROR, ERROR_INVOICE_FILE_NAME_GENERATION_FAILURE.formatted("null"));
		}

		final var pattern = Pattern.compile("\\{[^\\}]*\\}");
		final var matcher = pattern.matcher(template);

		if (matcher.find()) {

			final var patternMatch = matcher.group();
			final var datePattern = patternMatch.replaceAll("[{}]", "");

			final var formattedDateString = new SimpleDateFormat(datePattern).format(Date.from(now(clock)));
			return template.replace(patternMatch, formattedDateString);
		}

		throw Problem.valueOf(INTERNAL_SERVER_ERROR, ERROR_INVOICE_FILE_NAME_GENERATION_FAILURE.formatted(template));
	}
}
