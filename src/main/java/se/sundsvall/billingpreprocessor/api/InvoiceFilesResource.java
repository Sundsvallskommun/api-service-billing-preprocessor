package se.sundsvall.billingpreprocessor.api;

import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.http.ResponseEntity.noContent;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;
import org.zalando.problem.violations.ConstraintViolationProblem;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import se.sundsvall.billingpreprocessor.service.AsyncExecutorService;
import se.sundsvall.dept44.requestid.RequestId;

@RestController
@Validated
@RequestMapping("/invoicefiles")
@Tag(name = "Invoice", description = "Invoice file operations")
public class InvoiceFilesResource {

	private final AsyncExecutorService service;

	public InvoiceFilesResource(AsyncExecutorService service) {
		this.service = service;
	}

	@PostMapping(produces = APPLICATION_PROBLEM_JSON_VALUE)
	@Operation(summary = "Triggers creation of file entities from billing records flagged as APPROVED")
	@ApiResponse(responseCode = "204", description = "Successful Operation", useReturnTypeSchema = true)
	@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = { Problem.class, ConstraintViolationProblem.class })))
	@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	public ResponseEntity<Void> createFileEntities() {
		service.createFileEntities(RequestId.get());
		return noContent().build();
	}
}
