package se.sundsvall.billingpreprocessor.service.creator;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ExternalSalaryAndPensionInvoiceCreatorTest {

	@Test
	void testExternalSalaryAndPensionInvoiceCreator_extendsExternalInvoiceCreator() {
		assertThat(ExternalSalaryAndPensionInvoiceCreator.class).isAssignableTo(ExternalInvoiceCreator.class);
	}
}
