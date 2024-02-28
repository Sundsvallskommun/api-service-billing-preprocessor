package se.sundsvall.billingpreprocessor.integration.db.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import se.sundsvall.billingpreprocessor.integration.db.model.enums.InvoiceFileStatus;

class InvoiceFileStatusConverterTest {

	private final InvoiceFileStatusConverter converter = new InvoiceFileStatusConverter();

	@ParameterizedTest
	@EnumSource(value = InvoiceFileStatus.class)
	void testConvertToDatabaseColumn(InvoiceFileStatus invoiceFileStatus) {
		final var value = converter.convertToDatabaseColumn(invoiceFileStatus);
		assertThat(value)
			.isNotNull()
			.isEqualTo(invoiceFileStatus.toString());
	}

	@Test
	void testConvertToDatabaseColumn_whenNullValue_shouldReturnNull() {
		final var value = converter.convertToDatabaseColumn(null);
		assertThat(value).isNull();
	}

	@ParameterizedTest
	@ValueSource(strings = { "GENERATED", "SEND_SUCCESSFUL", "SEND_FAILED" })
	void testConvertToEntityAttribute(String string) {
		final var value = converter.convertToEntityAttribute(string);
		assertThat(value)
			.isNotNull()
			.isEqualTo(InvoiceFileStatus.valueOf(string));
	}

	@Test
	void testConvertToEntityAttribute_whenMissingValue_should() {
		assertThatExceptionOfType(IllegalArgumentException.class)
			.isThrownBy(() -> converter.convertToEntityAttribute("noMatch"))
			.withMessage("No enum constant se.sundsvall.billingpreprocessor.integration.db.model.enums.InvoiceFileStatus.noMatch");
	}
}
