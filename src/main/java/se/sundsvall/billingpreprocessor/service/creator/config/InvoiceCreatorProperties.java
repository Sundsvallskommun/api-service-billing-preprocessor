package se.sundsvall.billingpreprocessor.service.creator.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("invoicecreator")
public record InvoiceCreatorProperties(String recordTerminator) {
}
