package se.sundsvall.billingpreprocessor.service;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

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

import generated.se.sundsvall.messaging.EmailRequest;
import generated.se.sundsvall.messaging.EmailSender;
import se.sundsvall.billingpreprocessor.integration.messaging.MessagingClient;
import se.sundsvall.billingpreprocessor.integration.messaging.config.ErrorMessageProperties;
import se.sundsvall.billingpreprocessor.service.creator.CreationError;
import se.sundsvall.dept44.requestid.RequestId;

@SpringBootTest(classes = { MessagingService.class, ErrorMessageProperties.class })
@TestPropertySource(properties = {
	"spring.application.name=applicationName",
	"spring.profiles.active=junit" })
class MessagingServiceTest {

	private final List<CreationError> ERRORS = List.of(CreationError.create("error"));

	@MockBean
	private MessagingClient messagingClientMock;

	@MockBean
	private ErrorMessageProperties errorMessagePropertiesMock;

	@Captor
	private ArgumentCaptor<EmailRequest> emailRequestCaptor;

	@Autowired
	private MessagingService service;

	@Test
	void sendErrorMailWhenNullSender() {
		when(errorMessagePropertiesMock.recipients()).thenReturn(List.of("receiver"));

		service.sendErrorMail(ERRORS);

		verify(errorMessagePropertiesMock).sender();
		verifyNoMoreInteractions(errorMessagePropertiesMock, messagingClientMock);
	}

	@Test
	void sendErrorMailWhenNullReceivers() {
		when(errorMessagePropertiesMock.sender()).thenReturn("sender");

		service.sendErrorMail(ERRORS);

		verify(errorMessagePropertiesMock).sender();
		verify(errorMessagePropertiesMock).recipients();
		verifyNoMoreInteractions(errorMessagePropertiesMock, messagingClientMock);
	}

	@Test
	void sendErrorMailWhenEmptyReceivers() {
		when(errorMessagePropertiesMock.sender()).thenReturn("sender");
		when(errorMessagePropertiesMock.recipients()).thenReturn(emptyList());

		service.sendErrorMail(ERRORS);

		verify(errorMessagePropertiesMock).sender();
		verify(errorMessagePropertiesMock).recipients();
		verifyNoMoreInteractions(errorMessagePropertiesMock, messagingClientMock);
	}

	@Test
	void sendErrorMail() {
		RequestId.init();

		when(errorMessagePropertiesMock.sender()).thenReturn("sender");
		when(errorMessagePropertiesMock.recipients()).thenReturn(List.of("recipient.1", "recipient.2"));
		when(errorMessagePropertiesMock.subjectTemplate()).thenReturn("ApplicationName: %s Environment: %s");
		when(errorMessagePropertiesMock.bodyPrefixTemplate()).thenReturn("ExecutionDate: %s ");
		when(errorMessagePropertiesMock.bodySuffixTemplate()).thenReturn("RequestId: %s Sender: %s SenderName: %s");
		when(errorMessagePropertiesMock.htmlPrefixTemplate()).thenReturn("");
		when(errorMessagePropertiesMock.htmlSuffixTemplate()).thenReturn("");
		when(errorMessagePropertiesMock.listItemTemplate()).thenReturn("ListItem: %s ");
		when(errorMessagePropertiesMock.listPrefixTemplate()).thenReturn("");
		when(errorMessagePropertiesMock.listSuffixTemplate()).thenReturn("");

		service.sendErrorMail(ERRORS);

		verify(errorMessagePropertiesMock, times(4)).sender();
		verify(errorMessagePropertiesMock, times(2)).recipients();
		verify(messagingClientMock, times(2)).sendEmail(eq(true), emailRequestCaptor.capture());

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
