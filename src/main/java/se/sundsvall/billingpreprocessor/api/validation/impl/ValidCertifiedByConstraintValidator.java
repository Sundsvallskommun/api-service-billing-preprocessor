package se.sundsvall.billingpreprocessor.api.validation.impl;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static se.sundsvall.billingpreprocessor.api.model.enums.Status.CERTIFIED;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import se.sundsvall.billingpreprocessor.api.model.BillingRecord;
import se.sundsvall.billingpreprocessor.api.validation.ValidCertifiedBy;

public class ValidCertifiedByConstraintValidator implements ConstraintValidator<ValidCertifiedBy, BillingRecord> {
	private static final String CUSTOM_ERROR_MESSAGE = "certifiedBy must be present when status is " + CERTIFIED;

	@Override
	public boolean isValid(final BillingRecord billingRecord, final ConstraintValidatorContext context) {
		final var isValid = billingRecord.getStatus() != CERTIFIED || isNotBlank(billingRecord.getCertifiedBy());

		if (!isValid) {
			useCustomMessageForValidation(context);
		}

		return isValid;
	}

	private void useCustomMessageForValidation(ConstraintValidatorContext constraintContext) {
		constraintContext.disableDefaultConstraintViolation();
		constraintContext.buildConstraintViolationWithTemplate(CUSTOM_ERROR_MESSAGE).addConstraintViolation();
	}
}
