package se.sundsvall.billingpreprocessor.api.validation.impl;


import se.sundsvall.billingpreprocessor.api.model.BillingRecord;
import se.sundsvall.billingpreprocessor.api.validation.ValidAddressDetails;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isNoneBlank;
import static se.sundsvall.billingpreprocessor.api.model.enums.Type.EXTERNAL;

public class ValidAddressDetailsConstraintValidator implements ConstraintValidator<ValidAddressDetails, BillingRecord> {
	@Override
	public boolean isValid(BillingRecord billingRecord, ConstraintValidatorContext constraintValidatorContext) {

		if(EXTERNAL == billingRecord.getType()) {
			return Optional.ofNullable(billingRecord)
					.filter(this::isNonNullAddressDetails)
					.filter(this::isValidStreet)
					.filter(this::isValidPostalCode)
					.filter(this::isValidCity)
					.isPresent();
		}
		return true;
	}

	private boolean isNonNullAddressDetails(BillingRecord record) {
		return record.getIssuer() != null && record.getIssuer().getAddressDetails() != null;
	}

	private boolean isValidStreet(BillingRecord record){
		return isNoneBlank(record.getIssuer().getAddressDetails().getStreet());
	}

	private boolean isValidPostalCode(BillingRecord record) {
		return isNoneBlank(record.getIssuer().getAddressDetails().getPostalCode());
	}

	private boolean isValidCity(BillingRecord record) {
		return isNoneBlank(record.getIssuer().getAddressDetails().getCity());
	}
}
