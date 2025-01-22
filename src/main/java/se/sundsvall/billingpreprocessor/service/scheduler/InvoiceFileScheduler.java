package se.sundsvall.billingpreprocessor.service.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import se.sundsvall.billingpreprocessor.integration.sftp.SftpPropertiesConfig;
import se.sundsvall.billingpreprocessor.service.InvoiceFileService;
import se.sundsvall.dept44.requestid.RequestId;
import se.sundsvall.dept44.scheduling.Dept44Scheduled;

@Component
public class InvoiceFileScheduler {

	private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceFileScheduler.class);
	private static final String LOG_CREATE_FILES_STARTED = "Beginning creation of invoice files for municipality id {}";
	private static final String LOG_CREATE_FILES_ENDED = "Ending creation of invoice files for municipality id {}";
	private static final String LOG_TRANSFER_FILES_STARTED = "Beginning transfer of invoice files for municipality id {}";
	private static final String LOG_TRANSFER_FILES_ENDED = "Ending transfer of invoice files for municipality id {}";

	private final InvoiceFileService invoiceFileService;
	private final SftpPropertiesConfig sftpPropertiesConfig;

	public InvoiceFileScheduler(final InvoiceFileService invoiceFileService, final SftpPropertiesConfig sftpPropertiesConfig) {
		this.invoiceFileService = invoiceFileService;
		this.sftpPropertiesConfig = sftpPropertiesConfig;
	}

	@Dept44Scheduled(
		cron = "${scheduler.createfiles.cron}",
		name = "${scheduler.createfiles.name}",
		lockAtMostFor = "${scheduler.shedlock-lock-at-most-for}",
		maximumExecutionTime = "${scheduler.maximum-execution-time}")
	public void executeCreateFiles() {
		// Until separation of cron jobs per municipality is needed all are scheduled with same interval
		for (final String municipalityId : sftpPropertiesConfig.getMap().keySet()) {
			RequestId.init();
			LOGGER.info(LOG_CREATE_FILES_STARTED, municipalityId);
			invoiceFileService.createFiles(municipalityId);
			LOGGER.info(LOG_CREATE_FILES_ENDED, municipalityId);
			RequestId.reset();
		}
	}

	@Dept44Scheduled(
		cron = "${scheduler.transferfiles.cron}",
		name = "${scheduler.transferfiles.name}",
		lockAtMostFor = "${scheduler.shedlock-lock-at-most-for}",
		maximumExecutionTime = "${scheduler.maximum-execution-time}")
	public void executeTransferFiles() {
		// Until separation of cron jobs per municipality is needed all are scheduled with same interval
		for (final String municipalityId : sftpPropertiesConfig.getMap().keySet()) {
			RequestId.init();
			LOGGER.info(LOG_TRANSFER_FILES_STARTED, municipalityId);
			invoiceFileService.transferFiles(municipalityId);
			LOGGER.info(LOG_TRANSFER_FILES_ENDED, municipalityId);
			RequestId.reset();
		}
	}
}
