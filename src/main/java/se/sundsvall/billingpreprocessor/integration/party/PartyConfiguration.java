package se.sundsvall.billingpreprocessor.integration.party;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.List;

import org.springframework.cloud.openfeign.FeignBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import se.sundsvall.dept44.configuration.feign.FeignConfiguration;
import se.sundsvall.dept44.configuration.feign.FeignMultiCustomizer;
import se.sundsvall.dept44.configuration.feign.decoder.ProblemErrorDecoder;

@Import(FeignConfiguration.class)
class PartyConfiguration {

    static final String CLIENT_ID = "party";

    @Bean
    FeignBuilderCustomizer feignBuilderCustomizer(final PartyProperties partyProperties) {
        return FeignMultiCustomizer.create()
            .withRetryableOAuth2InterceptorForClientRegistration(ClientRegistration.withRegistrationId(CLIENT_ID)
                .tokenUri(partyProperties.oauth2().tokenUri())
                .clientId(partyProperties.oauth2().clientId())
                .clientSecret(partyProperties.oauth2().clientSecret())
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .build())
            .withErrorDecoder(new ProblemErrorDecoder(CLIENT_ID, List.of(NOT_FOUND.value())))
            .withRequestTimeoutsInSeconds(partyProperties.connectTimeout(), partyProperties.readTimeout())
            .composeCustomizersToOne();
    }
}
