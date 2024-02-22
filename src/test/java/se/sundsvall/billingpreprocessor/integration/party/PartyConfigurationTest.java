package se.sundsvall.billingpreprocessor.integration.party;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static se.sundsvall.billingpreprocessor.integration.party.PartyConfiguration.CLIENT_ID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.registration.ClientRegistration;

import se.sundsvall.dept44.configuration.feign.FeignMultiCustomizer;
import se.sundsvall.dept44.configuration.feign.decoder.ProblemErrorDecoder;

import feign.codec.ErrorDecoder;

@ExtendWith(MockitoExtension.class)
class PartyConfigurationTest {

    @Mock
    private PartyProperties.Oauth2 oauth2Mock;
    @Mock
    private PartyProperties partyPropertiesMock;

    @Spy
    private FeignMultiCustomizer feignMultiCustomizerSpy;
    @Captor
    private ArgumentCaptor<ErrorDecoder> errorDecoderCaptor;

    private final PartyConfiguration configuration = new PartyConfiguration();

    @BeforeEach
    void setUp() {
        when(oauth2Mock.tokenUri()).thenReturn("someTokenUri");
        when(oauth2Mock.clientId()).thenReturn("someClientId");
        when(oauth2Mock.clientSecret()).thenReturn("someClientSecret");
        when(partyPropertiesMock.oauth2()).thenReturn(oauth2Mock);
        when(partyPropertiesMock.connectTimeout()).thenReturn(123);
        when(partyPropertiesMock.readTimeout()).thenReturn(456);
    }

    @Test
    void testFeignBuilderCustomizer() {
        try (var mockFeignMultiCustomizer = mockStatic(FeignMultiCustomizer.class)) {
            mockFeignMultiCustomizer.when(FeignMultiCustomizer::create).thenReturn(feignMultiCustomizerSpy);

            configuration.feignBuilderCustomizer(partyPropertiesMock);

            mockFeignMultiCustomizer.verify(FeignMultiCustomizer::create);
        }

        verify(oauth2Mock).tokenUri();
        verify(oauth2Mock).clientId();
        verify(oauth2Mock).clientSecret();
        verify(partyPropertiesMock, times(3)).oauth2();
        verify(partyPropertiesMock).connectTimeout();
        verify(partyPropertiesMock).readTimeout();
        verify(feignMultiCustomizerSpy).withErrorDecoder(errorDecoderCaptor.capture());
        verify(feignMultiCustomizerSpy).withRequestTimeoutsInSeconds(partyPropertiesMock.connectTimeout(), partyPropertiesMock.readTimeout());
        verify(feignMultiCustomizerSpy).withRetryableOAuth2InterceptorForClientRegistration(any(ClientRegistration.class));
        verify(feignMultiCustomizerSpy).composeCustomizersToOne();

        assertThat(errorDecoderCaptor.getValue())
            .isInstanceOf(ProblemErrorDecoder.class)
            .hasFieldOrPropertyWithValue("integrationName", CLIENT_ID);
    }
}
