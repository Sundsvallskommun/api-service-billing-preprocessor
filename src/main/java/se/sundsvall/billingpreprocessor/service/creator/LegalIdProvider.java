package se.sundsvall.billingpreprocessor.service.creator;

import org.springframework.stereotype.Component;
import se.sundsvall.billingpreprocessor.integration.party.PartyClient;
import se.sundsvall.dept44.problem.Problem;

import static generated.se.sundsvall.party.PartyType.ENTERPRISE;
import static generated.se.sundsvall.party.PartyType.PRIVATE;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static se.sundsvall.billingpreprocessor.service.util.ProblemUtil.createProblem;

@Component
public class LegalIdProvider {
	private final PartyClient partyClient;

	public LegalIdProvider(PartyClient partyClient) {
		this.partyClient = partyClient;
	}

	public String translateToLegalId(String municipalityId, String partyId) {
		if (isBlank(partyId)) {
			throw Problem.valueOf(INTERNAL_SERVER_ERROR, "Party id is not present");
		}

		return partyClient.getLegalId(municipalityId, PRIVATE, partyId)
			.or(() -> partyClient.getLegalId(municipalityId, ENTERPRISE, partyId))
			.orElseThrow(createProblem(NOT_FOUND, format("PartyId '%s' could not be found as a private customer or an enterprise customer", partyId)));
	}
}
