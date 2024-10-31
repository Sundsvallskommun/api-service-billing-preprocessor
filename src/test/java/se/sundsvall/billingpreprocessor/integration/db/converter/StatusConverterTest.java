package se.sundsvall.billingpreprocessor.integration.db.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import se.sundsvall.billingpreprocessor.integration.db.model.enums.Status;

class StatusConverterTest {

	private final StatusConverter converter = new StatusConverter();

	@ParameterizedTest
	@EnumSource(value = Status.class)
	void testConvertToDatabaseColumn(Status status) {
		final var value = converter.convertToDatabaseColumn(status);
		assertThat(value)
			.isNotNull()
			.isEqualTo(status.toString());
	}

	@Test
	void testConvertToDatabaseColumn_whenNullValue_shouldReturnNull() {
		final var value = converter.convertToDatabaseColumn(null);
		assertThat(value).isNull();
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"NEW", "APPROVED", "INVOICED", "REJECTED"
	})
	void testConvertToEntityAttribute(String string) {
		final var value = converter.convertToEntityAttribute(string);
		assertThat(value)
			.isNotNull()
			.isEqualTo(Status.valueOf(string));
	}

	@Test
	void testConvertToEntityAttribute_whenMissingValue_should() {
		assertThatExceptionOfType(IllegalArgumentException.class)
			.isThrownBy(() -> converter.convertToEntityAttribute("noMatch"))
			.withMessage("No enum constant se.sundsvall.billingpreprocessor.integration.db.model.enums.Status.noMatch");
	}
}
