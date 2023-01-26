package se.sundsvall.billingpreprocessor.api.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Billing status model", enumAsRef = true, example = "APPROVED")
public enum Status {
	NEW,
	APPROVED,
	INVOICED,
	REJECTED;
}
