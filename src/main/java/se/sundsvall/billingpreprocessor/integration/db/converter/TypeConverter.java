package se.sundsvall.billingpreprocessor.integration.db.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import se.sundsvall.billingpreprocessor.integration.db.model.enums.Type;

@Converter(autoApply = true)
public class TypeConverter implements AttributeConverter<Type, String> {

	@Override
	public String convertToDatabaseColumn(Type attribute) {
		if (attribute == null) {
			return null;
		}
		return attribute.toString();
	}

	@Override
	public Type convertToEntityAttribute(String columnValue) {
		return Type.valueOf(columnValue);
	}
}
