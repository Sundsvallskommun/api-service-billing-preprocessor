package se.sundsvall.billingpreprocessor.service.creator.config;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;

import org.beanio.types.FloatTypeHandler;

public class ExternalInvoiceFloatTypeHandler extends FloatTypeHandler {
	public static final String NAME = "externalInvoiceFloatTypeHandler";

	@Override
	public String format(Object value) {
		return ofNullable(value)
			.filter(Float.class::isInstance)
			.map(Float.class::cast)
			.map(f -> f * 100) // Move the two first decimals into the integer part of the number
			.map(Double::valueOf)
			.map(this::format)
			.orElse(null);
	}

	private String format(Double value) {
		if (isNull(getPattern())) {
			return String.valueOf(value.intValue());
		}

		return createDecimalFormat().format(value);
	}
}
