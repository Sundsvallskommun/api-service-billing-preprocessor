package se.sundsvall.billingpreprocessor.service.creator;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class InternalSalaryAndPensionInvoiceCreatorTest {

	@Test
	void testInternalSalaryAndPensionInvoiceCreator_extendsInternalInvoiceCreator() {
		assertThat(InternalSalaryAndPensionInvoiceCreator.class).isAssignableTo(InternalInvoiceCreator.class);
	}
}
