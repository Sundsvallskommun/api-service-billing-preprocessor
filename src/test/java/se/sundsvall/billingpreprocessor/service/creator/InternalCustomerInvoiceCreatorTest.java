package se.sundsvall.billingpreprocessor.service.creator;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class InternalCustomerInvoiceCreatorTest {

	@Test
	void testInternalCustomerInvoiceCreator_extendsInternalInvoiceCreator() {
		assertThat(InternalCustomerInvoiceCreator.class).isAssignableTo(InternalInvoiceCreator.class);
	}
}
