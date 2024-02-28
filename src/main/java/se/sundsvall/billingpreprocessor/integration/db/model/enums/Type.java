package se.sundsvall.billingpreprocessor.integration.db.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Billing type model", enumAsRef = true)
public enum Type {
	EXTERNAL,
	INTERNAL;
}
