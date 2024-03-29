package se.sundsvall.billingpreprocessor.integration.messaging;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static se.sundsvall.billingpreprocessor.integration.messaging.config.MessagingConfiguration.CLIENT_ID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import generated.se.sundsvall.messaging.EmailRequest;
import generated.se.sundsvall.messaging.MessageResult;
import se.sundsvall.billingpreprocessor.integration.messaging.config.MessagingConfiguration;

@FeignClient(name = CLIENT_ID, url = "${integration.messaging.url}", configuration = MessagingConfiguration.class)
public interface MessagingClient {

	/**
	 * Send a single e-mail
	 *
	 * @param emailRequest containing email information
	 * @return response containing id for sent message
	 */
	@PostMapping(path = "/email", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	MessageResult sendEmail(@RequestParam("async") boolean sendAsynchronously, @RequestBody EmailRequest emailRequest);
}
