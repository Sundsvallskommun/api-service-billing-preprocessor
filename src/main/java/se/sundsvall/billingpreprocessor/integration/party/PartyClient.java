package se.sundsvall.billingpreprocessor.integration.party;

import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static se.sundsvall.billingpreprocessor.integration.party.config.PartyConfiguration.CLIENT_ID;

import generated.se.sundsvall.party.PartyType;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.Optional;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import se.sundsvall.billingpreprocessor.integration.party.config.PartyConfiguration;

@FeignClient(
	name = CLIENT_ID,
	url = "${integration.party.url}",
	configuration = PartyConfiguration.class,
	dismiss404 = true)
@CircuitBreaker(name = CLIENT_ID)
public interface PartyClient {

	/**
	 * Get legal id by party type and party id.
	 *
	 * @param  municipalityId                       municipality ID
	 * @param  partyType                            the party type.
	 * @param  partyId                              the party id, i.e. person id or organization id.
	 * @return                                      an optional string containing the legal id that corresponds to the
	 *                                              provided party
	 *                                              type and party id.
	 * @throws org.zalando.problem.ThrowableProblem on errors
	 */
	@GetMapping(path = "/{municipalityId}/{type}/{partyId}/legalId", produces = TEXT_PLAIN_VALUE)
	Optional<String> getLegalId(@PathVariable("municipalityId") String municipalityId, @PathVariable("type") PartyType partyType, @PathVariable("partyId") String partyId);
}
