package se.sundsvall.billingpreprocessor.api;

import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.http.ResponseEntity.ok;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import java.time.Month;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;
import org.zalando.problem.violations.ConstraintViolationProblem;
import se.sundsvall.billingpreprocessor.api.model.InvoiceFileStatus;
import se.sundsvall.billingpreprocessor.service.StatusService;
import se.sundsvall.dept44.common.validators.annotation.ValidMunicipalityId;

@RestController
@Validated
@RequestMapping("/{municipalityId}/status")
@Tag(name = "Status Resource", description = "Check status operations")
@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = {
	Problem.class, ConstraintViolationProblem.class
})))
@ApiResponse(responseCode = "500", description = "Internal Server error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
class StatusResource {

	private final StatusService statusService;

	StatusResource(final StatusService statusService) {
		this.statusService = statusService;
	}

	@Operation(summary = "Get the status for invoice files, indicating if they have been sent successfully or not", description = "Checks the status of a invoice file matching the given year and month")
	@ApiResponse(responseCode = "200", description = "Operation successful", useReturnTypeSchema = true)
	@GetMapping
	ResponseEntity<List<InvoiceFileStatus>> getFileStatusesForMonth(
		@ValidMunicipalityId @PathVariable final String municipalityId,
		@RequestParam @NotNull final Integer year,
		@RequestParam @NotNull final Month month) {
		return ok(statusService.getInvoiceFilesForMonth(municipalityId, year, month));
	}

}
