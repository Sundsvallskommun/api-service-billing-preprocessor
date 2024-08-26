package se.sundsvall.billingpreprocessor.service.scheduler;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import se.sundsvall.billingpreprocessor.integration.sftp.SftpProperties;
import se.sundsvall.billingpreprocessor.integration.sftp.SftpPropertiesConfig;
import se.sundsvall.billingpreprocessor.service.InvoiceFileService;
import se.sundsvall.dept44.requestid.RequestId;

import java.util.Map;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class InvoiceFileSchedulerTest {

	private static final String MUNICIPALITY_ID = "2281";

	@Mock
	private InvoiceFileService invoiceFileServiceMock;

	@Mock
	private SftpPropertiesConfig sftpPropertiesConfigMock;

	@Mock
	private SftpProperties sftpPropertiesMock;

	@InjectMocks
	private InvoiceFileScheduler scheduler;

	@Test
	void executeCreateFiles() {
		when(sftpPropertiesConfigMock.getMap()).thenReturn(Map.of(MUNICIPALITY_ID, sftpPropertiesMock));
		// Mock static RequestId to enable spy and to verify that static method is being called
		try (MockedStatic<RequestId> requestIdMock = Mockito.mockStatic(RequestId.class)) {
			scheduler.executeCreateFiles();

			requestIdMock.verify(RequestId::init);
			verify(invoiceFileServiceMock).createFiles(MUNICIPALITY_ID);
			verifyNoMoreInteractions(invoiceFileServiceMock);
		}
	}

	@Test
	void executeSendToFtp() {
		when(sftpPropertiesConfigMock.getMap()).thenReturn(Map.of(MUNICIPALITY_ID, sftpPropertiesMock));
		// Mock static RequestId to enable spy and to verify that static method is being called
		try (MockedStatic<RequestId> requestIdMock = Mockito.mockStatic(RequestId.class)) {
			scheduler.executeTransferFiles();

			requestIdMock.verify(RequestId::init);
			verify(invoiceFileServiceMock).transferFiles(MUNICIPALITY_ID);
			verifyNoMoreInteractions(invoiceFileServiceMock);
		}
	}
}
