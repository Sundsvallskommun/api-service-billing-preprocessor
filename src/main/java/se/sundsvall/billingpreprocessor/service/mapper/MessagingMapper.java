package se.sundsvall.billingpreprocessor.service.mapper;

import static java.time.LocalDate.now;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.zalando.problem.Problem;

import generated.se.sundsvall.messaging.EmailRequest;
import generated.se.sundsvall.messaging.EmailSender;
import se.sundsvall.billingpreprocessor.integration.messaging.config.ErrorMessageProperties;
import se.sundsvall.billingpreprocessor.service.error.InvoiceFileError;
import se.sundsvall.dept44.common.validators.annotation.impl.ValidBase64ConstraintValidator;
import se.sundsvall.dept44.requestid.RequestId;

public final class MessagingMapper {
	private static final ValidBase64ConstraintValidator BASE64_VALIDATOR = new ValidBase64ConstraintValidator();
	private static final Encoder BASE64_ENCODER = Base64.getEncoder();

	private MessagingMapper() {}

	public static String composeCreationErrorMailBody(List<InvoiceFileError> errors, String senderName, ErrorMessageProperties properties) {
		if (isNull(properties)) {
			throw Problem.valueOf(INTERNAL_SERVER_ERROR, "ErrorMessageProperties bean is null");
		}

		final var bodyBuilder = new StringBuilder(properties.creationErrorMailTemplate().htmlPrefix())
			.append(properties.creationErrorMailTemplate().bodyPrefix().formatted(now().format(ISO_LOCAL_DATE)));

		final var commonErrors = ofNullable(errors).orElse(emptyList()).stream()
			.filter(InvoiceFileError::isCommonError)
			.toList();
		final var recordSpecificErrors = CollectionUtils.subtract(ofNullable(errors).orElse(emptyList()), commonErrors);

		if (!recordSpecificErrors.isEmpty()) {
			addSpecificCreationErrors(properties, bodyBuilder, recordSpecificErrors);
		}
		if (!commonErrors.isEmpty()) {
			addCommonCreationErrors(properties, bodyBuilder, commonErrors);
		}

		return bodyBuilder
			.append(properties.creationErrorMailTemplate().bodySuffix().formatted(RequestId.get(), properties.sender(), senderName))
			.append(properties.creationErrorMailTemplate().htmlSuffix()).toString();
	}

	public static String composeTransferErrorMailBody(List<InvoiceFileError> errors, String senderName, ErrorMessageProperties properties) {
		if (isNull(properties)) {
			throw Problem.valueOf(INTERNAL_SERVER_ERROR, "ErrorMessageProperties bean is null");
		}

		final var bodyBuilder = new StringBuilder(properties.transferErrorMailTemplate().htmlPrefix())
			.append(properties.transferErrorMailTemplate().bodyPrefix().formatted(now().format(ISO_LOCAL_DATE)));

		if (!errors.isEmpty()) {
			bodyBuilder.append(properties.transferErrorMailTemplate().listPrefix());
			errors.forEach(error -> bodyBuilder.append(properties.transferErrorMailTemplate().listItem().formatted(error.getMessage())));
			bodyBuilder.append(properties.transferErrorMailTemplate().listSuffix());
		}

		return bodyBuilder
			.append(properties.transferErrorMailTemplate().bodySuffix().formatted(RequestId.get(), properties.sender(), senderName))
			.append(properties.transferErrorMailTemplate().htmlSuffix()).toString();
	}

	public static EmailRequest toEmail(String subject, String htmlBody, String recipient, String sender) {
		return new EmailRequest()
			.emailAddress(recipient)
			.htmlMessage(base64Encode(htmlBody))
			.sender(toEmailSender(sender))
			.subject(subject);
	}

	private static void addSpecificCreationErrors(ErrorMessageProperties properties, final StringBuilder bodyBuilder, final Collection<InvoiceFileError> recordSpecificErrors) {
		bodyBuilder.append(properties.creationErrorMailTemplate().listPrefix().formatted("Billingrecord-specifika"));
		recordSpecificErrors.stream()
			.map(error -> "Billingrecord med id %s gick inte att bearbeta. Felmeddelande är '%s'.".formatted(error.getEntityId(), error.getMessage()))
			.forEach(message -> bodyBuilder.append(properties.creationErrorMailTemplate().listItem().formatted(message)));
		bodyBuilder.append(properties.creationErrorMailTemplate().listSuffix());
	}

	private static void addCommonCreationErrors(ErrorMessageProperties properties, final StringBuilder bodyBuilder, final List<InvoiceFileError> commonErrors) {
		bodyBuilder.append(properties.creationErrorMailTemplate().listPrefix().formatted("Övriga"));
		commonErrors.forEach(error -> bodyBuilder.append(properties.creationErrorMailTemplate().listItem().formatted(error.getMessage())));
		bodyBuilder.append(properties.creationErrorMailTemplate().listSuffix());
	}

	private static EmailSender toEmailSender(String sender) {
		return new EmailSender()
			.name(sender)
			.address(sender);
	}

	private static String base64Encode(String message) {
		if (isEmpty(message) || BASE64_VALIDATOR.isValid(message)) {
			return message;
		}
		return BASE64_ENCODER.encodeToString(message.getBytes(StandardCharsets.UTF_8));
	}
}
