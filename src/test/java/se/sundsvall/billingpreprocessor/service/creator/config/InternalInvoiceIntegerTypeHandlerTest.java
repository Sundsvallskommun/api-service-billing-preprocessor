package se.sundsvall.billingpreprocessor.service.creator.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class InternalInvoiceIntegerTypeHandlerTest {

	private InternalInvoiceIntegerTypeHandler handler = new InternalInvoiceIntegerTypeHandler();

	@ParameterizedTest
	@MethodSource("formatArgumentProvider")
	void format(String pattern, Object input, String expected) {
		handler.setPattern(pattern);
		assertThat(handler.format(input)).isEqualTo(expected);
	}

	private static Stream<Arguments> formatArgumentProvider() {
		return Stream.of(
			// Following 4 are integers and should be converted by handler
			Arguments.of("+0000000000", 1337, "+0000001337"),
			Arguments.of("+00000000.00", 1337, "+00001337.00"),
			Arguments.of(null, 1337, "1337.00"),
			Arguments.of("+0000000000", Integer.valueOf(1337), "+0000001337"),
			Arguments.of("+00000.000", Integer.valueOf(1337), "+01337.000"),
			Arguments.of(null, Integer.valueOf(1337), "1337.00"),

			// Below is not integers and should be transformed to null
			Arguments.of("+0000000000", 13.37d, null),
			Arguments.of(null, 13.37d, null),
			Arguments.of("+0000000000", 1337f, null),
			Arguments.of(null, 1337f, null),
			Arguments.of(null, null, null),
			Arguments.of("+0000000000", null, null),
			Arguments.of(null, "value", null),
			Arguments.of("+0000000000", "value", null));
	}
}
