package se.sundsvall.billingpreprocessor.service.creator;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InternalCustomerInvoiceCreatorTest {

	@Test
	void testInternalCustomerInvoiceCreator_extendsInternalInvoiceCreator() {
		assertThat(InternalCustomerInvoiceCreator.class).isAssignableTo(InternalInvoiceCreator.class);
	}
}
