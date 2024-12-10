package se.sundsvall.billingpreprocessor.api.validation.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.billingpreprocessor.api.model.enums.Type.EXTERNAL;
import static se.sundsvall.billingpreprocessor.api.model.enums.Type.INTERNAL;

import org.junit.jupiter.api.Test;
import se.sundsvall.billingpreprocessor.api.model.AddressDetails;
import se.sundsvall.billingpreprocessor.api.model.BillingRecord;
import se.sundsvall.billingpreprocessor.api.model.Recipient;

class ValidAddressDetailsConstraintValidatorTest {

	private ValidAddressDetailsConstraintValidator validator = new ValidAddressDetailsConstraintValidator();

	@Test
	void withInternalType() {
		assertThat(validator.isValid(BillingRecord.create().withType(INTERNAL).withRecipient(Recipient.create()), null)).isTrue();
	}

	@Test
	void withExternalTypeAndValidAddressDetails() {
		assertThat(validator.isValid(BillingRecord.create()
			.withType(EXTERNAL)
			.withRecipient(Recipient.create().withAddressDetails(AddressDetails.create()
				.withCity("City")
				.withStreet("Street")
				.withtPostalCode("Postal code"))), null))
			.isTrue();
	}

	@Test
	void withExternalTypeAndMissingCity() {
		assertThat(validator.isValid(BillingRecord.create()
			.withType(EXTERNAL)
			.withRecipient(Recipient.create().withAddressDetails(AddressDetails.create()
				.withStreet("Street")
				.withtPostalCode("Postal code"))), null))
			.isFalse();
	}

	@Test
	void withExternalTypeAndMissingStreet() {
		assertThat(validator.isValid(BillingRecord.create()
			.withType(EXTERNAL)
			.withRecipient(Recipient.create().withAddressDetails(AddressDetails.create()
				.withCity("City")
				.withtPostalCode("Postal code"))), null))
			.isFalse();
	}

	@Test
	void withExternalTypeAndMissingPostalCode() {
		assertThat(validator.isValid(BillingRecord.create()
			.withType(EXTERNAL)
			.withRecipient(Recipient.create().withAddressDetails(AddressDetails.create()
				.withCity("City")
				.withStreet("Street"))), null))
			.isFalse();
	}
}
