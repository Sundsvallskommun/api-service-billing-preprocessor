package se.sundsvall.billingpreprocessor.integration.party;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("integration.party")
record PartyProperties(

        @NotBlank
        String url,

        @Valid
        @NotNull
        Oauth2 oauth2,

        @DefaultValue("10")
        int connectTimeout,
        @DefaultValue("20")
        int readTimeout) {

    record Oauth2(

        @NotBlank
        String tokenUri,
        @NotBlank
        String clientId,
        @NotBlank
        String clientSecret) { }
}
