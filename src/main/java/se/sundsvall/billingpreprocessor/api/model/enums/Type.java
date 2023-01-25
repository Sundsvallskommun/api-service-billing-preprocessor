package se.sundsvall.billingpreprocessor.api.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Billing type model", enumAsRef = true)
public enum Type {
	EXTERNAL,
	INTERNAL;
}
