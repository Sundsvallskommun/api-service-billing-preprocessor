package se.sundsvall.billingpreprocessor.integration.db.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import se.sundsvall.billingpreprocessor.integration.db.model.enums.InvoiceFileStatus;

@Converter(autoApply = true)
public class InvoiceFileStatusConverter implements AttributeConverter<InvoiceFileStatus, String> {

	@Override
	public String convertToDatabaseColumn(InvoiceFileStatus attribute) {
		if (attribute == null) {
			return null;
		}
		return attribute.toString();
	}

	@Override
	public InvoiceFileStatus convertToEntityAttribute(String columnValue) {
		return InvoiceFileStatus.valueOf(columnValue);
	}
}
