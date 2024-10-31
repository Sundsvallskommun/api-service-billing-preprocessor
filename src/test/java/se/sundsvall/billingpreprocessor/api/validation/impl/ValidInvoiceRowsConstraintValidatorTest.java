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
import java.util.stream.Stream;

import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
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

	private final ValidInvoiceRowsConstraintValidator validator = new ValidInvoiceRowsConstraintValidator();

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
	@MethodSource("faultyAccountInformationArgumentProvider")
	void withFaultyAccountInformation(Type type) {
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

	private static Stream<Arguments> faultyAccountInformationArgumentProvider() {
		// Invalid parameter scenarios
		return Stream.of(
			Arguments.of(EXTERNAL, createAccountInformation(null, "Counterpart", "Department", "Subaccount")),
			Arguments.of(INTERNAL, createAccountInformation(null, "Counterpart", "Department", "Subaccount")),
			Arguments.of(EXTERNAL, createAccountInformation("CostCenter", null, "Department", "Subaccount")),
			Arguments.of(INTERNAL, createAccountInformation("CostCenter", null, "Department", "Subaccount")),
			Arguments.of(EXTERNAL, createAccountInformation("CostCenter", "Counterpart", null, "Subaccount")),
			Arguments.of(INTERNAL, createAccountInformation("CostCenter", "Counterpart", null, "Subaccount")),
			Arguments.of(EXTERNAL, createAccountInformation("CostCenter", "Counterpart", "Department", null)),
			Arguments.of(INTERNAL, createAccountInformation("CostCenter", "Counterpart", "Department", null)),

			Arguments.of(EXTERNAL, createAccountInformation("", "Counterpart", "Department", "Subaccount")),
			Arguments.of(INTERNAL, createAccountInformation("", "Counterpart", "Department", "Subaccount")),
			Arguments.of(EXTERNAL, createAccountInformation("CostCenter", "", "Department", "Subaccount")),
			Arguments.of(INTERNAL, createAccountInformation("CostCenter", "", "Department", "Subaccount")),
			Arguments.of(EXTERNAL, createAccountInformation("CostCenter", "Counterpart", "", "Subaccount")),
			Arguments.of(INTERNAL, createAccountInformation("CostCenter", "Counterpart", "", "Subaccount")),
			Arguments.of(EXTERNAL, createAccountInformation("CostCenter", "Counterpart", "Department", "")),
			Arguments.of(INTERNAL, createAccountInformation("CostCenter", "Counterpart", "Department", "")),

			Arguments.of(EXTERNAL, createAccountInformation(" ", "Counterpart", "Department", "Subaccount")),
			Arguments.of(INTERNAL, createAccountInformation(" ", "Counterpart", "Department", "Subaccount")),
			Arguments.of(EXTERNAL, createAccountInformation("CostCenter", " ", "Department", "Subaccount")),
			Arguments.of(INTERNAL, createAccountInformation("CostCenter", " ", "Department", "Subaccount")),
			Arguments.of(EXTERNAL, createAccountInformation("CostCenter", "Counterpart", " ", "Subaccount")),
			Arguments.of(INTERNAL, createAccountInformation("CostCenter", "Counterpart", " ", "Subaccount")),
			Arguments.of(EXTERNAL, createAccountInformation("CostCenter", "Counterpart", "Department", " ")),
			Arguments.of(INTERNAL, createAccountInformation("CostCenter", "Counterpart", "Department", " ")));
	}

	private static AccountInformation createAccountInformation(String costCenter, String counterpart, String department, String subAccount) {
		return AccountInformation.create()
			.withCostCenter(costCenter)
			.withCounterpart(counterpart)
			.withDepartment(department)
			.withSubaccount(subAccount);
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
