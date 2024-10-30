package se.sundsvall.billingpreprocessor.integration.messaging.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("integration.messaging")
public record MessagingProperties(int connectTimeout, int readTimeout) {
}
