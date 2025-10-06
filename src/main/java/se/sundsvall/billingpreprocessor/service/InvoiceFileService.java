package se.sundsvall.billingpreprocessor.service;

import static org.zalando.problem.Status.BAD_REQUEST;
import static se.sundsvall.billingpreprocessor.Constants.ERROR_INVOICE_FILE_GENERATION_FAILURE;
import static se.sundsvall.billingpreprocessor.Constants.ERROR_INVOICE_FILE_TRANSFER_FAILURE;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.InvoiceFileStatus.GENERATED;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.InvoiceFileStatus.SEND_FAILED;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.InvoiceFileStatus.SEND_SUCCESSFUL;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.Status.APPROVED;
import static se.sundsvall.billingpreprocessor.integration.db.model.enums.Status.INVOICED;
import static se.sundsvall.billingpreprocessor.service.mapper.InvoiceFileMapper.toInvoiceFileEntity;
import static se.sundsvall.dept44.util.LogUtils.sanitizeForLogging;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.integration.file.remote.session.DelegatingSessionFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.problem.Problem;
import se.sundsvall.billingpreprocessor.integration.db.BillingRecordRepository;
import se.sundsvall.billingpreprocessor.integration.db.InvoiceFileRepository;
import se.sundsvall.billingpreprocessor.integration.db.model.BillingRecordEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.InvoiceFileEntity;
import se.sundsvall.billingpreprocessor.integration.db.model.enums.Type;
import se.sundsvall.billingpreprocessor.integration.sftp.SftpConfiguration.UploadGateway;
import se.sundsvall.billingpreprocessor.integration.sftp.SftpPropertiesConfig;
import se.sundsvall.billingpreprocessor.service.creator.InvoiceCreator;
import se.sundsvall.billingpreprocessor.service.error.InvoiceFileError;

@Service
public class InvoiceFileService {
	private static final Logger LOG = LoggerFactory.getLogger(InvoiceFileService.class);

	private final BillingRecordRepository billingRecordRepository;
	private final InvoiceFileRepository invoiceFileRepository;
	private final List<InvoiceCreator> invoiceCreators;
	private final InvoiceFileConfigurationService invoiceFileConfigurationService;
	private final MessagingService messagingService;
	private final UploadGateway uploadGateway;
	private final DelegatingSessionFactory<?> sessionFactory;
	private final SftpPropertiesConfig sftpPropertiesConfig;

	public InvoiceFileService(
		BillingRecordRepository billingRecordRepository,
		InvoiceFileRepository invoiceFileRepository,
		List<InvoiceCreator> invoiceCreators,
		InvoiceFileConfigurationService invoiceFileConfigurationService,
		MessagingService messagingService,
		UploadGateway uploadGateway,
		DelegatingSessionFactory<?> sessionFactory,
		SftpPropertiesConfig sftpPropertiesConfig) {

		this.billingRecordRepository = billingRecordRepository;
		this.invoiceFileRepository = invoiceFileRepository;
		this.invoiceCreators = invoiceCreators;
		this.invoiceFileConfigurationService = invoiceFileConfigurationService;
		this.messagingService = messagingService;
		this.uploadGateway = uploadGateway;
		this.sessionFactory = sessionFactory;
		this.sftpPropertiesConfig = sftpPropertiesConfig;
	}

	public void transferFiles(String municipalityId) {
		if (!sftpPropertiesConfig.getMap().containsKey(municipalityId)) {
			throw Problem.valueOf(BAD_REQUEST, String.format("File transfer for municipality id '%s' is not configured!", municipalityId));
		}
		try {
			final List<InvoiceFileError> errors = new ArrayList<>();
			sessionFactory.setThreadKey(municipalityId);
			invoiceFileRepository.findByStatusInAndMunicipalityId(List.of(GENERATED, SEND_FAILED), municipalityId)
				.forEach(fileEntity -> this.transferFile(fileEntity, sftpPropertiesConfig.getMap().get(municipalityId).getRemoteDir()).ifPresent(errors::add));

			if (!errors.isEmpty()) {
				messagingService.sendTransferErrorMail(municipalityId, errors);
			}
		} finally {
			sessionFactory.clearThreadKey();
		}
	}

	private Optional<InvoiceFileError> transferFile(InvoiceFileEntity fileEntity, String remoteDir) {
		try {
			Charset encoding = Charset.forName(fileEntity.getEncoding());

			LOG.info("Starting to transfer file '{}' with encoding '{}' to remote dir '{}'", fileEntity.getName(), encoding, remoteDir);
			uploadGateway.sendToSftp(new ByteArrayResource(fileEntity.getContent().getBytes(encoding)), fileEntity.getName(), remoteDir);
			invoiceFileRepository.save(fileEntity
				.withSent(OffsetDateTime.now())
				.withStatus(SEND_SUCCESSFUL));

			return Optional.empty();

		} catch (Exception e) {
			LOG.error("{} occurred while transferring file {} to ftp.", e.getClass().getSimpleName(), fileEntity.getName(), e);
			invoiceFileRepository.save(fileEntity.withStatus(SEND_FAILED));
			return Optional.of(InvoiceFileError.create(ERROR_INVOICE_FILE_TRANSFER_FAILURE.formatted(fileEntity.getName(), e.getClass().getSimpleName() + ": " + e.getMessage())));
		}
	}

	@Transactional
	public void createFiles(String municipalityId) {
		final var billingRecords = new ArrayList<>(billingRecordRepository.findAllByStatusAndMunicipalityId(APPROVED, municipalityId));

		final var creationErrors = invoiceCreators.stream()
			.map(creator -> processBillingRecords(billingRecords, creator, municipalityId))
			.flatMap(List::stream)
			.toList();

		if (!creationErrors.isEmpty() || !billingRecords.isEmpty()) {
			sendCreationErrorMail(creationErrors, billingRecords, municipalityId);
		}
	}

	private List<InvoiceFileError> processBillingRecords(List<BillingRecordEntity> billingRecords, InvoiceCreator invoiceCreator, String municipalityId) {
		final List<InvoiceFileError> billingRecordProcessErrors = new ArrayList<>();
		final List<InvoiceFileError> commonErrors = new ArrayList<>();

		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			final var type = invoiceCreator.getProcessableType();
			final var category = invoiceCreator.getProcessableCategory();

			LOG.info("Processing type '{}' and category '{}'", type, category);

			final var billingRecordsToProcess = filterByTypeAndCategory(billingRecords, type, category);
			if (!billingRecordsToProcess.isEmpty()) {
				billingRecords.removeAll(billingRecordsToProcess); // Remove processed records from the original list and send mail if unprocessed records exists at end of execution

				final var filename = invoiceFileConfigurationService.getInvoiceFileNameBy(type.name(), category);
				final var encoding = invoiceFileConfigurationService.getEncoding(type.name(), category);

				outputStream.write(invoiceCreator.createFileHeader());

				billingRecordsToProcess.forEach(billingRecord -> createBillingRecord(outputStream, billingRecord, invoiceCreator)
					.ifPresent(billingRecordProcessErrors::add));

				outputStream.write(invoiceCreator.createFileFooter(billingRecordsToProcess));

				if (billingRecordsToProcess.size() > billingRecordProcessErrors.size()) { // At least one of the records should be successful for the file to be created
					LOG.info("Saving file '{}' with {} successfully processed records for municipality id '{}'",
						filename, (billingRecordsToProcess.size() - billingRecordProcessErrors.size()), sanitizeForLogging(municipalityId));
					invoiceFileRepository.save(toInvoiceFileEntity(filename, type.name(), outputStream.toByteArray(), encoding, municipalityId));
				}
			}

		} catch (Exception e) {
			LOG.error("Exception occurred during creation of invoice billing file", e);
			commonErrors.add(InvoiceFileError.create(ERROR_INVOICE_FILE_GENERATION_FAILURE.formatted(e.getClass().getSimpleName() + ": " + e.getMessage())));
		}

		return Stream.concat(commonErrors.stream(), billingRecordProcessErrors.stream()).toList();
	}

	private Optional<InvoiceFileError> createBillingRecord(final ByteArrayOutputStream outputStream, BillingRecordEntity entity, InvoiceCreator invoiceCreator) {
		try {
			outputStream.write(invoiceCreator.createInvoiceData(entity));
			billingRecordRepository.save(entity.withStatus(INVOICED));
			return Optional.empty();
		} catch (Exception e) {
			LOG.warn("{} occurred when persisting record with id {} to file'", e.getClass().getSimpleName(), entity.getId(), e);
			return Optional.of(InvoiceFileError.create(entity.getId(), e.getMessage()));
		}
	}

	private List<BillingRecordEntity> filterByTypeAndCategory(List<BillingRecordEntity> entities, Type type, String category) {
		return entities
			.stream()
			.filter(entity -> Objects.equals(type, entity.getType()))
			.filter(entity -> Objects.equals(category, entity.getCategory()))
			.toList();
	}

	private void sendCreationErrorMail(List<InvoiceFileError> creationErrors, List<BillingRecordEntity> unprocessedRecords, String municipalityId) {
		final var allErrors = new ArrayList<>(creationErrors);
		allErrors.addAll(unprocessedRecords.stream()
			.map(billingRecord -> InvoiceFileError.create(
				billingRecord.getId(),
				"No corresponding invoice creator implementation could be found for type: '%s' and category: '%s'".formatted(
					billingRecord.getType(),
					billingRecord.getCategory())))
			.toList());

		allErrors.forEach(error -> LOG.warn(error.isCommonError() ? error.getMessage() : "Record with id {} couldn't be processed. Message is '{}'.", error.getEntityId(), error.getMessage()));

		messagingService.sendCreationErrorMail(municipalityId, allErrors);
	}
}
