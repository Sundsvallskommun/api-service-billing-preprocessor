package se.sundsvall.billingpreprocessor.api.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Billing status model", enumAsRef = true, example = "CERTIFIED")
public enum Status {
	NEW,
	CERTIFIED,
	INVOICED,
	REJECTED;
}
