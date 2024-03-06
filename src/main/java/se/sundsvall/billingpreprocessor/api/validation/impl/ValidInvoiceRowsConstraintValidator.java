package se.sundsvall.billingpreprocessor.api.validation.impl;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNoneBlank;
import static org.springframework.util.ObjectUtils.isEmpty;
import static se.sundsvall.billingpreprocessor.api.model.enums.Type.EXTERNAL;
import static se.sundsvall.billingpreprocessor.api.model.enums.Type.INTERNAL;

import java.util.Objects;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import se.sundsvall.billingpreprocessor.api.model.BillingRecord;
import se.sundsvall.billingpreprocessor.api.model.InvoiceRow;
import se.sundsvall.billingpreprocessor.api.validation.ValidInvoiceRows;

public class ValidInvoiceRowsConstraintValidator implements ConstraintValidator<ValidInvoiceRows, BillingRecord> {
	private static final String CUSTOM_ERROR_INTERNAL_TYPE = "can not contain vat code information on invoice rows when billing record is of type " + INTERNAL;
	private static final String CUSTOM_ERROR_EXTERNAL_TYPE = "must contain vat code information on invoice rows when billing record is of type " + EXTERNAL;
	private static final String CUSTOM_ERROR_ACCOUNT_INFORMATION_PRESENT = "at least one invoice row must have accountInformation";
	private static final String CUSTOM_ERROR_ACCOUNT_INFORMATION_COMPLETE = "when accountInformation is present costCenter, subaccount, department and counterpart are mandatory";

	@Override
	public boolean isValid(final BillingRecord billingRecord, final ConstraintValidatorContext context) {

		var isValid = true;
		// If invoice and invoice rows exists
		if (nonNull(billingRecord.getInvoice()) && !isEmpty(billingRecord.getInvoice().getInvoiceRows())) {
			final var isValidInvoiceRow = EXTERNAL == billingRecord.getType() ? isValidExternalInvoiceRows(billingRecord) : isValidInternalInvoiceRows(billingRecord);

			if (!isValidInvoiceRow) {
				useCustomMessageForValidation(context, INTERNAL == billingRecord.getType() ? CUSTOM_ERROR_INTERNAL_TYPE : CUSTOM_ERROR_EXTERNAL_TYPE);
				isValid = false;
			}

			if (!accountInformationIsPresent(billingRecord)) {
				useCustomMessageForValidation(context, CUSTOM_ERROR_ACCOUNT_INFORMATION_PRESENT);
				isValid = false;
			}

			if (!isValidAccountInformation(billingRecord)) {
				useCustomMessageForValidation(context, CUSTOM_ERROR_ACCOUNT_INFORMATION_COMPLETE);
				isValid = false;
			}
		}

		return isValid;
	}

	/**
	 * Verify that vat codes exists for all provided invoice rows with a defined cost
	 */
	private boolean isValidExternalInvoiceRows(final BillingRecord billingRecord) {
		return billingRecord.getInvoice().getInvoiceRows().stream()
			.filter(row -> nonNull(row.getCostPerUnit()))
			.map(InvoiceRow::getVatCode)
			.allMatch(Objects::nonNull);
	}

	/**
	 * Verify that no vat codes exists for any of the provided invoice rows
	 */
	private boolean isValidInternalInvoiceRows(final BillingRecord billingRecord) {
		return billingRecord.getInvoice().getInvoiceRows().stream()
			.filter(row -> nonNull(row.getCostPerUnit()))
			.map(InvoiceRow::getVatCode)
			.allMatch(Objects::isNull);
	}

	/**
	 * Verify that at least one row has accountInformation
	 */
	private boolean accountInformationIsPresent(final BillingRecord billingRecord) {
		return billingRecord.getInvoice().getInvoiceRows().stream()
			.map(InvoiceRow::getAccountInformation)
			.anyMatch(Objects::nonNull);

	}

	/**
	 * Verify that all row that has accountInformation is complete
	 */
	private boolean isValidAccountInformation(final BillingRecord billingRecord) {
		return billingRecord.getInvoice().getInvoiceRows().stream()
			.map(InvoiceRow::getAccountInformation)
			.filter(Objects::nonNull)
			.allMatch(accountInformation -> isNoneBlank(
				accountInformation.getCostCenter(),
				accountInformation.getSubaccount(),
				accountInformation.getDepartment(),
				accountInformation.getCounterpart()));
	}

	private void useCustomMessageForValidation(final ConstraintValidatorContext constraintContext, final String message) {
		constraintContext.disableDefaultConstraintViolation();
		constraintContext.buildConstraintViolationWithTemplate(message).addConstraintViolation();
	}
}
