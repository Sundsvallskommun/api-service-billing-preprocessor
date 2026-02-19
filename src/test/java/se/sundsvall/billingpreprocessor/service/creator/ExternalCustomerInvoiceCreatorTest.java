package se.sundsvall.billingpreprocessor.service.creator;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExternalCustomerInvoiceCreatorTest {

	@Test
	void testExternalCustomerInvoiceCreator_extendsExternalInvoiceCreator() {
		assertThat(ExternalCustomerInvoiceCreator.class).isAssignableTo(ExternalInvoiceCreator.class);
	}
}
