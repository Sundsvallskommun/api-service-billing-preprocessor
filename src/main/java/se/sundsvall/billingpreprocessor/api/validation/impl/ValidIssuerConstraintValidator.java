package se.sundsvall.billingpreprocessor.api.validation.impl;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isAllBlank;
import static org.apache.commons.lang3.StringUtils.isNoneBlank;
import static se.sundsvall.billingpreprocessor.api.model.enums.Type.EXTERNAL;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import se.sundsvall.billingpreprocessor.api.model.BillingRecord;
import se.sundsvall.billingpreprocessor.api.model.Issuer;
import se.sundsvall.billingpreprocessor.api.validation.ValidIssuer;

public class ValidIssuerConstraintValidator implements ConstraintValidator<ValidIssuer, BillingRecord> {
	private static final String CUSTOM_ERROR_ISSUER_MISSING_MESSAGE = "issuer can not be null when billing record is of type " + EXTERNAL;
	private static final String CUSTOM_ERROR_ISSUER_PARTY_ID_MISSING = "issuer must have partyId when billing record is of type " + EXTERNAL;
	private static final String CUSTOM_ERROR_INVALID_ISSUER_NAME_MESSAGE = "issuer must either have an organization name or a first and last name defined";

	@Override
	public boolean isValid(final BillingRecord billingRecord, final ConstraintValidatorContext context) {
		boolean isValid = true;

		if (EXTERNAL == billingRecord.getType()) {
			if (isNull(billingRecord.getIssuer())) {
				useCustomMessageForValidation(context, CUSTOM_ERROR_ISSUER_MISSING_MESSAGE);
				return false;
			}
			if (!isValidOrganizationName(billingRecord.getIssuer()) && !isValidFirstAndLastName(billingRecord.getIssuer())) {
				useCustomMessageForValidation(context, CUSTOM_ERROR_INVALID_ISSUER_NAME_MESSAGE);
				isValid = false;
			}
			if (!isValidPartyId(billingRecord.getIssuer())) {
				useCustomMessageForValidation(context, CUSTOM_ERROR_ISSUER_PARTY_ID_MISSING);
				isValid = false;
			}
		}

		return isValid;
	}

	private boolean isValidPartyId(Issuer issuer) {
		return isNoneBlank(issuer.getPartyId());
	}

	private boolean isValidOrganizationName(Issuer issuer) {
		return isNoneBlank(issuer.getOrganizationName()) && isAllBlank(issuer.getFirstName(), issuer.getLastName());
	}

	private boolean isValidFirstAndLastName(Issuer issuer) {
		return isNoneBlank(issuer.getFirstName(), issuer.getLastName()) && isAllBlank(issuer.getOrganizationName());
	}

	private void useCustomMessageForValidation(ConstraintValidatorContext constraintContext, String customMessage) {
		constraintContext.disableDefaultConstraintViolation();
		constraintContext.buildConstraintViolationWithTemplate(customMessage).addConstraintViolation();
	}
}
