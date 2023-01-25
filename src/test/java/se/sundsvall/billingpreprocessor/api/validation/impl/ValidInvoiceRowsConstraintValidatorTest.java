package se.sundsvall.billingpreprocessor.api.validation.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static se.sundsvall.billingpreprocessor.api.BillingRecordRequestUtil.createAccountInformationInstance;
import static se.sundsvall.billingpreprocessor.api.model.enums.Type.EXTERNAL;
import static se.sundsvall.billingpreprocessor.api.model.enums.Type.INTERNAL;

import java.util.List;

import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import se.sundsvall.billingpreprocessor.api.model.AccountInformation;
import se.sundsvall.billingpreprocessor.api.model.BillingRecord;
import se.sundsvall.billingpreprocessor.api.model.Invoice;
import se.sundsvall.billingpreprocessor.api.model.InvoiceRow;
import se.sundsvall.billingpreprocessor.api.model.enums.Type;

@ExtendWith(MockitoExtension.class)
class ValidInvoiceRowsConstraintValidatorTest {

	@Mock
	private ConstraintValidatorContext contextMock;

	@Mock
	private ConstraintViolationBuilder builderMock;

	private ValidInvoiceRowsConstraintValidator validator = new ValidInvoiceRowsConstraintValidator();

	@Test
	void withNoInvoices() {
		assertThat(validator.isValid(BillingRecord.create(), contextMock)).isTrue();

		verifyNoInteractions(contextMock, builderMock);
	}

	@Test
	void withNoInvoiceRows() {
		assertThat(validator.isValid(BillingRecord.create().withInvoice(Invoice.create()), contextMock)).isTrue();

		verifyNoInteractions(contextMock, builderMock);
	}

	@Test
	void withInvoiceRowsContainingNoPricePerUnit() {
		final var billingRecord = BillingRecord.create().withInvoice(Invoice.create().withInvoiceRows(List.of(InvoiceRow.create()
				.withAccountInformation(createAccountInformationInstance(true)))));

		assertThat(validator.isValid(billingRecord, contextMock)).isTrue();

		verifyNoInteractions(contextMock, builderMock);
	}

	@Test
	void withInternalTypeAndVatPresent() {
		final var billingRecord = BillingRecord.create().withType(INTERNAL).withInvoice(Invoice.create()
				.withInvoiceRows(List.of(InvoiceRow.create()
						.withAccountInformation(createAccountInformationInstance(true))
						.withCostPerUnit(10f)
						.withVatCode("25"))));

		when(contextMock.buildConstraintViolationWithTemplate(any())).thenReturn(builderMock);

		assertThat(validator.isValid(billingRecord, contextMock)).isFalse();

		verify(contextMock).disableDefaultConstraintViolation();
		verify(contextMock).buildConstraintViolationWithTemplate("can not contain vat code information on invoice rows when billing record is of type INTERNAL");
		verify(builderMock).addConstraintViolation();
	}

	@Test
	void withExternalTypeAndVatNotPresent() {
		final var billingRecord = BillingRecord.create().withType(EXTERNAL).withInvoice(Invoice.create().withInvoiceRows(List.of(InvoiceRow.create()
				.withAccountInformation(createAccountInformationInstance(true))
				.withCostPerUnit(10f))));

		when(contextMock.buildConstraintViolationWithTemplate(any())).thenReturn(builderMock);

		assertThat(validator.isValid(billingRecord, contextMock)).isFalse();

		verify(contextMock).disableDefaultConstraintViolation();
		verify(contextMock).buildConstraintViolationWithTemplate("must contain vat code information on invoice rows when billing record is of type EXTERNAL");
		verify(builderMock).addConstraintViolation();
	}

	@ParameterizedTest
	@EnumSource(value = Type.class)
	void withNoAccountInformation(Type type) {
		final var billingRecord = BillingRecord.create().withType(type).withInvoice(Invoice.create().withInvoiceRows(List.of(InvoiceRow.create())));

		when(contextMock.buildConstraintViolationWithTemplate(any())).thenReturn(builderMock);

		assertThat(validator.isValid(billingRecord, contextMock)).isFalse();

		verify(contextMock).disableDefaultConstraintViolation();
		verify(contextMock).buildConstraintViolationWithTemplate("at least one invoice row must have accountInformation");
		verify(builderMock).addConstraintViolation();
	}

	@ParameterizedTest
	@EnumSource(value = Type.class)
	void withAccountInformationMissingCounterPart(Type type) {
		final var billingRecord = BillingRecord.create().withType(type).withInvoice(Invoice.create().withInvoiceRows(List.of(InvoiceRow.create()
				.withAccountInformation(AccountInformation.create()
						.withDepartment("Department")
						.withSubaccount("Subaccount")
						.withCostCenter("CostCenter")))));

		when(contextMock.buildConstraintViolationWithTemplate(any())).thenReturn(builderMock);

		assertThat(validator.isValid(billingRecord, contextMock)).isFalse();

		verify(contextMock).disableDefaultConstraintViolation();
		verify(contextMock).buildConstraintViolationWithTemplate("when accountInformation is present costCenter, subaccount, department and counterpart are mandatory");
		verify(builderMock).addConstraintViolation();
	}

	@ParameterizedTest
	@EnumSource(value = Type.class)
	void withAccountInformationMissingDepartment(Type type) {
		final var billingRecord = BillingRecord.create().withType(type).withInvoice(Invoice.create().withInvoiceRows(List.of(InvoiceRow.create()
				.withAccountInformation(createAccountInformationInstance(true).withDepartment(null)))));

		when(contextMock.buildConstraintViolationWithTemplate(any())).thenReturn(builderMock);

		assertThat(validator.isValid(billingRecord, contextMock)).isFalse();

		verify(contextMock).disableDefaultConstraintViolation();
		verify(contextMock).buildConstraintViolationWithTemplate("when accountInformation is present costCenter, subaccount, department and counterpart are mandatory");
		verify(builderMock).addConstraintViolation();
	}
	@ParameterizedTest
	@EnumSource(value = Type.class)
	void withAccountInformationMissingSubaccount(Type type) {
		final var billingRecord = BillingRecord.create().withType(type).withInvoice(Invoice.create().withInvoiceRows(List.of(InvoiceRow.create()
				.withAccountInformation(createAccountInformationInstance(true).withSubaccount(null)))));

		when(contextMock.buildConstraintViolationWithTemplate(any())).thenReturn(builderMock);

		assertThat(validator.isValid(billingRecord, contextMock)).isFalse();

		verify(contextMock).disableDefaultConstraintViolation();
		verify(contextMock).buildConstraintViolationWithTemplate("when accountInformation is present costCenter, subaccount, department and counterpart are mandatory");
		verify(builderMock).addConstraintViolation();
	}

	@ParameterizedTest
	@EnumSource(value = Type.class)
	void withAccountInformationMissingCostCenter(Type type) {
		final var billingRecord = BillingRecord.create().withType(type).withInvoice(Invoice.create().withInvoiceRows(List.of(InvoiceRow.create()
				.withAccountInformation(createAccountInformationInstance(true).withCostCenter(null)))));

		when(contextMock.buildConstraintViolationWithTemplate(any())).thenReturn(builderMock);

		assertThat(validator.isValid(billingRecord, contextMock)).isFalse();

		verify(contextMock).disableDefaultConstraintViolation();
		verify(contextMock).buildConstraintViolationWithTemplate("when accountInformation is present costCenter, subaccount, department and counterpart are mandatory");
		verify(builderMock).addConstraintViolation();
	}

	@ParameterizedTest
	@EnumSource(value = Type.class)
	void withAccountInformationInCompleteOnOneRow(Type type) {
		final var billingRecord = BillingRecord.create().withType(type).withInvoice(Invoice.create()
				.withInvoiceRows(List.of(
						InvoiceRow.create().withAccountInformation(createAccountInformationInstance(true)),
						InvoiceRow.create().withAccountInformation(createAccountInformationInstance(true).withCounterpart(null)))));

		when(contextMock.buildConstraintViolationWithTemplate(any())).thenReturn(builderMock);

		assertThat(validator.isValid(billingRecord, contextMock)).isFalse();

		verify(contextMock).disableDefaultConstraintViolation();
		verify(contextMock).buildConstraintViolationWithTemplate("when accountInformation is present costCenter, subaccount, department and counterpart are mandatory");
		verify(builderMock).addConstraintViolation();
	}
}
