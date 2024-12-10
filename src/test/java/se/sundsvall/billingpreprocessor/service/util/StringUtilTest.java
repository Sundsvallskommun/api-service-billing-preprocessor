package se.sundsvall.billingpreprocessor.service.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class StringUtilTest {

	@ParameterizedTest
	@MethodSource("removeHyphenArgumentProvider")
	void removeHyphen(String input, String expectedResponse) {
		assertThat(StringUtil.formatLegalId(input)).isEqualTo(expectedResponse);
	}

	private static Stream<Arguments> removeHyphenArgumentProvider() {
		return Stream.of(
			Arguments.of(null, null),
			Arguments.of("123456789012", "3456789012"),
			Arguments.of("1234567890", "1234567890"),
			Arguments.of("12345678-9012", "3456789012"),
			Arguments.of("123456-7890", "1234567890"),
			Arguments.of("123456", "123456"),
			Arguments.of("ABCDEF", "ABCDEF"),
			Arguments.of("1-2-3-4-5-6", "123456"),
			Arguments.of("-1-2-3-", "123"),
			Arguments.of("-A-2-C-", "-A-2-C-"),
			Arguments.of("1A2B3C4D5E6F", "1A2B3C4D5E6F"),
			Arguments.of("1A-2B-3C-4D-5E-6F", "1A-2B-3C-4D-5E-6F"));
	}
}
