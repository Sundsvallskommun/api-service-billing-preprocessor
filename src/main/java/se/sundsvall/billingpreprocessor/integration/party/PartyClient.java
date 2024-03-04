package se.sundsvall.billingpreprocessor.integration.party;

import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static se.sundsvall.billingpreprocessor.integration.party.PartyConfiguration.CLIENT_ID;

import java.util.Optional;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import generated.se.sundsvall.party.PartyType;

@FeignClient(
    name = CLIENT_ID,
    url = "${integration.party.url}",
    configuration = PartyConfiguration.class,
    dismiss404 = true
)
public interface PartyClient {

    /**
     * Get legal id by party type and party id.
     *
     * @param partyType the party type.
     * @param partyId the party id, i.e. person id or organization id.
     * @return an optional string containing the legal id that corresponds to the provided party
     * type and party id.
     * @throws org.zalando.problem.ThrowableProblem on errors
     */
    @GetMapping(path = "/{type}/{partyId}/legalId", produces = { TEXT_PLAIN_VALUE, APPLICATION_PROBLEM_JSON_VALUE })
    Optional<String> getLegalId(@PathVariable("type") PartyType partyType, @PathVariable("partyId") String partyId);
}
