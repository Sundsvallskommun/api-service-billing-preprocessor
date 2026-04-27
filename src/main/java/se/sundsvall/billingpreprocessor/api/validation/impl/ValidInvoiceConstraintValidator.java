package se.sundsvall.billingpreprocessor.api.validation.impl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;
import se.sundsvall.billingpreprocessor.api.model.BillingRecord;
import se.sundsvall.billingpreprocessor.api.model.Invoice;
import se.sundsvall.billingpreprocessor.api.validation.ValidInvoice;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNoneBlank;
import static se.sundsvall.billingpreprocessor.api.model.enums.Type.EXTERNAL;
import static se.sundsvall.billingpreprocessor.api.model.enums.Type.INTERNAL;

public class ValidInvoiceConstraintValidator implements ConstraintValidator<ValidInvoice, BillingRecord> {

	private static final String CUSTOM_ERROR_MESSAGE_MISSING_OUR_REFERENCE = "invoice.ourReference is mandatory when billing record is of type " + INTERNAL;
	private static final String CUSTOM_ERROR_MESSAGE_MISSING_DESCRIPTION = "invoice.description is mandatory when billing record is of type " + INTERNAL;
	private static final String CUSTOM_ERROR_MESSAGE_DESCRIPTION_TOO_LONG_TEMPLATE = "invoice.description must not exceed %d characters when billing record is of type %s";

	private final int maxDescriptionLengthInternal;
	private final int maxDescriptionLengthExternal;

	public ValidInvoiceConstraintValidator(
		@Value("${validation.invoice.description.max-length.internal}") final int maxDescriptionLengthInternal,
		@Value("${validation.invoice.description.max-length.external}") final int maxDescriptionLengthExternal) {
		this.maxDescriptionLengthInternal = maxDescriptionLengthInternal;
		this.maxDescriptionLengthExternal = maxDescriptionLengthExternal;
	}

	@Override
	public boolean isValid(final BillingRecord billingRecord, final ConstraintValidatorContext context) {
		var isValid = true;

		if (billingRecord.getType() == INTERNAL && nonNull(billingRecord.getInvoice())) {
			if (!isValidOurReference(billingRecord.getInvoice())) {
				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate(CUSTOM_ERROR_MESSAGE_MISSING_OUR_REFERENCE).addPropertyNode("invoice.ourReference").addConstraintViolation();
				isValid = false;
			}
			if (!isValidDescription(billingRecord.getInvoice())) {
				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate(CUSTOM_ERROR_MESSAGE_MISSING_DESCRIPTION).addPropertyNode("invoice.description").addConstraintViolation();
				isValid = false;
			} else if (!isValidDescriptionLength(billingRecord.getInvoice(), maxDescriptionLengthInternal)) {
				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate(CUSTOM_ERROR_MESSAGE_DESCRIPTION_TOO_LONG_TEMPLATE.formatted(maxDescriptionLengthInternal, INTERNAL)).addPropertyNode("invoice.description").addConstraintViolation();
				isValid = false;
			}
		} else if (billingRecord.getType() == EXTERNAL && nonNull(billingRecord.getInvoice())) {
			if (!isValidDescriptionLength(billingRecord.getInvoice(), maxDescriptionLengthExternal)) {
				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate(CUSTOM_ERROR_MESSAGE_DESCRIPTION_TOO_LONG_TEMPLATE.formatted(maxDescriptionLengthExternal, EXTERNAL)).addPropertyNode("invoice.description").addConstraintViolation();
				isValid = false;
			}
		}
		return isValid;
	}

	private boolean isValidOurReference(Invoice invoice) {
		return isNoneBlank(invoice.getOurReference());
	}

	private boolean isValidDescription(Invoice invoice) {
		return isNoneBlank(invoice.getDescription());
	}

	private boolean isValidDescriptionLength(Invoice invoice, int maxLength) {
		return invoice.getDescription() == null || invoice.getDescription().length() <= maxLength;
	}

}
