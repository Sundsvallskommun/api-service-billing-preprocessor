package se.sundsvall.billingpreprocessor.service.creator.config;

import static java.util.Objects.isNull;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Optional;
import org.beanio.types.FloatTypeHandler;

public class InternalInvoiceFloatTypeHandler extends FloatTypeHandler {
	public static final String NAME = "internalInvoiceFloatTypeHandler";
	private static final DecimalFormatSymbols SYMBOLS = new DecimalFormatSymbols(Locale.getDefault());
	private static final DecimalFormat DEFAULT_FORMAT = new DecimalFormat("#.00;-#.00");

	public InternalInvoiceFloatTypeHandler() {
		SYMBOLS.setDecimalSeparator('.');
		DEFAULT_FORMAT.setDecimalFormatSymbols(SYMBOLS);
	}

	@Override
	public String format(Object value) {
		return Optional.ofNullable(value)
			.filter(Float.class::isInstance)
			.map(Float.class::cast)
			.map(this::format)
			.orElse(null);
	}

	private String format(Float value) {
		if (isNull(getPattern())) {
			return DEFAULT_FORMAT.format(value);
		}

		final var decimalFormat = createDecimalFormat();
		decimalFormat.setDecimalFormatSymbols(SYMBOLS);
		return decimalFormat.format(value);
	}
}
