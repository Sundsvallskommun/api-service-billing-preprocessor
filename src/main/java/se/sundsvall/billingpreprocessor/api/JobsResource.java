package se.sundsvall.billingpreprocessor.api;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.http.ResponseEntity.accepted;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import se.sundsvall.billingpreprocessor.service.AsyncExecutorService;
import se.sundsvall.dept44.requestid.RequestId;

@RestController
@Validated
@RequestMapping("/jobs")
@Tag(name = "Jobs", description = "Jobs resources")
public class JobsResource {

	private final AsyncExecutorService service;

	public JobsResource(AsyncExecutorService service) {
		this.service = service;
	}

	@PostMapping(path = "/filecreator", produces = APPLICATION_PROBLEM_JSON_VALUE)
	@Operation(summary = "Triggers job for creating files from billing records with status APPROVED")
	@ApiResponse(responseCode = "202", description = "Successful Operation", useReturnTypeSchema = true)
	@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	public ResponseEntity<Void> createFileEntities() {
		service.createFiles(RequestId.get());
		return accepted().header(CONTENT_TYPE, ALL_VALUE).build();
	}

	@PostMapping(path = "/filetransferrer", produces = APPLICATION_PROBLEM_JSON_VALUE)
	@Operation(summary = "Triggers job for transferring files with status CREATED or SEND_FAILED to final destination")
	@ApiResponse(responseCode = "202", description = "Successful Operation", useReturnTypeSchema = true)
	@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	public ResponseEntity<Void> transferFiles() {
		service.transferFiles(RequestId.get());
		return accepted().header(CONTENT_TYPE, ALL_VALUE).build();
	}
}
