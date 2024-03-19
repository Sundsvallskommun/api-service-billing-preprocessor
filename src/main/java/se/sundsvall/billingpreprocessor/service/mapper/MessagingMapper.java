package se.sundsvall.billingpreprocessor.service.mapper;

import static java.time.LocalDate.now;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.zalando.problem.Problem;

import generated.se.sundsvall.messaging.EmailRequest;
import generated.se.sundsvall.messaging.EmailSender;
import io.netty.util.internal.StringUtil;
import se.sundsvall.billingpreprocessor.integration.messaging.config.ErrorMessageProperties;
import se.sundsvall.billingpreprocessor.service.creator.CreationError;
import se.sundsvall.dept44.requestid.RequestId;

public final class MessagingMapper {
	private static final Decoder BASE64_DECODER = Base64.getDecoder();
	private static final Encoder BASE64_ENCODER = Base64.getEncoder();

	private MessagingMapper() {}

	public static String composeBody(List<CreationError> errors, String senderName, ErrorMessageProperties properties) {
		if (isNull(properties)) {
			throw Problem.valueOf(INTERNAL_SERVER_ERROR, "ErrorMessageProperties bean is null");
		}

		final var bodyBuilder = new StringBuilder(properties.htmlPrefixTemplate())
			.append(properties.bodyPrefixTemplate().formatted(now().format(ISO_LOCAL_DATE)));

		final var commonErrors = ofNullable(errors).orElse(emptyList()).stream()
			.filter(CreationError::isCommonError)
			.toList();
		final var recordSpecificErrors = CollectionUtils.subtract(ofNullable(errors).orElse(emptyList()), commonErrors);

		if (!recordSpecificErrors.isEmpty()) {
			addSpecificErrors(properties, bodyBuilder, recordSpecificErrors);
		}
		if (!commonErrors.isEmpty()) {
			addCommonErrors(properties, bodyBuilder, commonErrors);
		}

		return bodyBuilder
			.append(properties.bodySuffixTemplate().formatted(RequestId.get(), properties.sender(), senderName))
			.append(properties.htmlSuffixTemplate()).toString();
	}

	public static EmailRequest toEmail(String subject, String htmlBody, String recipient, String sender) {
		return new EmailRequest()
			.emailAddress(recipient)
			.htmlMessage(base64Encode(htmlBody))
			.sender(toEmailSender(sender))
			.subject(subject);
	}

	private static void addSpecificErrors(ErrorMessageProperties properties, final StringBuilder bodyBuilder, final Collection<CreationError> recordSpecificErrors) {
		bodyBuilder.append(properties.listPrefixTemplate().formatted("Billingrecord-specifika"));
		recordSpecificErrors.stream()
			.map(error -> "Billingrecord med id %s gick inte att bearbeta. Felmeddelande är '%s'.".formatted(error.getEntityId(), error.getMessage()))
			.forEach(message -> bodyBuilder.append(properties.listItemTemplate().formatted(message)));
		bodyBuilder.append(properties.listSuffixTemplate());
	}

	private static void addCommonErrors(ErrorMessageProperties properties, final StringBuilder bodyBuilder, final List<CreationError> commonErrors) {
		bodyBuilder.append(properties.listPrefixTemplate().formatted("Övriga"));
		commonErrors.forEach(error -> bodyBuilder.append(properties.listItemTemplate().formatted(error.getMessage())));
		bodyBuilder.append(properties.listSuffixTemplate());
	}

	private static EmailSender toEmailSender(String sender) {
		return new EmailSender()
			.name(sender)
			.address(sender);
	}

	private static String base64Encode(String message) {
		if (StringUtil.isNullOrEmpty(message)) {
			return message;
		}
		try {
			BASE64_DECODER.decode(message.getBytes(StandardCharsets.UTF_8));
			return message; // If decoding passes, the message is already in base64 format
		} catch (Exception e) {
			return BASE64_ENCODER.encodeToString(message.getBytes(StandardCharsets.UTF_8));
		}
	}

}
