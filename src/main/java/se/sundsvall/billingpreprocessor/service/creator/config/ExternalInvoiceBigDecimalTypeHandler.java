package se.sundsvall.billingpreprocessor.service.creator.config;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.beanio.types.BigDecimalTypeHandler;

public class ExternalInvoiceBigDecimalTypeHandler extends BigDecimalTypeHandler {
	public static final String NAME = "externalInvoiceBigDecimalTypeHandler";

	@Override
	public String format(Object value) {
		return ofNullable(value)
			.filter(BigDecimal.class::isInstance)
			.map(BigDecimal.class::cast)
			.map(bd -> bd.setScale(2, RoundingMode.HALF_UP)) // Set scale to 2 and round by HALF_UP (e.g. 1.235 -> 1.24 and 1.234999 -> 1.23)
			.map(bd -> bd.movePointRight(2)) // Move the two first decimals into the integer part of the number (e.g. 1.23 -> 123)
			.map(this::format)
			.orElse(null);
	}

	private String format(BigDecimal value) {
		if (isNull(getPattern())) {
			return String.valueOf(value.intValue());
		}

		return createDecimalFormat().format(value);
	}
}
