package se.sundsvall.billingpreprocessor.api.validation.impl;

import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.billingpreprocessor.api.model.AddressDetails;
import se.sundsvall.billingpreprocessor.api.model.BillingRecord;
import se.sundsvall.billingpreprocessor.api.model.Recipient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static se.sundsvall.billingpreprocessor.api.model.enums.Type.EXTERNAL;
import static se.sundsvall.billingpreprocessor.api.model.enums.Type.INTERNAL;

@ExtendWith(MockitoExtension.class)
class ValidAddressDetailsConstraintValidatorTest {

	@Mock
	private ConstraintValidatorContext contextMock;

	@Mock
	private ConstraintViolationBuilder builderMock;

	@Mock
	private NodeBuilderCustomizableContext nodeBuilderMock;

	private final ValidAddressDetailsConstraintValidator validator = new ValidAddressDetailsConstraintValidator();

	@Test
	void withInternalType() {
		assertThat(validator.isValid(BillingRecord.create().withType(INTERNAL).withRecipient(Recipient.create()), contextMock)).isTrue();

		verifyNoInteractions(builderMock);
	}

	@Test
	void withExternalTypeAndValidAddressDetails() {
		assertThat(validator.isValid(BillingRecord.create()
			.withType(EXTERNAL)
			.withRecipient(Recipient.create().withAddressDetails(AddressDetails.create()
				.withCity("City")
				.withStreet("Street")
				.withtPostalCode("Postal code"))), contextMock))
			.isTrue();

		verifyNoInteractions(builderMock);
	}

	@Test
	void withExternalTypeAndMissingCity() {
		when(contextMock.getDefaultConstraintMessageTemplate()).thenReturn("default message");
		when(contextMock.buildConstraintViolationWithTemplate(any())).thenReturn(builderMock);
		when(builderMock.addPropertyNode(any())).thenReturn(nodeBuilderMock);

		assertThat(validator.isValid(BillingRecord.create()
			.withType(EXTERNAL)
			.withRecipient(Recipient.create().withAddressDetails(AddressDetails.create()
				.withStreet("Street")
				.withtPostalCode("Postal code"))), contextMock))
			.isFalse();

		verify(contextMock).disableDefaultConstraintViolation();
		verify(nodeBuilderMock).addConstraintViolation();
	}

	@Test
	void withExternalTypeAndMissingStreet() {
		when(contextMock.getDefaultConstraintMessageTemplate()).thenReturn("default message");
		when(contextMock.buildConstraintViolationWithTemplate(any())).thenReturn(builderMock);
		when(builderMock.addPropertyNode(any())).thenReturn(nodeBuilderMock);

		assertThat(validator.isValid(BillingRecord.create()
			.withType(EXTERNAL)
			.withRecipient(Recipient.create().withAddressDetails(AddressDetails.create()
				.withCity("City")
				.withtPostalCode("Postal code"))), contextMock))
			.isFalse();

		verify(contextMock).disableDefaultConstraintViolation();
		verify(nodeBuilderMock).addConstraintViolation();
	}

	@Test
	void withExternalTypeAndMissingPostalCode() {
		when(contextMock.getDefaultConstraintMessageTemplate()).thenReturn("default message");
		when(contextMock.buildConstraintViolationWithTemplate(any())).thenReturn(builderMock);
		when(builderMock.addPropertyNode(any())).thenReturn(nodeBuilderMock);

		assertThat(validator.isValid(BillingRecord.create()
			.withType(EXTERNAL)
			.withRecipient(Recipient.create().withAddressDetails(AddressDetails.create()
				.withCity("City")
				.withStreet("Street"))), contextMock))
			.isFalse();

		verify(contextMock).disableDefaultConstraintViolation();
		verify(nodeBuilderMock).addConstraintViolation();
	}
}
