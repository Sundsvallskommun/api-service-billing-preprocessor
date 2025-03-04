package se.sundsvall.billingpreprocessor.api.validation.impl;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNoneBlank;
import static se.sundsvall.billingpreprocessor.api.model.enums.Type.INTERNAL;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import se.sundsvall.billingpreprocessor.api.model.BillingRecord;
import se.sundsvall.billingpreprocessor.api.model.Invoice;
import se.sundsvall.billingpreprocessor.api.validation.ValidInvoice;

public class ValidInvoiceConstraintValidator implements ConstraintValidator<ValidInvoice, BillingRecord> {

	private static final String CUSTOM_ERROR_MESSAGE_MISSING_OUR_REFERENCE = "invoice.ourReference is mandatory when billing record is of type " + INTERNAL;

	@Override
	public boolean isValid(final BillingRecord billingRecord, final ConstraintValidatorContext context) {
		var isValid = true;

		if (billingRecord.getType() == INTERNAL && nonNull(billingRecord.getInvoice())) {
			if (!isValidOurReference(billingRecord.getInvoice())) {
				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate(CUSTOM_ERROR_MESSAGE_MISSING_OUR_REFERENCE).addConstraintViolation();
				isValid = false;
			}
		}
		return isValid;
	}

	private boolean isValidOurReference(Invoice invoice) {
		return isNoneBlank(invoice.getOurReference());
	}

}
