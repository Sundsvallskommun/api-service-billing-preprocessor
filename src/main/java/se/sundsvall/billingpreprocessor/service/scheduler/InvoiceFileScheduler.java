package se.sundsvall.billingpreprocessor.service.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import se.sundsvall.billingpreprocessor.service.InvoiceFileService;
import se.sundsvall.dept44.requestid.RequestId;

@Component
public class InvoiceFileScheduler {

	private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceFileScheduler.class);
	private static final String LOG_CREATE_FILES_STARTED = "Beginning creation of invoice files";
	private static final String LOG_CREATE_FILES_ENDED = "Ending creation of invoice files";
	private static final String LOG_SEND_TO_FTP_STARTED = "Beginning sending invoice files to ftp";
	private static final String LOG_SEND_TO_FTP_ENDED = "Ending sending invoice files to ftp";

	private final InvoiceFileService invoiceFileService;

	public InvoiceFileScheduler(final InvoiceFileService invoiceFileService) {
		this.invoiceFileService = invoiceFileService;
	}

	@Scheduled(cron = "${scheduler.createfiles.cron}")
	@SchedulerLock(name = "createfiles", lockAtMostFor = "${scheduler.shedlock-lock-at-most-for}")
	public void executeCreateFiles() {
		LOGGER.info(LOG_CREATE_FILES_STARTED);

		RequestId.init();
		invoiceFileService.createFiles();

		LOGGER.info(LOG_CREATE_FILES_ENDED);
	}

	@Scheduled(cron = "${scheduler.transferfiles.cron}")
	@SchedulerLock(name = "transferfiles", lockAtMostFor = "${scheduler.shedlock-lock-at-most-for}")
	public void executeTransferFiles() {
		LOGGER.info(LOG_SEND_TO_FTP_STARTED);

		RequestId.init();
		invoiceFileService.transferFiles();

		LOGGER.info(LOG_SEND_TO_FTP_ENDED);
	}
}
