package se.sundsvall.billingpreprocessor.service;

import static se.sundsvall.billingpreprocessor.integration.db.model.enums.Status.APPROVED;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.Status.INVOICED;
import static se.sundsvall.billingpreprocessor.service.mapper.InvoiceFileMapper.toInvoiceFileEntity;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import se.sundsvall.billingpreprocessor.integration.db.BillingRecordRepository;
import se.sundsvall.billingpreprocessor.integration.db.InvoiceFileRepository;
import se.sundsvall.billingpreprocessor.integration.db.model.BillingRecordEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.enums.Type;
import se.sundsvall.billingpreprocessor.service.creator.CreationError;
import se.sundsvall.billingpreprocessor.service.creator.InvoiceCreator;

@Service
public class InvoiceFileService {
	private static final Logger LOG = LoggerFactory.getLogger(InvoiceFileService.class);

	private final BillingRecordRepository billingRecordRepository;
	private final InvoiceFileRepository invoiceFileRepository;
	private final List<InvoiceCreator> invoiceCreators;
	private final InvoiceFileConfigurationService invoiceFileConfigurationService;

	public InvoiceFileService(
		BillingRecordRepository billingRecordRepository,
		InvoiceFileRepository invoiceFileRepository,
		List<InvoiceCreator> invoiceCreators,
		InvoiceFileConfigurationService invoiceFileConfigurationService) {

		this.billingRecordRepository = billingRecordRepository;
		this.invoiceFileRepository = invoiceFileRepository;
		this.invoiceCreators = invoiceCreators;
		this.invoiceFileConfigurationService = invoiceFileConfigurationService;
	}

	@Transactional
	public void createFileEntities() {
		final var billingRecords = new ArrayList<>(billingRecordRepository.findAllByStatus(APPROVED));

		final var creationErrors = invoiceCreators.stream()
			.map(creator -> processBillingRecords(billingRecords, creator))
			.flatMap(List::stream)
			.toList();

		if (!creationErrors.isEmpty() || !billingRecords.isEmpty()) {
			sendErrorMail(creationErrors, billingRecords);
		}
	}

	private List<CreationError> processBillingRecords(List<BillingRecordEntity> billingRecords, InvoiceCreator invoiceCreator) {
		final var totalErrors = new ArrayList<CreationError>();

		invoiceCreator.getProcessableTypes()
			.forEach(type -> invoiceCreator.getProcessableCategories()
				.forEach(category -> totalErrors.addAll(createBillingFile(billingRecords, invoiceCreator, type, category))));

		return totalErrors;
	}

	private List<CreationError> createBillingFile(List<BillingRecordEntity> billingRecords, InvoiceCreator invoiceCreator, Type type, String category) {
		final var billingRecordsToProcess = filterByTypeAndCategory(billingRecords, type, category);
		final List<CreationError> billingRecordProcessErrors = new ArrayList<>();
		final List<CreationError> commonErrors = new ArrayList<>();

		if (!billingRecordsToProcess.isEmpty()) {
			billingRecords.removeAll(billingRecordsToProcess); // Remove processed records from the original list and send mejl if unprocessed records exists at end of execution

			try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
				final var filename = invoiceFileConfigurationService.getInvoiceFileNameBy(type.name(), category);

				outputStream.write(invoiceCreator.createFileHeader());

				billingRecordsToProcess.forEach(billingRecord ->
				createBillingRecord(outputStream, billingRecord, invoiceCreator)
					.ifPresent(billingRecordProcessErrors::add));

				if (billingRecordsToProcess.size() > billingRecordProcessErrors.size()) { // At least one of the records should be successful for the file to be created
					invoiceFileRepository.save(toInvoiceFileEntity(filename, type.name(), outputStream.toByteArray()));
				}

			} catch (Exception e) {
				LOG.error("Exception occurred during creation of {} invoice billing file for category {}", type.name().toLowerCase(), category, e);
				commonErrors.add(CreationError.create(e.getMessage()));
			}
		}

		return Stream.concat(commonErrors.stream(), billingRecordProcessErrors.stream()).toList();
	}

	private Optional<CreationError> createBillingRecord(final ByteArrayOutputStream outputStream, BillingRecordEntity entity, InvoiceCreator invoiceCreator) {
		try {
			outputStream.write(invoiceCreator.createInvoiceData(entity));
			billingRecordRepository.save(entity.withStatus(INVOICED));
			return Optional.empty();
		} catch (Exception e) {
			LOG.warn("{} occurred when persisting record with id {} to file'", e.getClass().getSimpleName(), entity.getId(), e);
			return Optional.of(CreationError.create(entity.getId(), e.getMessage()));
		}
	}

	private List<BillingRecordEntity> filterByTypeAndCategory(List<BillingRecordEntity> entities, Type type, String category) {
		return entities
			.stream()
			.filter(entity -> Objects.equals(type, entity.getType()))
			.filter(entity -> Objects.equals(category, entity.getCategory()))
			.toList();
	}

	private void sendErrorMail(List<CreationError> creationErrors, List<BillingRecordEntity> unprocessedRecords) {
		// TODO: Integrate with messaging and send error email (task UF-7461)
		creationErrors.forEach(error -> LOG.warn(error.isCommonError() ? error.getMessage() : "Record with id %s couldn't be processed. Message is '%s'.".formatted(error.getEntityId(), error.getMessage())));
		unprocessedRecords.forEach(billingRecord -> LOG.warn("Record with id %s of type: '%s' and category: '%s' couldn't be processed as no corresponding invoice creator implementation exists.".formatted(
			billingRecord.getId(),
			billingRecord.getType(),
			billingRecord.getCategory())));
	}
}
