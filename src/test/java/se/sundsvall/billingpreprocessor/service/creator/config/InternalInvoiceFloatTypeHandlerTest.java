package se.sundsvall.billingpreprocessor.service.creator.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class InternalInvoiceFloatTypeHandlerTest {

	private InternalInvoiceFloatTypeHandler handler = new InternalInvoiceFloatTypeHandler();

	@ParameterizedTest
	@MethodSource("formatArgumentProvider")
	void format(String pattern, Object input, String expected) {
		handler.setPattern(pattern);
		assertThat(handler.format(input)).isEqualTo(expected);
	}

	private static Stream<Arguments> formatArgumentProvider() {
		return Stream.of(
			// Following 7 are floats and should be converted by handler
			Arguments.of("+0000000000", 13.37f, "+0000000013"),
			Arguments.of("+00000000.00", 13.37f, "+00000013.37"),
			Arguments.of(null, 13.37123f, "13.37"),
			Arguments.of("+0000000000", Float.valueOf(13.37f), "+0000000013"),
			Arguments.of("+#000.000", Float.valueOf(13.37f), "+013.370"),
			Arguments.of(null, Float.valueOf(13.37f), "13.37"),
			Arguments.of(null, Float.valueOf(13.375f), "13.38"),

			// Below is not floats and should be transformed to null
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
