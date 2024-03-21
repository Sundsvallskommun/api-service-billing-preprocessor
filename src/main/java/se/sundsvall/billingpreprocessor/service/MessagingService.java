package se.sundsvall.billingpreprocessor.service;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static se.sundsvall.billingpreprocessor.service.mapper.MessagingMapper.composeBody;
import static se.sundsvall.billingpreprocessor.service.mapper.MessagingMapper.toEmail;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import se.sundsvall.billingpreprocessor.integration.messaging.MessagingClient;
import se.sundsvall.billingpreprocessor.integration.messaging.config.ErrorMessageProperties;
import se.sundsvall.billingpreprocessor.service.creator.CreationError;

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

	public void sendErrorMail(List<CreationError> errors) {
		if (isBlank(properties.sender()) || isEmpty(properties.recipients())) {
			LOG.info("Error report will not be sent as sender or receiver has not been defined in properties.");
			return;
		}

		final var subject = properties.subjectTemplate().formatted(applicationName, environment);
		final var body = composeBody(errors, applicationName, properties);
		properties.recipients().forEach(recipient ->
		client.sendEmail(ASYNCHRONOUSLY,
			toEmail(
				subject,
				body,
				recipient,
			properties.sender())));
	}
}
