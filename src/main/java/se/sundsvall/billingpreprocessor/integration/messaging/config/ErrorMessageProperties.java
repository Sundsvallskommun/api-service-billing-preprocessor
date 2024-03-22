package se.sundsvall.billingpreprocessor.integration.messaging.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("errorreport")
public record ErrorMessageProperties(
	ErrorMailTemplate creationErrorMailTemplate,
	ErrorMailTemplate transferErrorMailTemplate,
	List<String> recipients,
	String sender) {

	public record ErrorMailTemplate(
		String subject,
		String htmlPrefix,
		String bodyPrefix,
		String listPrefix,
		String listItem,
		String listSuffix,
		String bodySuffix,
		String htmlSuffix) {
	}
}