package se.sundsvall.billingpreprocessor.service;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import generated.se.sundsvall.messaging.EmailRequest;
import generated.se.sundsvall.messaging.EmailSender;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import se.sundsvall.billingpreprocessor.integration.messaging.MessagingClient;
import se.sundsvall.billingpreprocessor.integration.messaging.config.ErrorMessageProperties;
import se.sundsvall.billingpreprocessor.integration.messaging.config.ErrorMessageProperties.ErrorMailTemplate;
import se.sundsvall.billingpreprocessor.service.error.InvoiceFileError;
import se.sundsvall.dept44.requestid.RequestId;

@SpringBootTest(classes = {
	MessagingService.class, ErrorMessageProperties.class
})
@TestPropertySource(properties = {
	"spring.application.name=applicationName",
	"spring.profiles.active=junit"
})
class MessagingServiceTest {

	private static final List<InvoiceFileError> ERRORS = List.of(InvoiceFileError.create("error"));
	private static final String MUNICIPALITY_ID = "municipalityId";

	@MockBean
	private MessagingClient messagingClientMock;

	@MockBean
	private ErrorMessageProperties errorMessagePropertiesMock;

	@MockBean
	private ErrorMailTemplate errorMailTemplateMock;

	@Captor
	private ArgumentCaptor<EmailRequest> emailRequestCaptor;

	@Autowired
	private MessagingService service;

	@Test
	void sendCreationErrorMailWhenNullSender() {
		when(errorMessagePropertiesMock.recipients()).thenReturn(List.of("receiver"));

		service.sendCreationErrorMail(MUNICIPALITY_ID, ERRORS);

		verify(errorMessagePropertiesMock).sender();
		verifyNoMoreInteractions(errorMessagePropertiesMock, messagingClientMock);
	}

	@Test
	void sendCreationErrorMailWhenNullReceivers() {
		when(errorMessagePropertiesMock.sender()).thenReturn("sender");

		service.sendCreationErrorMail(MUNICIPALITY_ID, ERRORS);

		verify(errorMessagePropertiesMock).sender();
		verify(errorMessagePropertiesMock).recipients();
		verifyNoMoreInteractions(errorMessagePropertiesMock, messagingClientMock);
	}

	@Test
	void sendCreationErrorMailWhenEmptyReceivers() {
		when(errorMessagePropertiesMock.sender()).thenReturn("sender");
		when(errorMessagePropertiesMock.recipients()).thenReturn(emptyList());

		service.sendCreationErrorMail(MUNICIPALITY_ID, ERRORS);

		verify(errorMessagePropertiesMock).sender();
		verify(errorMessagePropertiesMock).recipients();
		verifyNoMoreInteractions(errorMessagePropertiesMock, messagingClientMock);
	}

	@Test
	void sendCreationErrorMail() {
		RequestId.init();

		when(errorMessagePropertiesMock.sender()).thenReturn("sender");
		when(errorMessagePropertiesMock.recipients()).thenReturn(List.of("recipient.1", "recipient.2"));
		when(errorMessagePropertiesMock.creationErrorMailTemplate()).thenReturn(errorMailTemplateMock);
		when(errorMailTemplateMock.subject()).thenReturn("ApplicationName: %s Environment: %s");
		when(errorMailTemplateMock.bodyPrefix()).thenReturn("ExecutionDate: %s ");
		when(errorMailTemplateMock.bodySuffix()).thenReturn("RequestId: %s Sender: %s SenderName: %s");
		when(errorMailTemplateMock.htmlPrefix()).thenReturn("");
		when(errorMailTemplateMock.htmlSuffix()).thenReturn("");
		when(errorMailTemplateMock.listItem()).thenReturn("ListItem: %s ");
		when(errorMailTemplateMock.listPrefix()).thenReturn("");
		when(errorMailTemplateMock.listSuffix()).thenReturn("");

		service.sendCreationErrorMail(MUNICIPALITY_ID, ERRORS);

		verify(errorMessagePropertiesMock, times(4)).sender();
		verify(errorMessagePropertiesMock, times(2)).recipients();
		verify(errorMessagePropertiesMock, times(8)).creationErrorMailTemplate();
		verify(messagingClientMock, times(2)).sendEmail(eq(MUNICIPALITY_ID), eq(true), emailRequestCaptor.capture());
		verifyNoMoreInteractions(errorMessagePropertiesMock, messagingClientMock);

		assertThat(emailRequestCaptor.getAllValues()).hasSize(2)
			.allSatisfy(request -> {
				assertThat(request.getSender()).isNotNull().extracting(EmailSender::getName, EmailSender::getAddress).containsOnly("sender");
				assertThat(request.getSubject()).isEqualTo("ApplicationName: applicationName Environment: junit");
				assertThat(new String(Base64.getDecoder().decode(request.getHtmlMessage()), StandardCharsets.UTF_8))
					.isEqualTo("ExecutionDate: %s ListItem: error RequestId: %s Sender: sender SenderName: applicationName"
						.formatted(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE), RequestId.get()));
			})
			.satisfiesOnlyOnce(request -> {
				assertThat(request.getEmailAddress()).isEqualTo("recipient.1");
			}).satisfiesOnlyOnce(request -> {
				assertThat(request.getEmailAddress()).isEqualTo("recipient.2");
			});
	}

	@Test
	void sendTransferErrorMailWhenNullSender() {
		when(errorMessagePropertiesMock.recipients()).thenReturn(List.of("receiver"));

		service.sendTransferErrorMail(MUNICIPALITY_ID, ERRORS);

		verify(errorMessagePropertiesMock).sender();
		verifyNoMoreInteractions(errorMessagePropertiesMock, messagingClientMock);
	}

	@Test
	void sendTransferErrorMailWhenNullReceivers() {
		when(errorMessagePropertiesMock.sender()).thenReturn("sender");

		service.sendTransferErrorMail(MUNICIPALITY_ID, ERRORS);

		verify(errorMessagePropertiesMock).sender();
		verify(errorMessagePropertiesMock).recipients();
		verifyNoMoreInteractions(errorMessagePropertiesMock, messagingClientMock);
	}

	@Test
	void sendTransferErrorMailWhenEmptyReceivers() {
		when(errorMessagePropertiesMock.sender()).thenReturn("sender");
		when(errorMessagePropertiesMock.recipients()).thenReturn(emptyList());

		service.sendTransferErrorMail(MUNICIPALITY_ID, ERRORS);

		verify(errorMessagePropertiesMock).sender();
		verify(errorMessagePropertiesMock).recipients();
		verifyNoMoreInteractions(errorMessagePropertiesMock, messagingClientMock);
	}

	@Test
	void sendTransferErrorMail() {
		RequestId.init();

		when(errorMessagePropertiesMock.sender()).thenReturn("sender");
		when(errorMessagePropertiesMock.recipients()).thenReturn(List.of("recipient.1", "recipient.2"));
		when(errorMessagePropertiesMock.transferErrorMailTemplate()).thenReturn(errorMailTemplateMock);
		when(errorMailTemplateMock.subject()).thenReturn("ApplicationName: %s Environment: %s");
		when(errorMailTemplateMock.bodyPrefix()).thenReturn("ExecutionDate: %s ");
		when(errorMailTemplateMock.bodySuffix()).thenReturn("RequestId: %s Sender: %s SenderName: %s");
		when(errorMailTemplateMock.htmlPrefix()).thenReturn("");
		when(errorMailTemplateMock.htmlSuffix()).thenReturn("");
		when(errorMailTemplateMock.listItem()).thenReturn("ListItem: %s ");
		when(errorMailTemplateMock.listPrefix()).thenReturn("");
		when(errorMailTemplateMock.listSuffix()).thenReturn("");

		service.sendTransferErrorMail(MUNICIPALITY_ID, ERRORS);

		verify(errorMessagePropertiesMock, times(4)).sender();
		verify(errorMessagePropertiesMock, times(2)).recipients();
		verify(errorMessagePropertiesMock, times(8)).transferErrorMailTemplate();
		verify(messagingClientMock, times(2)).sendEmail(eq(MUNICIPALITY_ID), eq(true), emailRequestCaptor.capture());
		verifyNoMoreInteractions(errorMessagePropertiesMock, messagingClientMock);

		assertThat(emailRequestCaptor.getAllValues()).hasSize(2)
			.allSatisfy(request -> {
				assertThat(request.getSender()).isNotNull().extracting(EmailSender::getName, EmailSender::getAddress).containsOnly("sender");
				assertThat(request.getSubject()).isEqualTo("ApplicationName: applicationName Environment: junit");
				assertThat(new String(Base64.getDecoder().decode(request.getHtmlMessage()), StandardCharsets.UTF_8))
					.isEqualTo("ExecutionDate: %s ListItem: error RequestId: %s Sender: sender SenderName: applicationName"
						.formatted(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE), RequestId.get()));
			})
			.satisfiesOnlyOnce(request -> {
				assertThat(request.getEmailAddress()).isEqualTo("recipient.1");
			}).satisfiesOnlyOnce(request -> {
				assertThat(request.getEmailAddress()).isEqualTo("recipient.2");
			});
	}
}
