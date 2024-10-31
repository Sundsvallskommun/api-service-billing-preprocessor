package se.sundsvall.billingpreprocessor.service;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static se.sundsvall.billingpreprocessor.service.mapper.MessagingMapper.composeCreationErrorMailBody;
import static se.sundsvall.billingpreprocessor.service.mapper.MessagingMapper.composeTransferErrorMailBody;
import static se.sundsvall.billingpreprocessor.service.mapper.MessagingMapper.toEmail;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import se.sundsvall.billingpreprocessor.integration.messaging.MessagingClient;
import se.sundsvall.billingpreprocessor.integration.messaging.config.ErrorMessageProperties;
import se.sundsvall.billingpreprocessor.service.error.InvoiceFileError;

@Service
public class MessagingService {
	private static final boolean ASYNCHRONOUSLY = true;
	private static final Logger LOG = LoggerFactory.getLogger(MessagingService.class);

	private final String applicationName;
	private final String environment;
	private final MessagingClient client;
	private final ErrorMessageProperties properties;

	public MessagingService(MessagingClient client,
		@Value("${spring.application.name}") String applicationName,
		@Value("${spring.profiles.active:}") String environment,
		ErrorMessageProperties properties) {

		this.client = client;
		this.applicationName = applicationName;
		this.environment = environment;
		this.properties = properties;
	}

	public void sendCreationErrorMail(String municipalityId, List<InvoiceFileError> errors) {
		if (isBlank(properties.sender()) || isEmpty(properties.recipients())) {
			LOG.info("Report of creation errors will not be sent as sender or receiver has not been defined in properties.");
			return;
		}

		final var subject = properties.creationErrorMailTemplate().subject().formatted(applicationName, environment);
		final var body = composeCreationErrorMailBody(errors, applicationName, properties);
		properties.recipients().forEach(recipient -> client.sendEmail(municipalityId, ASYNCHRONOUSLY,
			toEmail(
				subject,
				body,
				recipient,
				properties.sender())));
	}

	public void sendTransferErrorMail(String municipalityId, List<InvoiceFileError> errors) {
		if (isBlank(properties.sender()) || isEmpty(properties.recipients())) {
			LOG.info("Report of transfer errors will not be sent as sender or receiver has not been defined in properties.");
			return;
		}

		final var subject = properties.transferErrorMailTemplate().subject().formatted(applicationName, environment);
		final var body = composeTransferErrorMailBody(errors, applicationName, properties);
		properties.recipients().forEach(recipient -> client.sendEmail(municipalityId, ASYNCHRONOUSLY,
			toEmail(
				subject,
				body,
				recipient,
				properties.sender())));
	}
}
