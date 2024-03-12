package se.sundsvall.billingpreprocessor.service;

import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.Status.APPROVED;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.Status.INVOICED;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.Type.EXTERNAL;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.Type.INTERNAL;
import static se.sundsvall.billingpreprocessor.service.mapper.InvoiceFileMapper.toInvoiceFileEntity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;
import org.zalando.problem.ThrowableProblem;

import jakarta.transaction.Transactional;
import se.sundsvall.billingpreprocessor.integration.db.BillingRecordRepository;
import se.sundsvall.billingpreprocessor.integration.db.InvoiceFileRepository;
import se.sundsvall.billingpreprocessor.integration.db.model.BillingRecordEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.enums.Type;
import se.sundsvall.billingpreprocessor.service.creator.ExternalInvoiceCreator;
import se.sundsvall.billingpreprocessor.service.creator.InternalInvoiceCreator;
import se.sundsvall.billingpreprocessor.service.creator.InvoiceCreator;

@Service
public class InvoiceFileService {
	private static final String FILE_CREATION_ERROR = "%s occurred during creation of billing file";
	private static final Logger LOG = LoggerFactory.getLogger(InvoiceFileService.class);

	private final BillingRecordRepository billingRecordRepository;
	private final InvoiceFileRepository invoiceFileRepository;
	private final ExternalInvoiceCreator externalInvoiceCreator;
	private final InternalInvoiceCreator internalInvoiceCreator;

	public InvoiceFileService(
		BillingRecordRepository billingRecordRepository,
		InvoiceFileRepository invoiceFileRepository,
		ExternalInvoiceCreator externalInvoiceCreator,
		InternalInvoiceCreator internalInvoiceCreator) {

		this.billingRecordRepository = billingRecordRepository;
		this.invoiceFileRepository = invoiceFileRepository;
		this.externalInvoiceCreator = externalInvoiceCreator;
		this.internalInvoiceCreator = internalInvoiceCreator;
	}

	@Transactional
	public void createFileEntities() {
		final var billingRecords = billingRecordRepository.findAllByStatus(APPROVED);
		try {
			createFileEntity(billingRecords, EXTERNAL, externalInvoiceCreator);
			createFileEntity(billingRecords, INTERNAL, internalInvoiceCreator);
		} catch (IOException e) {
			throw Problem.valueOf(INTERNAL_SERVER_ERROR, FILE_CREATION_ERROR.formatted(e.getClass().getSimpleName()));
		}
	}

	private void createFileEntity(List<BillingRecordEntity> billingRecords, Type type, InvoiceCreator invoiceCreator) throws IOException {
		final var entitiesToProcess = filterByType(billingRecords, type);
		if (!entitiesToProcess.isEmpty()) {
			final Map<String, String> errors = new HashMap<>();
			try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
				outputStream.write(invoiceCreator.createFileHeader());
				entitiesToProcess.forEach(entity -> createExternalBillingRecord(outputStream, entity, invoiceCreator).ifPresent(error -> {
					errors.put(entity.getId(), error);
				}));

				invoiceFileRepository.save(toInvoiceFileEntity(type.name() + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), type.name(), outputStream.toByteArray())); // TODO: Change to correct name when UF-7423 is done
				alertErrors(errors);
			}
		}
	}

	private Optional<String> createExternalBillingRecord(final ByteArrayOutputStream outputStream, BillingRecordEntity entity, InvoiceCreator invoiceCreator) {
		try {
			outputStream.write(invoiceCreator.createInvoiceData(entity));
			billingRecordRepository.save(entity.withStatus(INVOICED));
			return Optional.empty();
		} catch (IOException | ThrowableProblem e) {
			LOG.warn("{} occurred when persisting record with id %s to file. Message is '{}'", e.getClass().getSimpleName(), e.getMessage());
			return Optional.of(e.getMessage());
		}
	}

	private List<BillingRecordEntity> filterByType(List<BillingRecordEntity> entities, Type type) {
		return entities
			.stream()
			.filter(entity -> Objects.equals(type, entity.getType()))
			.toList();
	}

	private void alertErrors(Map<String, String> entitiesWithErrors) {
		if (!entitiesWithErrors.isEmpty()) {
			// TODO: Integrate with messaging and send email regarding the problems that has occurred during file creation?
		}
	}
}
