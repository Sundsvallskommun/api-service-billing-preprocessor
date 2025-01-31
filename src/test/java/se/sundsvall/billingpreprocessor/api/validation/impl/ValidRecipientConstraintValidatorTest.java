package se.sundsvall.billingpreprocessor.api.validation.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static se.sundsvall.billingpreprocessor.api.model.enums.Type.EXTERNAL;
import static se.sundsvall.billingpreprocessor.api.model.enums.Type.INTERNAL;

import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.billingpreprocessor.api.model.BillingRecord;
import se.sundsvall.billingpreprocessor.api.model.Recipient;

@ExtendWith(MockitoExtension.class)
class ValidRecipientConstraintValidatorTest {

	@Mock
	private ConstraintValidatorContext contextMock;

	@Mock
	private ConstraintViolationBuilder builderMock;

	private ValidRecipientConstraintValidator validator = new ValidRecipientConstraintValidator();

	@Test
	void withExternalTypeAndValidRecipient() {
		assertThat(validator.isValid(BillingRecord.create().withType(EXTERNAL).withRecipient(Recipient.create().withPartyId("partyId").withLegalId("legalId")
			.withOrganizationName("organizationName")), contextMock)).isTrue();
		assertThat(validator.isValid(BillingRecord.create().withType(EXTERNAL).withRecipient(Recipient.create().withPartyId("partyId").withLegalId("legalId")
			.withFirstName("firstName").withLastName("lastName")), contextMock)).isTrue();

		verifyNoInteractions(contextMock, builderMock);
	}

	@Test
	void withInternalType() {
		assertThat(validator.isValid(BillingRecord.create().withType(INTERNAL), contextMock)).isTrue();
		assertThat(validator.isValid(BillingRecord.create().withType(INTERNAL).withRecipient(Recipient.create()), contextMock)).isTrue();

		verifyNoInteractions(contextMock, builderMock);
	}

	@Test
	void withExternalTypeAndRecipientWithNoName() {
		when(contextMock.buildConstraintViolationWithTemplate(any())).thenReturn(builderMock);

		assertThat(validator.isValid(BillingRecord.create().withType(EXTERNAL).withRecipient(Recipient.create().withPartyId("partyId").withLegalId("legalId")), contextMock)).isFalse();

		verify(contextMock).disableDefaultConstraintViolation();
		verify(contextMock).buildConstraintViolationWithTemplate("recipient must either have an organization name or a first and last name defined");
		verify(builderMock).addConstraintViolation();
	}

	@Test
	void withExternalTypeAndRecipientWithNoFirstName() {
		when(contextMock.buildConstraintViolationWithTemplate(any())).thenReturn(builderMock);

		assertThat(validator.isValid(BillingRecord.create().withType(EXTERNAL).withRecipient(Recipient.create().withPartyId("partyId").withLegalId("legalId").withFirstName("firstName")), contextMock)).isFalse();

		verify(contextMock).disableDefaultConstraintViolation();
		verify(contextMock).buildConstraintViolationWithTemplate("recipient must either have an organization name or a first and last name defined");
		verify(builderMock).addConstraintViolation();
	}

	@Test
	void withExternalTypeAndRecipientWithNoLastName() {
		when(contextMock.buildConstraintViolationWithTemplate(any())).thenReturn(builderMock);

		assertThat(validator.isValid(BillingRecord.create().withType(EXTERNAL).withRecipient(Recipient.create().withPartyId("partyId").withLegalId("legalId").withLastName("lastName")), contextMock)).isFalse();

		verify(contextMock).disableDefaultConstraintViolation();
		verify(contextMock).buildConstraintViolationWithTemplate("recipient must either have an organization name or a first and last name defined");
		verify(builderMock).addConstraintViolation();
	}

	@Test
	void withExternalTypeAndRecipientWithPrivateAndOrganizationName() {
		when(contextMock.buildConstraintViolationWithTemplate(any())).thenReturn(builderMock);

		assertThat(validator.isValid(BillingRecord.create().withType(EXTERNAL).withRecipient(Recipient.create().withPartyId("partyId").withLegalId("legalId").withFirstName("firstName").withLastName("lastName")
			.withOrganizationName("organizationName")), contextMock)).isFalse();

		verify(contextMock).disableDefaultConstraintViolation();
		verify(contextMock).buildConstraintViolationWithTemplate("recipient must either have an organization name or a first and last name defined");
		verify(builderMock).addConstraintViolation();
	}

	@Test
	void withExternalTypeAndRecipientNotPresent() {
		when(contextMock.buildConstraintViolationWithTemplate(any())).thenReturn(builderMock);

		assertThat(validator.isValid(BillingRecord.create().withType(EXTERNAL), contextMock)).isFalse();

		verify(contextMock).disableDefaultConstraintViolation();
		verify(contextMock).buildConstraintViolationWithTemplate("recipient can not be null when billing record is of type EXTERNAL");
		verify(builderMock).addConstraintViolation();
	}

	@Test
	void withExternalTypeAndRecipientWithNoPartyIdButWithLegalId() {
		assertThat(validator.isValid(BillingRecord.create().withType(EXTERNAL).withRecipient(Recipient.create()
			.withOrganizationName("organizationName").withLegalId("legalId")), contextMock)).isTrue();

		verifyNoInteractions(contextMock, builderMock);
	}

	@Test
	void withExternalTypeAndRecipientWithNoLegalIdOrNoPartyId() {
		when(contextMock.buildConstraintViolationWithTemplate(any())).thenReturn(builderMock);

		assertThat(validator.isValid(BillingRecord.create().withType(EXTERNAL).withRecipient(Recipient.create()
			.withOrganizationName("organizationName")), contextMock)).isFalse();

		verify(contextMock).disableDefaultConstraintViolation();
		verify(contextMock).buildConstraintViolationWithTemplate("recipient must have partyId or legalId when billing record is of type EXTERNAL");
		verify(builderMock).addConstraintViolation();
	}
}
