package se.sundsvall.billingpreprocessor.api.validation.impl;

import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.billingpreprocessor.api.model.BillingRecord;
import se.sundsvall.billingpreprocessor.api.model.Invoice;
import se.sundsvall.billingpreprocessor.api.model.InvoiceRow;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static se.sundsvall.billingpreprocessor.api.model.enums.Type.EXTERNAL;
import static se.sundsvall.billingpreprocessor.api.model.enums.Type.INTERNAL;

@ExtendWith(MockitoExtension.class)
class ValidInvoiceConstraintValidatorTest {

	@Mock
	private ConstraintValidatorContext contextMock;

	@Mock
	private ConstraintViolationBuilder builderMock;

	private ValidInvoiceConstraintValidator validator = new ValidInvoiceConstraintValidator();

	@Test
	void withExternalType() {
		assertThat(validator.isValid(BillingRecord.create().withType(EXTERNAL), contextMock)).isTrue();

		verifyNoInteractions(contextMock, builderMock);
	}

	@Test
	void withExternalTypeAndCustomerReferenceNotPresent() {
		when(contextMock.buildConstraintViolationWithTemplate(any())).thenReturn(builderMock);

		assertThat(validator.isValid(BillingRecord.create().withType(EXTERNAL).withInvoice(Invoice.create()), contextMock)).isFalse();

		verify(contextMock).disableDefaultConstraintViolation();
		verify(contextMock).buildConstraintViolationWithTemplate("invoice.customerReference is mandatory when billing record is of type EXTERNAL");
		verify(builderMock).addConstraintViolation();
	}

	@Test
	void withInternalTypeAndInvoiceNotPresent() {
		assertThat(validator.isValid(BillingRecord.create().withType(INTERNAL), contextMock)).isTrue();

		verifyNoInteractions(contextMock, builderMock);
	}

	@Test
	void withInternalTypeAndInvoiceRowsNotPresent() {
		assertThat(validator.isValid(BillingRecord.create().withType(INTERNAL).withInvoice(Invoice.create().withReferenceId("refId").withOurReference("ourRef")), contextMock)).isTrue();

		verifyNoInteractions(contextMock, builderMock);
	}

	@Test
	void withInternalTypeAndDetailDescriptionsNotPresent() {
		assertThat(validator.isValid(BillingRecord.create().withType(INTERNAL).withInvoice(Invoice.create().withReferenceId("refId").withOurReference("ourRef").withInvoiceRows(List.of(InvoiceRow.create()))), contextMock)).isTrue();

		verifyNoInteractions(contextMock, builderMock);
	}

	@Test
	void withInternalTypeAndEmptyListAsDetailDescriptions() {
		final var billingRecord = BillingRecord.create().withType(INTERNAL).withInvoice(Invoice.create().withReferenceId("refId").withOurReference("ourRef").withInvoiceRows(List.of(InvoiceRow.create().withDetailedDescriptions(emptyList()))));

		assertThat(validator.isValid(billingRecord, contextMock)).isTrue();

		verifyNoInteractions(contextMock, builderMock);
	}

	@Test
	void withInternalTypeAndDetailDescriptionsPresent() {
		final var billingRecord = BillingRecord.create().withType(INTERNAL).withInvoice(Invoice.create().withReferenceId("refId").withOurReference("ourRef").withInvoiceRows(List.of(InvoiceRow.create().withDetailedDescriptions(List.of("detailedDescription")))));

		when(contextMock.buildConstraintViolationWithTemplate(any())).thenReturn(builderMock);

		assertThat(validator.isValid(billingRecord, contextMock)).isFalse();

		verify(contextMock).disableDefaultConstraintViolation();
		verify(contextMock).buildConstraintViolationWithTemplate("can not contain detailed description on invoice rows when billing record is of type INTERNAL");
		verify(builderMock).addConstraintViolation();
	}

	@Test
	void withInternalTypeAndReferenceIdNotPresent() {
		when(contextMock.buildConstraintViolationWithTemplate(any())).thenReturn(builderMock);

		assertThat(validator.isValid(BillingRecord.create().withType(INTERNAL).withInvoice(Invoice.create().withOurReference("ourRef").withInvoiceRows(List.of(InvoiceRow.create()))), contextMock)).isFalse();

		verify(contextMock).disableDefaultConstraintViolation();
		verify(contextMock).buildConstraintViolationWithTemplate("invoice.referenceId is mandatory when billing record is of type INTERNAL");
		verify(builderMock).addConstraintViolation();
	}

	@Test
	void withInternalTypeAndOurReferenceNotPresent() {
		when(contextMock.buildConstraintViolationWithTemplate(any())).thenReturn(builderMock);

		assertThat(validator.isValid(BillingRecord.create().withType(INTERNAL).withInvoice(Invoice.create().withReferenceId("refId").withInvoiceRows(List.of(InvoiceRow.create()))), contextMock)).isFalse();

		verify(contextMock).disableDefaultConstraintViolation();
		verify(contextMock).buildConstraintViolationWithTemplate("invoice.ourReference is mandatory when billing record is of type INTERNAL");
		verify(builderMock).addConstraintViolation();
	}
}
