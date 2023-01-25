package se.sundsvall.billingpreprocessor.api.validation.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static se.sundsvall.billingpreprocessor.api.model.enums.Type.EXTERNAL;
import static se.sundsvall.billingpreprocessor.api.model.enums.Type.INTERNAL;

import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import se.sundsvall.billingpreprocessor.api.model.BillingRecord;
import se.sundsvall.billingpreprocessor.api.model.Issuer;

@ExtendWith(MockitoExtension.class)
class ValidIssuerConstraintValidatorTest {

	@Mock
	private ConstraintValidatorContext contextMock;

	@Mock
	private ConstraintViolationBuilder builderMock;

	private ValidIssuerConstraintValidator validator = new ValidIssuerConstraintValidator();

	@Test
	void withExternalTypeAndValidIssuer() {
		assertThat(validator.isValid(BillingRecord.create().withType(EXTERNAL).withIssuer(Issuer.create().withPartyId("partyId")
				.withOrganizationName("organizationName")), contextMock)).isTrue();
		assertThat(validator.isValid(BillingRecord.create().withType(EXTERNAL).withIssuer(Issuer.create().withPartyId("partyId")
				.withFirstName("firstName").withLastName("lastName")), contextMock)).isTrue();

		verifyNoInteractions(contextMock, builderMock);
	}

	@Test
	void withInternalType() {
		assertThat(validator.isValid(BillingRecord.create().withType(INTERNAL), contextMock)).isTrue();
		assertThat(validator.isValid(BillingRecord.create().withType(INTERNAL).withIssuer(Issuer.create()), contextMock)).isTrue();

		verifyNoInteractions(contextMock, builderMock);
	}

	@Test
	void withExternalTypeAndIssuerWithNoName() {
		when(contextMock.buildConstraintViolationWithTemplate(any())).thenReturn(builderMock);

		assertThat(validator.isValid(BillingRecord.create().withType(EXTERNAL).withIssuer(Issuer.create().withPartyId("partyId")), contextMock)).isFalse();

		verify(contextMock).disableDefaultConstraintViolation();
		verify(contextMock).buildConstraintViolationWithTemplate("issuer must either have an organization name or a first and last name defined");
		verify(builderMock).addConstraintViolation();
	}

	@Test
	void withExternalTypeAndIssuerWithNoFirstName() {
		when(contextMock.buildConstraintViolationWithTemplate(any())).thenReturn(builderMock);

		assertThat(validator.isValid(BillingRecord.create().withType(EXTERNAL).withIssuer(Issuer.create().withPartyId("partyId").withFirstName("firstName")), contextMock)).isFalse();

		verify(contextMock).disableDefaultConstraintViolation();
		verify(contextMock).buildConstraintViolationWithTemplate("issuer must either have an organization name or a first and last name defined");
		verify(builderMock).addConstraintViolation();
	}

	@Test
	void withExternalTypeAndIssuerWithNoLastName() {
		when(contextMock.buildConstraintViolationWithTemplate(any())).thenReturn(builderMock);

		assertThat(validator.isValid(BillingRecord.create().withType(EXTERNAL).withIssuer(Issuer.create().withPartyId("partyId").withLastName("lastName")), contextMock)).isFalse();

		verify(contextMock).disableDefaultConstraintViolation();
		verify(contextMock).buildConstraintViolationWithTemplate("issuer must either have an organization name or a first and last name defined");
		verify(builderMock).addConstraintViolation();
	}

	@Test
	void withExternalTypeAndIssuerWithPrivateAndOrganizationName() {
		when(contextMock.buildConstraintViolationWithTemplate(any())).thenReturn(builderMock);

		assertThat(validator.isValid(BillingRecord.create().withType(EXTERNAL).withIssuer(Issuer.create().withPartyId("partyId").withFirstName("firstName").withLastName("lastName")
			.withOrganizationName("organizationName")), contextMock)).isFalse();

		verify(contextMock).disableDefaultConstraintViolation();
		verify(contextMock).buildConstraintViolationWithTemplate("issuer must either have an organization name or a first and last name defined");
		verify(builderMock).addConstraintViolation();
	}

	@Test
	void withExternalTypeAndIssuerNotPresent() {
		when(contextMock.buildConstraintViolationWithTemplate(any())).thenReturn(builderMock);

		assertThat(validator.isValid(BillingRecord.create().withType(EXTERNAL), contextMock)).isFalse();

		verify(contextMock).disableDefaultConstraintViolation();
		verify(contextMock).buildConstraintViolationWithTemplate("issuer can not be null when billing record is of type EXTERNAL");
		verify(builderMock).addConstraintViolation();
	}

	@Test
	void withExternalTypeAndIssuerWithNoPartyId() {
		when(contextMock.buildConstraintViolationWithTemplate(any())).thenReturn(builderMock);

		assertThat(validator.isValid(BillingRecord.create().withType(EXTERNAL).withIssuer(Issuer.create()
				.withOrganizationName("organizationName")), contextMock)).isFalse();

		verify(contextMock).disableDefaultConstraintViolation();
		verify(contextMock).buildConstraintViolationWithTemplate("issuer must have partyId when billing record is of type EXTERNAL");
		verify(builderMock).addConstraintViolation();
	}
}
