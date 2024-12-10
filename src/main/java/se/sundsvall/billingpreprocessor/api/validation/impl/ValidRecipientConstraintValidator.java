package se.sundsvall.billingpreprocessor.api.validation.impl;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isAllBlank;
import static org.apache.commons.lang3.StringUtils.isNoneBlank;
import static se.sundsvall.billingpreprocessor.api.model.enums.Type.EXTERNAL;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import se.sundsvall.billingpreprocessor.api.model.BillingRecord;
import se.sundsvall.billingpreprocessor.api.model.Recipient;
import se.sundsvall.billingpreprocessor.api.validation.ValidRecipient;

public class ValidRecipientConstraintValidator implements ConstraintValidator<ValidRecipient, BillingRecord> {
	private static final String CUSTOM_ERROR_RECIPIENT_MISSING_MESSAGE = "recipient can not be null when billing record is of type " + EXTERNAL;
	private static final String CUSTOM_ERROR_INVALID_PARTY_ID_OR_LEGAL_ID_MESSAGE = "recipient must have partyId or legalId when billing record is of type " + EXTERNAL;

	private static final String CUSTOM_ERROR_INVALID_RECIPIENT_NAME_MESSAGE = "recipient must either have an organization name or a first and last name defined";

	@Override
	public boolean isValid(final BillingRecord billingRecord, final ConstraintValidatorContext context) {
		boolean isValid = true;

		if (EXTERNAL == billingRecord.getType()) {
			if (isNull(billingRecord.getRecipient())) {
				useCustomMessageForValidation(context, CUSTOM_ERROR_RECIPIENT_MISSING_MESSAGE);
				return false;
			}
			if (!isValidOrganizationName(billingRecord.getRecipient()) && !isValidFirstAndLastName(billingRecord.getRecipient())) {
				useCustomMessageForValidation(context, CUSTOM_ERROR_INVALID_RECIPIENT_NAME_MESSAGE);
				isValid = false;
			}
			if (!isValidPartyId(billingRecord.getRecipient()) && !isValidLegalId(billingRecord.getRecipient())) {
				useCustomMessageForValidation(context, CUSTOM_ERROR_INVALID_PARTY_ID_OR_LEGAL_ID_MESSAGE);
				isValid = false;
			}

		}

		return isValid;
	}

	private boolean isValidPartyId(Recipient recipient) {
		return isNoneBlank(recipient.getPartyId());
	}

	private boolean isValidLegalId(Recipient recipient) {
		return isNoneBlank(recipient.getLegalId());
	}

	private boolean isValidOrganizationName(Recipient recipient) {
		return isNoneBlank(recipient.getOrganizationName()) && isAllBlank(recipient.getFirstName(), recipient.getLastName());
	}

	private boolean isValidFirstAndLastName(Recipient recipient) {
		return isNoneBlank(recipient.getFirstName(), recipient.getLastName()) && isAllBlank(recipient.getOrganizationName());
	}

	private void useCustomMessageForValidation(ConstraintValidatorContext constraintContext, String customMessage) {
		constraintContext.disableDefaultConstraintViolation();
		constraintContext.buildConstraintViolationWithTemplate(customMessage).addConstraintViolation();
	}
}
