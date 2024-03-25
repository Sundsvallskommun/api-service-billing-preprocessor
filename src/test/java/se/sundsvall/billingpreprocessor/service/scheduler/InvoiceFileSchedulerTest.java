package se.sundsvall.billingpreprocessor.service.scheduler;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import se.sundsvall.billingpreprocessor.service.InvoiceFileService;
import se.sundsvall.dept44.requestid.RequestId;

@ExtendWith(MockitoExtension.class)
class InvoiceFileSchedulerTest {

	@Mock
	private InvoiceFileService invoiceFileServiceMock;

	@InjectMocks
	private InvoiceFileScheduler scheduler;

	@Test
	void executeCreateFiles() {
		// Mock static RequestId to enable spy and to verify that static method is being called
		try (MockedStatic<RequestId> requestIdMock = Mockito.mockStatic(RequestId.class)) {
			scheduler.executeCreateFiles();

			requestIdMock.verify(() -> RequestId.init());
			verify(invoiceFileServiceMock).createFiles();
			verifyNoMoreInteractions(invoiceFileServiceMock);
		}
	}

	@Test
	void executeSendToFtp() {
		// Mock static RequestId to enable spy and to verify that static method is being called
		try (MockedStatic<RequestId> requestIdMock = Mockito.mockStatic(RequestId.class)) {
			scheduler.executeTransferFiles();

			requestIdMock.verify(() -> RequestId.init());
			verify(invoiceFileServiceMock).transferFiles();
			verifyNoMoreInteractions(invoiceFileServiceMock);
		}
	}
}
