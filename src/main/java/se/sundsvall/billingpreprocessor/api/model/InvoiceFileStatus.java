package se.sundsvall.billingpreprocessor.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;

@Schema(description = "InvoiceFile status model")
public record InvoiceFileStatus(
	String id,
	String name,
	String type,
	String status,
	String municipalityId,
	OffsetDateTime createdAt,
	OffsetDateTime sentAt) {
}
