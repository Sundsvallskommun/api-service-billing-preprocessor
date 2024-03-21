package se.sundsvall.billingpreprocessor.integration.messaging.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("errorreport")
public record ErrorMessageProperties(
	String subjectTemplate,
	String htmlPrefixTemplate,
	String bodyPrefixTemplate,
	String listPrefixTemplate,
	String listItemTemplate,
	String listSuffixTemplate,
	String bodySuffixTemplate,
	String htmlSuffixTemplate,
	List<String> recipients,
	String sender) {
}