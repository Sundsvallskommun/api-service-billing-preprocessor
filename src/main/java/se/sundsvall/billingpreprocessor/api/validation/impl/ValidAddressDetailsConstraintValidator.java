package se.sundsvall.billingpreprocessor.api.validation.impl;

import static org.apache.commons.lang3.StringUtils.isNoneBlank;
import static se.sundsvall.billingpreprocessor.api.model.enums.Type.EXTERNAL;

import java.util.Optional;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import se.sundsvall.billingpreprocessor.api.model.BillingRecord;
import se.sundsvall.billingpreprocessor.api.validation.ValidAddressDetails;

public class ValidAddressDetailsConstraintValidator implements ConstraintValidator<ValidAddressDetails, BillingRecord> {
	@Override
	public boolean isValid(final BillingRecord billingRecord, final ConstraintValidatorContext constraintValidatorContext) {

		if (EXTERNAL == billingRecord.getType()) {
			return Optional.ofNullable(billingRecord)
				.filter(this::isNonNullAddressDetails)
				.filter(this::isValidStreet)
				.filter(this::isValidPostalCode)
				.filter(this::isValidCity)
				.isPresent();
		}
		return true;
	}

	private boolean isNonNullAddressDetails(final BillingRecord billingRecord) {
		return billingRecord.getIssuer() != null && billingRecord.getIssuer().getAddressDetails() != null;
	}

	private boolean isValidStreet(final BillingRecord billingRecord) {
		return isNoneBlank(billingRecord.getIssuer().getAddressDetails().getStreet());
	}

	private boolean isValidPostalCode(final BillingRecord billingRecord) {
		return isNoneBlank(billingRecord.getIssuer().getAddressDetails().getPostalCode());
	}

	private boolean isValidCity(final BillingRecord billingRecord) {
		return isNoneBlank(billingRecord.getIssuer().getAddressDetails().getCity());
	}
}
