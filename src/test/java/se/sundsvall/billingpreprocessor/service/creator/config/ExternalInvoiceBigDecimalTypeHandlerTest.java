package se.sundsvall.billingpreprocessor.service.creator.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ExternalInvoiceBigDecimalTypeHandlerTest {

	private final ExternalInvoiceBigDecimalTypeHandler handler = new ExternalInvoiceBigDecimalTypeHandler();

	@ParameterizedTest
	@MethodSource("formatArgumentProvider")
	void format(String pattern, Object input, String expected) {
		handler.setPattern(pattern);
		assertThat(handler.format(input)).isEqualTo(expected);
	}

	private static Stream<Arguments> formatArgumentProvider() {
		return Stream.of(
			// Following are BigDecimal and should be converted by handler
			Arguments.of("+0000000000", BigDecimal.valueOf(13.37d), "+0000001337"),
			Arguments.of(null, BigDecimal.valueOf(13.37d), "1337"),
			Arguments.of(null, BigDecimal.valueOf(13.375d), "1338"),
			Arguments.of(null, BigDecimal.valueOf(-13.37d), "-1337"),
			Arguments.of(null, BigDecimal.valueOf(-13.375d), "-1338"),

			// Below are not BigDecimals and should be transformed to null
			Arguments.of("+0000000000", 13.37f, null),
			Arguments.of(null, Float.valueOf(13.37f), null),
			Arguments.of("+0000000000", 13.37d, null),
			Arguments.of(null, 13.37d, null),
			Arguments.of("+0000000000", 1337, null),
			Arguments.of(null, 1337, null),
			Arguments.of(null, null, null),
			Arguments.of("+0000000000", null, null),
			Arguments.of(null, "value", null),
			Arguments.of("+0000000000", "value", null));
	}
}
