package se.sundsvall.billingpreprocessor.integration.db.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import se.sundsvall.billingpreprocessor.integration.db.model.enums.DescriptionType;

@Converter(autoApply = true)
public class DescriptionTypeConverter implements AttributeConverter<DescriptionType, String> {

	@Override
	public String convertToDatabaseColumn(DescriptionType attribute) {
		if (attribute == null) {
			return null;
		}
		return attribute.toString();
	}

	@Override
	public DescriptionType convertToEntityAttribute(String columnValue) {
		return DescriptionType.valueOf(columnValue);
	}
}
