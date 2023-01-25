package se.sundsvall.billingpreprocessor.api.validation.impl;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNoneBlank;
import static se.sundsvall.billingpreprocessor.api.model.enums.Type.EXTERNAL;
import static se.sundsvall.billingpreprocessor.api.model.enums.Type.INTERNAL;

import java.util.ArrayList;
import java.util.Optional;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.util.ObjectUtils;

import se.sundsvall.billingpreprocessor.api.model.BillingRecord;
import se.sundsvall.billingpreprocessor.api.model.Invoice;
import se.sundsvall.billingpreprocessor.api.model.InvoiceRow;
import se.sundsvall.billingpreprocessor.api.validation.ValidInvoice;

public class ValidInvoiceConstraintValidator implements ConstraintValidator<ValidInvoice, BillingRecord> {
	private static final String CUSTOM_ERROR_MESSAGE_INVOICE_ROW = "can not contain detailed description on invoice rows when billing record is of type " + INTERNAL;
	private static final String CUSTOM_ERROR_MESSAGE_MISSING_REFERENCE_ID = "invoice.referenceId is mandatory when billing record is of type " + INTERNAL;
	private static final String CUSTOM_ERROR_MESSAGE_MISSING_OUR_REFERENCE = "invoice.ourReference is mandatory when billing record is of type " + INTERNAL;
	private static final String CUSTOM_ERROR_MESSAGE_MISSING_CUSTOMER_REFERENCE = "invoice.customerReference is mandatory when billing record is of type " + EXTERNAL;

	@Override
	public boolean isValid(final BillingRecord billingRecord, final ConstraintValidatorContext context) {
		var isValid = true;

		if (billingRecord.getType() == INTERNAL && nonNull(billingRecord.getInvoice())) {

			if (!isValidReferenceId(billingRecord.getInvoice())) {
				useCustomMessageForValidation(context, CUSTOM_ERROR_MESSAGE_MISSING_REFERENCE_ID);
				isValid = false;
			}

			if (!isValidOurReference(billingRecord.getInvoice())) {
				useCustomMessageForValidation(context, CUSTOM_ERROR_MESSAGE_MISSING_OUR_REFERENCE);
				isValid = false;
			}

			if (!isValidInvoiceRows(billingRecord.getInvoice())) {
				useCustomMessageForValidation(context, CUSTOM_ERROR_MESSAGE_INVOICE_ROW);
				isValid = false;
			}
		}

		if (billingRecord.getType() == EXTERNAL && nonNull(billingRecord.getInvoice()) && !isValidCustomerReference(billingRecord.getInvoice())) {
			useCustomMessageForValidation(context, CUSTOM_ERROR_MESSAGE_MISSING_CUSTOMER_REFERENCE);
			isValid = false;
		}

		return isValid;
	}

	private boolean isValidReferenceId(Invoice invoice) {
		return isNoneBlank(invoice.getReferenceId());
	}

	private boolean isValidCustomerReference(Invoice invoice) {
		return isNoneBlank(invoice.getCustomerReference());
	}

	private boolean isValidOurReference(Invoice invoice) {
		return isNoneBlank(invoice.getOurReference());
	}

	private boolean isValidInvoiceRows(Invoice invoice) {
		// Verify that no detailed description rows exists for any of the provided invoice rows
		return Optional.ofNullable(invoice)
			.flatMap(i -> Optional.ofNullable(i.getInvoiceRows()))
			.orElseGet(ArrayList::new)
			.stream()
			.map(InvoiceRow::getDetailedDescriptions)
			.allMatch(ObjectUtils::isEmpty);
	}

	private void useCustomMessageForValidation(ConstraintValidatorContext constraintContext, String message) {
		constraintContext.disableDefaultConstraintViolation();
		constraintContext.buildConstraintViolationWithTemplate(message).addConstraintViolation();
	}
}
