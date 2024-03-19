package se.sundsvall.billingpreprocessor.service.mapper;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zalando.problem.ThrowableProblem;

import se.sundsvall.billingpreprocessor.integration.messaging.config.ErrorMessageProperties;
import se.sundsvall.billingpreprocessor.service.creator.CreationError;
import se.sundsvall.dept44.requestid.RequestId;

@ExtendWith(MockitoExtension.class)
class MessagingMapperTest {
	private static final String SENDER_NAME = "senderName";

	@Mock
	private ErrorMessageProperties propertiesMock;

	@Test
	void composeWithNullProperties() {
		final List<CreationError> errors = emptyList();
		final var e = assertThrows(ThrowableProblem.class, () -> MessagingMapper.composeBody(errors, SENDER_NAME, null));

		assertThat(e.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR);
		assertThat(e.getMessage()).isEqualTo("Internal Server Error: ErrorMessageProperties bean is null");
	}

	@Test 
	void composeWithSpecificError() {
		final var error = CreationError.create(UUID.randomUUID().toString(), "Specific error");
	
		initialize();

		final var composedMessage = MessagingMapper.composeBody(List.of(error), SENDER_NAME, propertiesMock);

		assertThat(composedMessage).isEqualTo("<html><body>Execution date %s<ul><li>Billingrecord med id %s gick inte att bearbeta. Felmeddelande är '%s'.</li></ul>RequestId: %s, mailTo: %s, Regards %s</body></html>"
			.formatted(
				LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE),
				error.getEntityId(),
				error.getMessage(),
				RequestId.get(),
				"senderEmail",
				SENDER_NAME));
	}

	@Test
	void composeWithCommonError() {
		final var error = CreationError.create("Common error");

		initialize();

		final var composedMessage = MessagingMapper.composeBody(List.of(error), SENDER_NAME, propertiesMock);
		
		assertThat(composedMessage).isEqualTo("<html><body>Execution date %s<ul><li>%s</li></ul>RequestId: %s, mailTo: %s, Regards %s</body></html>"
			.formatted(
				LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE),
				error.getMessage(),
				RequestId.get(),
				"senderEmail",
				SENDER_NAME));
		
	}

	@Test
	void toEmail() {
		final var subject = "subject";
		final var recipient = "recipient";
		final var sender = "sender";
		final var body = "body";

		final var bean = MessagingMapper.toEmail(subject, body, recipient, sender);

		assertThat(bean.getAttachments()).isNullOrEmpty();
		assertThat(bean.getEmailAddress()).isEqualTo(recipient);
		assertThat(bean.getHeaders()).isNullOrEmpty();
		assertThat(bean.getHtmlMessage()).isEqualTo(body);
		assertThat(bean.getMessage()).isNull();
		assertThat(bean.getParty()).isNull();
		assertThat(bean.getSender()).isNotNull().satisfies(s -> {
			assertThat(s.getAddress()).isEqualTo(sender);
			assertThat(s.getName()).isEqualTo(sender);
			assertThat(s.getReplyTo()).isNull();
		});
	}

	@ParameterizedTest
	@ValueSource(strings = { "<body>", "åäö", " ", "" })
	void verifyBase64Body(String body) {
		final var bean = MessagingMapper.toEmail(null, body, null, null);

		assertThat(Base64.getDecoder().decode(bean.getHtmlMessage())).isEqualTo(body.getBytes());
	}

	@Test
	void verifyBase64BodyForEncodedString() {
		final var body = new String(Base64.getEncoder().encode("<body>".getBytes()), StandardCharsets.UTF_8);

		final var bean = MessagingMapper.toEmail(null, body, null, null);

		assertThat(bean.getHtmlMessage().getBytes()).isEqualTo(body.getBytes());
	}

	@Test
	void verifyBase64BodyForNull() {
		final var bean = MessagingMapper.toEmail(null, null, null, null);

		assertThat(bean.getHtmlMessage()).isNull();
	}

	private void initialize() {
		RequestId.init();

		when(propertiesMock.bodyPrefixTemplate()).thenReturn("<body>Execution date %s");
		when(propertiesMock.bodySuffixTemplate()).thenReturn("RequestId: %s, mailTo: %s, Regards %s</body>");
		when(propertiesMock.htmlPrefixTemplate()).thenReturn("<html>");
		when(propertiesMock.htmlSuffixTemplate()).thenReturn("</html>");
		when(propertiesMock.listItemTemplate()).thenReturn("<li>%s</li>");
		when(propertiesMock.listPrefixTemplate()).thenReturn("<ul>");
		when(propertiesMock.listSuffixTemplate()).thenReturn("</ul>");
		when(propertiesMock.sender()).thenReturn("senderEmail");
	}
}
