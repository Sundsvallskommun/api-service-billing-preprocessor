package se.sundsvall.billingpreprocessor.service.creator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;

import generated.se.sundsvall.party.PartyType;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zalando.problem.ThrowableProblem;
import se.sundsvall.billingpreprocessor.integration.party.PartyClient;

@ExtendWith(MockitoExtension.class)
class LegalIdProviderTest {

	private static final String MUNICIPALITY_ID = "municipalityId";

	@Mock
	private PartyClient partyClientMock;

	@InjectMocks
	private LegalIdProvider legalIdProvider;

	@Captor
	private ArgumentCaptor<PartyType> partyTypeCaptor;

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = " ")
	void translateToLegalIdWithEmptyValue(String value) {
		final var e = assertThrows(ThrowableProblem.class, () -> legalIdProvider.translateToLegalId(MUNICIPALITY_ID, value));

		assertThat(e.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR);
		assertThat(e.getMessage()).isEqualTo("Internal Server Error: Party id is not present");
		verifyNoInteractions(partyClientMock);
	}

	@Test
	void translatePrivatePartyIdToLegalId() {
		final var partyId = UUID.randomUUID().toString();
		final var legalId = "123456789012";
		when(partyClientMock.getLegalId(MUNICIPALITY_ID, PartyType.PRIVATE, partyId)).thenReturn(Optional.of(legalId));

		final var result = legalIdProvider.translateToLegalId(MUNICIPALITY_ID, partyId);

		verify(partyClientMock).getLegalId(MUNICIPALITY_ID, PartyType.PRIVATE, partyId);
		verifyNoMoreInteractions(partyClientMock);
		assertThat(result).isEqualTo(legalId);
	}

	@Test
	void translateEnterprisePartyIdToLegalId() {
		final var partyId = UUID.randomUUID().toString();
		final var legalId = "123456789012";
		when(partyClientMock.getLegalId(MUNICIPALITY_ID, PartyType.PRIVATE, partyId)).thenReturn(Optional.empty());
		when(partyClientMock.getLegalId(MUNICIPALITY_ID, PartyType.ENTERPRISE, partyId)).thenReturn(Optional.of(legalId));

		final var result = legalIdProvider.translateToLegalId(MUNICIPALITY_ID, partyId);

		verify(partyClientMock, times(2)).getLegalId(eq(MUNICIPALITY_ID), partyTypeCaptor.capture(), eq(partyId));
		verifyNoMoreInteractions(partyClientMock);
		assertThat(result).isEqualTo(legalId);
		assertThat(partyTypeCaptor.getAllValues()).hasSize(2).containsExactlyInAnyOrder(PartyType.ENTERPRISE, PartyType.PRIVATE);
	}
}
