package se.sundsvall.billingpreprocessor.api.validation.impl;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static se.sundsvall.billingpreprocessor.api.BillingRecordRequestUtil.createAccountInformationInstance;
import static se.sundsvall.billingpreprocessor.api.model.enums.Type.EXTERNAL;
import static se.sundsvall.billingpreprocessor.api.model.enums.Type.INTERNAL;

import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
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

	private static final BigDecimal AMOUNT = BigDecimal.valueOf(112d);
	private static final String COSTCENTER = "costcenter";
	private static final String COUNTERPART = "counterpart";
	private static final String DEPARTMENT = "department";
	private static final String SUBACCOUNT = "subaccount";

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
			.withAccountInformation(List.of(createAccountInformationInstance(true))))));

		assertThat(validator.isValid(billingRecord, contextMock)).isTrue();

		verifyNoInteractions(contextMock, builderMock);
	}

	@Test
	void withInternalTypeAndVatPresent() {
		final var billingRecord = BillingRecord.create().withType(INTERNAL).withInvoice(Invoice.create()
			.withInvoiceRows(List.of(InvoiceRow.create()
				.withAccountInformation(List.of(createAccountInformationInstance(true)))
				.withCostPerUnit(BigDecimal.valueOf(10d))
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
			.withAccountInformation(List.of(createAccountInformationInstance(true)))
			.withCostPerUnit(BigDecimal.valueOf(10d)))));

		when(contextMock.buildConstraintViolationWithTemplate(any())).thenReturn(builderMock);

		assertThat(validator.isValid(billingRecord, contextMock)).isFalse();

		verify(contextMock).disableDefaultConstraintViolation();
		verify(contextMock).buildConstraintViolationWithTemplate("must contain vat code information on invoice rows when billing record is of type EXTERNAL");
		verify(builderMock).addConstraintViolation();
	}

	@ParameterizedTest
	@EnumSource(value = Type.class)
	void withAccountInformationNull(Type type) {
		final var billingRecord = BillingRecord.create().withType(type).withInvoice(Invoice.create().withInvoiceRows(List.of(InvoiceRow.create())));

		when(contextMock.buildConstraintViolationWithTemplate(any())).thenReturn(builderMock);

		assertThat(validator.isValid(billingRecord, contextMock)).isFalse();

		verify(contextMock).disableDefaultConstraintViolation();
		verify(contextMock).buildConstraintViolationWithTemplate("at least one invoice row must have accountInformation");
		verify(builderMock).addConstraintViolation();
	}

	@ParameterizedTest
	@EnumSource(value = Type.class)
	void withAccountInformationEmptyList(Type type) {
		final var billingRecord = BillingRecord.create().withType(type).withInvoice(Invoice.create().withInvoiceRows(List.of(InvoiceRow.create().withAccountInformation(emptyList()))));

		when(contextMock.buildConstraintViolationWithTemplate(any())).thenReturn(builderMock);

		assertThat(validator.isValid(billingRecord, contextMock)).isFalse();

		verify(contextMock).disableDefaultConstraintViolation();
		verify(contextMock).buildConstraintViolationWithTemplate("at least one invoice row must have accountInformation");
		verify(builderMock).addConstraintViolation();
	}

	@ParameterizedTest
	@EnumSource(value = Type.class)
	void withAccountInformationLIstOnlyContainsNullValues(Type type) {
		final var billingRecord = BillingRecord.create().withType(type).withInvoice(Invoice.create().withInvoiceRows(List.of(InvoiceRow.create().withAccountInformation(new ArrayList<>(Collections.nCopies(3, null))))));

		when(contextMock.buildConstraintViolationWithTemplate(any())).thenReturn(builderMock);

		assertThat(validator.isValid(billingRecord, contextMock)).isFalse();

		verify(contextMock).disableDefaultConstraintViolation();
		verify(contextMock).buildConstraintViolationWithTemplate("at least one invoice row must have accountInformation");
		verify(builderMock).addConstraintViolation();
	}

	@ParameterizedTest
	@MethodSource("faultyAccountInformationArgumentProvider")
	void withFaultyAccountInformation(Type type, AccountInformation accountInformation) {
		final var billingRecord = BillingRecord.create().withType(type).withInvoice(Invoice.create().withInvoiceRows(List.of(InvoiceRow.create()
			.withAccountInformation(List.of(accountInformation)))));

		when(contextMock.buildConstraintViolationWithTemplate(any())).thenReturn(builderMock);

		assertThat(validator.isValid(billingRecord, contextMock)).isFalse();

		verify(contextMock).disableDefaultConstraintViolation();
		verify(contextMock).buildConstraintViolationWithTemplate("amount, costCenter, subaccount, department and counterpart must be present for invoice rows containing accountInformation");
		verify(builderMock).addConstraintViolation();
	}

	private static Stream<Arguments> faultyAccountInformationArgumentProvider() {
		// Invalid parameter scenarios
		return Stream.of(
			Arguments.of(EXTERNAL, createAccountInformation(null, COSTCENTER, COUNTERPART, DEPARTMENT, SUBACCOUNT)),
			Arguments.of(INTERNAL, createAccountInformation(null, COSTCENTER, COUNTERPART, DEPARTMENT, SUBACCOUNT)),
			Arguments.of(EXTERNAL, createAccountInformation(null, COSTCENTER, COUNTERPART, DEPARTMENT, SUBACCOUNT)),
			Arguments.of(INTERNAL, createAccountInformation(null, COSTCENTER, COUNTERPART, DEPARTMENT, SUBACCOUNT)),
			Arguments.of(EXTERNAL, createAccountInformation(null, COSTCENTER, COUNTERPART, DEPARTMENT, SUBACCOUNT)),
			Arguments.of(INTERNAL, createAccountInformation(null, COSTCENTER, COUNTERPART, DEPARTMENT, SUBACCOUNT)),
			Arguments.of(EXTERNAL, createAccountInformation(null, COSTCENTER, COUNTERPART, DEPARTMENT, SUBACCOUNT)),
			Arguments.of(INTERNAL, createAccountInformation(null, COSTCENTER, COUNTERPART, DEPARTMENT, SUBACCOUNT)),

			Arguments.of(EXTERNAL, createAccountInformation(AMOUNT, null, COUNTERPART, DEPARTMENT, SUBACCOUNT)),
			Arguments.of(INTERNAL, createAccountInformation(AMOUNT, null, COUNTERPART, DEPARTMENT, SUBACCOUNT)),
			Arguments.of(EXTERNAL, createAccountInformation(AMOUNT, COSTCENTER, null, DEPARTMENT, SUBACCOUNT)),
			Arguments.of(INTERNAL, createAccountInformation(AMOUNT, COSTCENTER, null, DEPARTMENT, SUBACCOUNT)),
			Arguments.of(EXTERNAL, createAccountInformation(AMOUNT, COSTCENTER, COUNTERPART, null, SUBACCOUNT)),
			Arguments.of(INTERNAL, createAccountInformation(AMOUNT, COSTCENTER, COUNTERPART, null, SUBACCOUNT)),
			Arguments.of(EXTERNAL, createAccountInformation(AMOUNT, COSTCENTER, COUNTERPART, DEPARTMENT, null)),
			Arguments.of(INTERNAL, createAccountInformation(AMOUNT, COSTCENTER, COUNTERPART, DEPARTMENT, null)),

			Arguments.of(EXTERNAL, createAccountInformation(AMOUNT, "", COUNTERPART, DEPARTMENT, SUBACCOUNT)),
			Arguments.of(INTERNAL, createAccountInformation(AMOUNT, "", COUNTERPART, DEPARTMENT, SUBACCOUNT)),
			Arguments.of(EXTERNAL, createAccountInformation(AMOUNT, COSTCENTER, "", DEPARTMENT, SUBACCOUNT)),
			Arguments.of(INTERNAL, createAccountInformation(AMOUNT, COSTCENTER, "", DEPARTMENT, SUBACCOUNT)),
			Arguments.of(EXTERNAL, createAccountInformation(AMOUNT, COSTCENTER, COUNTERPART, "", SUBACCOUNT)),
			Arguments.of(INTERNAL, createAccountInformation(AMOUNT, COSTCENTER, COUNTERPART, "", SUBACCOUNT)),
			Arguments.of(EXTERNAL, createAccountInformation(AMOUNT, COSTCENTER, COUNTERPART, DEPARTMENT, "")),
			Arguments.of(INTERNAL, createAccountInformation(AMOUNT, COSTCENTER, COUNTERPART, DEPARTMENT, "")),

			Arguments.of(EXTERNAL, createAccountInformation(AMOUNT, " ", COUNTERPART, DEPARTMENT, SUBACCOUNT)),
			Arguments.of(INTERNAL, createAccountInformation(AMOUNT, " ", COUNTERPART, DEPARTMENT, SUBACCOUNT)),
			Arguments.of(EXTERNAL, createAccountInformation(AMOUNT, COSTCENTER, " ", DEPARTMENT, SUBACCOUNT)),
			Arguments.of(INTERNAL, createAccountInformation(AMOUNT, COSTCENTER, " ", DEPARTMENT, SUBACCOUNT)),
			Arguments.of(EXTERNAL, createAccountInformation(AMOUNT, COSTCENTER, COUNTERPART, " ", SUBACCOUNT)),
			Arguments.of(INTERNAL, createAccountInformation(AMOUNT, COSTCENTER, COUNTERPART, " ", SUBACCOUNT)),
			Arguments.of(EXTERNAL, createAccountInformation(AMOUNT, COSTCENTER, COUNTERPART, DEPARTMENT, " ")),
			Arguments.of(INTERNAL, createAccountInformation(AMOUNT, COSTCENTER, COUNTERPART, DEPARTMENT, " ")));
	}

	private static AccountInformation createAccountInformation(BigDecimal amount, String costCenter, String counterpart, String department, String subAccount) {
		return AccountInformation.create()
			.withAmount(amount)
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
				InvoiceRow.create().withAccountInformation(List.of(createAccountInformationInstance(true))),
				InvoiceRow.create().withAccountInformation(List.of(createAccountInformationInstance(true).withCounterpart(null))))));

		when(contextMock.buildConstraintViolationWithTemplate(any())).thenReturn(builderMock);

		assertThat(validator.isValid(billingRecord, contextMock)).isFalse();

		verify(contextMock).disableDefaultConstraintViolation();
		verify(contextMock).buildConstraintViolationWithTemplate("amount, costCenter, subaccount, department and counterpart must be present for invoice rows containing accountInformation");
		verify(builderMock).addConstraintViolation();
	}
}
