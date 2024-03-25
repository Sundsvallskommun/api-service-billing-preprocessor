package se.sundsvall.billingpreprocessor.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import se.sundsvall.dept44.requestid.RequestId;

@ExtendWith(MockitoExtension.class)
class AsyncExecutorServiceTest {

	@Mock
	private InvoiceFileService invoiceFileServiceMock;

	@Spy
	private RequestId requestIdSpy;

	@InjectMocks
	private AsyncExecutorService service;

	@Test
	void verifyAnnotations() {
		assertThat(AsyncExecutorService.class).hasAnnotations(Service.class);

		assertThat(Stream.of(AsyncExecutorService.class.getDeclaredMethods())
			.filter(m -> !m.isSynthetic()) // Need to filter out method added by jacoco (see https://www.jacoco.org/jacoco/trunk/doc/faq.html)
			.allMatch(m -> m.isAnnotationPresent(Async.class)))
				.withFailMessage("One or more methods in AsyncExecutorService is not annotated with @Async")
				.isTrue();
	}

	@Test
	void createFiles() {
		final var uuid = UUID.randomUUID().toString();

		// Mock static RequestId to enable spy and to verify that static method is being called
		try (MockedStatic<RequestId> requestIdMock = Mockito.mockStatic(RequestId.class)) {
			service.createFiles(uuid);

			requestIdMock.verify(() -> RequestId.init(uuid));
			verify(invoiceFileServiceMock).createFiles();
			verifyNoMoreInteractions(invoiceFileServiceMock);
		}
	}

	@Test
	void sendFilesToFtp() {
		final var uuid = UUID.randomUUID().toString();

		// Mock static RequestId to enable spy and to verify that static method is being called
		try (MockedStatic<RequestId> requestIdMock = Mockito.mockStatic(RequestId.class)) {
			service.transferFiles(uuid);

			requestIdMock.verify(() -> RequestId.init(uuid));
			verify(invoiceFileServiceMock).transferFiles();
			verifyNoMoreInteractions(invoiceFileServiceMock);
		}
	}
}
