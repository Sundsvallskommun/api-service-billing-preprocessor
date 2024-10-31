package se.sundsvall.billingpreprocessor.api;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.web.util.UriComponentsBuilder.fromPath;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;
import org.zalando.problem.violations.ConstraintViolationProblem;

import com.turkraft.springfilter.boot.Filter;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import se.sundsvall.billingpreprocessor.api.model.BillingRecord;
import se.sundsvall.billingpreprocessor.integration.db.model.BillingRecordEntity;
import se.sundsvall.billingpreprocessor.service.BillingRecordService;
import se.sundsvall.dept44.common.validators.annotation.ValidMunicipalityId;
import se.sundsvall.dept44.common.validators.annotation.ValidUuid;

@RestController
@Validated
@RequestMapping("/{municipalityId}/billingrecords")
@Tag(name = "BillingRecord", description = "Billing record operations")
public class BillingRecordsResource {

	private final BillingRecordService service;

	public BillingRecordsResource(BillingRecordService service) {
		this.service = service;
	}

	@PostMapping(consumes = APPLICATION_JSON_VALUE, produces = {
		ALL_VALUE, APPLICATION_PROBLEM_JSON_VALUE
	})
	@Operation(summary = "Create billing record", description = "Creates a new billing record defined by the supplied attributes")
	@ApiResponse(responseCode = "201", headers = @Header(name = LOCATION, schema = @Schema(type = "string")), description = "Successful operation", useReturnTypeSchema = true)
	@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = {
		Problem.class, ConstraintViolationProblem.class
	})))
	@ApiResponse(responseCode = "500", description = "Internal Server error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	public ResponseEntity<Void> createBillingRecord(
		@Parameter(name = "municipalityId", description = "Municipality ID", example = "2281") @PathVariable("municipalityId") @ValidMunicipalityId String municipalityId,
		@Valid @NotNull @RequestBody final BillingRecord billingRecord) {
		final var uuid = service.createBillingRecord(billingRecord, municipalityId);

		return created(fromPath("/{municipalityId}/billingrecords/{id}").buildAndExpand(municipalityId, uuid).toUri()).header(CONTENT_TYPE, ALL_VALUE).build();
	}

	@PostMapping(path = "/batch", consumes = APPLICATION_JSON_VALUE, produces = {
		APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE
	})
	@Operation(summary = "Create billing records", description = "Creates new billing records defined by the supplied attributes")
	@ApiResponse(responseCode = "201", headers = @Header(name = LOCATION, schema = @Schema(type = "string")), description = "Successful operation", useReturnTypeSchema = true)
	@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = {
		Problem.class, ConstraintViolationProblem.class
	})))
	@ApiResponse(responseCode = "500", description = "Internal Server error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	public ResponseEntity<List<String>> createBillingRecords(
		@Parameter(name = "municipalityId", description = "Municipality ID", example = "2281") @PathVariable("municipalityId") @ValidMunicipalityId String municipalityId,
		@Valid @NotNull @RequestBody final List<BillingRecord> billingRecords) {
		final var uuidList = service.createBillingRecords(billingRecords, municipalityId);

		return created(fromPath("/{municipalityId}/billingrecords/").buildAndExpand(municipalityId).toUri()).header(CONTENT_TYPE, ALL_VALUE)
			.body(uuidList);
	}

	@GetMapping(path = "/{id}", produces = {
		APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE
	})
	@Operation(summary = "Read billing record", description = "Fetches the billing record that matches the provided id")
	@ApiResponse(responseCode = "200", description = "Successful Operation", useReturnTypeSchema = true)
	@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = {
		Problem.class, ConstraintViolationProblem.class
	})))
	@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	public ResponseEntity<BillingRecord> readBillingRecord(
		@Parameter(name = "municipalityId", description = "Municipality ID", example = "2281") @PathVariable("municipalityId") @ValidMunicipalityId String municipalityId,
		@Parameter(name = "id", description = "BillingRecord id", example = "b82bd8ac-1507-4d9a-958d-369261eecc15") @ValidUuid @PathVariable final String id) {
		return ok(service.readBillingRecord(id, municipalityId));
	}

	@GetMapping(produces = {
		APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE
	})
	@Operation(summary = "Read matching billing records", description = "Query for billing records with or without filters. The resource allows the client a wide range of variations on how to filter the result.")
	@ApiResponse(responseCode = "200", description = "Successful Operation", useReturnTypeSchema = true)
	@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = {
		Problem.class, ConstraintViolationProblem.class
	})))
	@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	public ResponseEntity<Page<BillingRecord>> findBillingRecords(
		@Parameter(name = "municipalityId", description = "Municipality ID", example = "2281") @PathVariable("municipalityId") @ValidMunicipalityId String municipalityId,
		@Parameter(description = "Syntax description: [spring-filter](https://github.com/turkraft/spring-filter/blob/85730f950a5f8623159cc0eb4d737555f9382bb7/README.md#syntax)",
			example = "category : 'ACCESS_CARD' and status : 'NEW'",
			schema = @Schema(implementation = String.class)) @Filter final Specification<BillingRecordEntity> filter,
		@ParameterObject final Pageable pageable) {
		return ok(service.findBillingRecords(filter, pageable, municipalityId));
	}

	@PutMapping(path = "/{id}", consumes = APPLICATION_JSON_VALUE, produces = {
		APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE
	})
	@Operation(summary = "Update billing record", description = "Updates the billing record matching provided id with the supplied attributes")
	@ApiResponse(responseCode = "200", description = "Successful operation", useReturnTypeSchema = true)
	@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = {
		Problem.class, ConstraintViolationProblem.class
	})))
	@ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	@ApiResponse(responseCode = "500", description = "Internal Server error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	public ResponseEntity<BillingRecord> updateBillingRecord(
		@Parameter(name = "municipalityId", description = "Municipality ID", example = "2281") @PathVariable("municipalityId") @ValidMunicipalityId String municipalityId,
		@Parameter(name = "id", description = "BillingRecord id", example = "b82bd8ac-1507-4d9a-958d-369261eecc15") @ValidUuid @PathVariable final String id,
		@Valid @NotNull @RequestBody final BillingRecord billingRecord) {
		return ok(service.updateBillingRecord(id, billingRecord, municipalityId));
	}

	@DeleteMapping(path = "/{id}")
	@Operation(summary = "Delete billing record", description = "Deletes the billing record that matches the provided id, but only if status of record is 'NEW'. Otherwise a client error is returned.")
	@ApiResponse(responseCode = "204", description = "Successful operation", useReturnTypeSchema = true)
	@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = {
		Problem.class, ConstraintViolationProblem.class
	})))
	@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	@ApiResponse(responseCode = "405", description = "Method not allowed", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	@ApiResponse(responseCode = "500", description = "Internal Server error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	public ResponseEntity<Void> deleteBillingRecord(
		@Parameter(name = "municipalityId", description = "Municipality ID", example = "2281") @PathVariable("municipalityId") @ValidMunicipalityId String municipalityId,
		@Parameter(name = "id", description = "BillingRecord id", example = "b82bd8ac-1507-4d9a-958d-369261eecc15") @ValidUuid @PathVariable final String id) {
		service.deleteBillingRecord(id, municipalityId);
		return noContent().header(CONTENT_TYPE, ALL_VALUE).build();
	}
}
