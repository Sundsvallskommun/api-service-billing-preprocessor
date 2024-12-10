package se.sundsvall.billingpreprocessor.api.validation.impl;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static se.sundsvall.billingpreprocessor.api.model.enums.Status.APPROVED;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import se.sundsvall.billingpreprocessor.api.model.BillingRecord;
import se.sundsvall.billingpreprocessor.api.validation.ValidApprovedBy;

public class ValidApprovedByConstraintValidator implements ConstraintValidator<ValidApprovedBy, BillingRecord> {
	private static final String CUSTOM_ERROR_MESSAGE = "approvedBy must be present when status is " + APPROVED;

	@Override
	public boolean isValid(final BillingRecord billingRecord, final ConstraintValidatorContext context) {
		final var isValid = billingRecord.getStatus() != APPROVED || isNotBlank(billingRecord.getApprovedBy());

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
