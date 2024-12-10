package se.sundsvall.billingpreprocessor.service.creator.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ExternalInvoiceFloatTypeHandlerTest {

	private ExternalInvoiceFloatTypeHandler handler = new ExternalInvoiceFloatTypeHandler();

	@ParameterizedTest
	@MethodSource("formatArgumentProvider")
	void format(String pattern, Object input, String expected) {
		handler.setPattern(pattern);
		assertThat(handler.format(input)).isEqualTo(expected);
	}

	private static Stream<Arguments> formatArgumentProvider() {
		return Stream.of(
			// Following 4 are floats and should be converted by handler
			Arguments.of("+0000000000", 13.37f, "+0000001337"),
			Arguments.of(null, 13.37f, "1337"),
			Arguments.of("+0000000000", Float.valueOf(13.37f), "+0000001337"),
			Arguments.of(null, Float.valueOf(13.37f), "1337"),

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
