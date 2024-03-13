package se.sundsvall.billingpreprocessor.service;

import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.Status.APPROVED;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.Status.INVOICED;
import static se.sundsvall.billingpreprocessor.service.mapper.InvoiceFileMapper.toInvoiceFileEntity;
import static se.sundsvall.billingpreprocessor.service.util.ProblemUtil.createInternalServerErrorProblem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;

import jakarta.transaction.Transactional;
import se.sundsvall.billingpreprocessor.integration.db.BillingRecordRepository;
import se.sundsvall.billingpreprocessor.integration.db.InvoiceFileRepository;
import se.sundsvall.billingpreprocessor.integration.db.model.BillingRecordEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.enums.Type;
import se.sundsvall.billingpreprocessor.service.creator.InvoiceCreator;

@Service
public class InvoiceFileService {
	private static final String FILE_CREATION_ERROR = "%s occurred during creation of %s invoice billing file";
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
		final var billingRecords = billingRecordRepository.findAllByStatus(APPROVED);
		Stream.of(Type.values()).forEach(type -> {
			try {
				createFileEntity(billingRecords, type);
			} catch (Exception e) {
				LOG.error("{} occurred during creation of {} invoice billing file", e.getClass().getSimpleName(), type.name().toLowerCase(), e);
				sendErrorMail(FILE_CREATION_ERROR.formatted(e.getClass().getSimpleName(), type.name().toLowerCase()), e.getMessage());
			}
		});
	}

	private void createFileEntity(List<BillingRecordEntity> billingRecords, Type type) throws IOException {
		final var billingRecordsToProcess = filterByType(billingRecords, type);
		if (!billingRecordsToProcess.isEmpty()) {
			final Map<String, String> billingRecordErrors = new HashMap<>();
			try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
				final var invoiceCreator = getCreatorFor(type);
				outputStream.write(invoiceCreator.createFileHeader());
				billingRecordsToProcess.forEach(billingRecord -> createBillingRecord(outputStream, billingRecord, invoiceCreator).ifPresent(error -> {
					billingRecordErrors.put(billingRecord.getId(), error);
				}));

				if (billingRecordsToProcess.size() > billingRecordErrors.size()) { // At least one of the records should be successful for the file to be created
					final var filename = invoiceFileConfigurationService.getInvoiceFileNameBy(type.name(), billingRecordsToProcess.getFirst().getCategory());
					invoiceFileRepository.save(toInvoiceFileEntity(filename, type.name(), outputStream.toByteArray()));
				}
				checkAndSendErrorMail(billingRecordErrors);
			}
		}
	}

	private Optional<String> createBillingRecord(final ByteArrayOutputStream outputStream, BillingRecordEntity entity, InvoiceCreator invoiceCreator) {
		try {
			if (invoiceCreator.handledCategories().contains(entity.getCategory())) {
				outputStream.write(invoiceCreator.createInvoiceData(entity));
				billingRecordRepository.save(entity.withStatus(INVOICED));
				return Optional.empty();
			}
			throw Problem.valueOf(INTERNAL_SERVER_ERROR, "No creator is defined to handle type %s and category %s".formatted(entity.getType(), entity.getCategory()));
		} catch (Exception e) {
			LOG.warn("{} occurred when persisting record with id {} to file'", e.getClass().getSimpleName(), entity.getId(), e);
			return Optional.of(e.getMessage());
		}
	}

	private List<BillingRecordEntity> filterByType(List<BillingRecordEntity> entities, Type type) {
		return entities
			.stream()
			.filter(entity -> Objects.equals(type, entity.getType()))
			.toList();
	}

	private InvoiceCreator getCreatorFor(Type type) {
		return invoiceCreators.stream()
			.filter(c -> c.canHandle(type))
			.findFirst()
			.orElseThrow(createInternalServerErrorProblem("No creator is defined to handle type %s".formatted(type)));
	}

	private void checkAndSendErrorMail(Map<String, String> entitiesWithErrors) {
		if (!entitiesWithErrors.isEmpty()) {
			// TODO: Integrate with messaging and send email with problems that has occurred during file creation (task UF-7461)
		}
	}

	private void sendErrorMail(String subject, String body) {
		// TODO: Integrate with messaging and send error email (task UF-7461)
	}
}
