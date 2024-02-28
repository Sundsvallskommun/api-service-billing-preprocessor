package se.sundsvall.billingpreprocessor.integration.db.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import se.sundsvall.billingpreprocessor.integration.db.model.enums.Status;

@Converter(autoApply = true)
public class StatusConverter implements AttributeConverter<Status, String> {

	@Override
	public String convertToDatabaseColumn(Status attribute) {
		if (attribute == null) {
			return null;
		}
		return attribute.toString();
	}

	@Override
	public Status convertToEntityAttribute(String columnValue) {
		return Status.valueOf(columnValue);
	}
}
