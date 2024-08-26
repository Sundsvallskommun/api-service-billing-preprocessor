package se.sundsvall.billingpreprocessor.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import se.sundsvall.dept44.requestid.RequestId;

/**
 * Class responsible for async-execution of scheduled jobs.
 *
 * The purpose with this is to detach the execution from the calling thread
 * when the call is initialized from the REST-API.
 */
@Service
public class AsyncExecutorService {

	private final InvoiceFileService invoiceFileService;

	public AsyncExecutorService(InvoiceFileService invoiceFileService) {
		this.invoiceFileService = invoiceFileService;
	}

	@Async
	public void createFiles(String uuid, String municipalityId) {
		RequestId.init(uuid);
		invoiceFileService.createFiles(municipalityId);
	}

	@Async
	public void transferFiles(String uuid, String municipalityId) {
		RequestId.init(uuid);
		invoiceFileService.transferFiles(municipalityId);
	}
}
