package se.sundsvall.billingpreprocessor.service.creator;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ExternalCustomerInvoiceCreatorTest {

	@Test
	void testExternalCustomerInvoiceCreator_extendsExternalInvoiceCreator() {
		assertThat(ExternalCustomerInvoiceCreator.class).isAssignableTo(ExternalInvoiceCreator.class);
	}
}
