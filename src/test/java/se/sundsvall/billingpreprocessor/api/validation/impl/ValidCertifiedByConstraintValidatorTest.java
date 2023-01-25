package se.sundsvall.billingpreprocessor.api.validation.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static se.sundsvall.billingpreprocessor.api.model.enums.Status.CERTIFIED;

import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import se.sundsvall.billingpreprocessor.api.model.BillingRecord;
import se.sundsvall.billingpreprocessor.api.model.enums.Status;

@ExtendWith(MockitoExtension.class)
class ValidCertifiedByConstraintValidatorTest {

	@Mock
	private ConstraintValidatorContext contextMock;

	@Mock
	private ConstraintViolationBuilder builderMock;

	private ValidCertifiedByConstraintValidator validator = new ValidCertifiedByConstraintValidator();

	@ParameterizedTest
	@EnumSource(mode = EXCLUDE, names = "CERTIFIED", value = Status.class)
	void withStatusNotEqualToCertifiedAndNoCertifiedByDefined(Status status) {
		assertThat(validator.isValid(BillingRecord.create().withStatus(status), contextMock)).isTrue();

		verifyNoInteractions(contextMock, builderMock);
	}

	@Test
	void withStatusEqualToCertifiedAndCertifiedByDefined() {
		assertThat(validator.isValid(BillingRecord.create().withStatus(CERTIFIED).withCertifiedBy("certifiedBy"), contextMock)).isTrue();

		verifyNoInteractions(contextMock, builderMock);
	}

	@Test
	void withStatusEqualToCertifiedAndNoCertifiedByDefined() {
		when(contextMock.buildConstraintViolationWithTemplate(any())).thenReturn(builderMock);

		assertThat(validator.isValid(BillingRecord.create().withStatus(CERTIFIED), contextMock)).isFalse();

		verify(contextMock).disableDefaultConstraintViolation();
		verify(contextMock).buildConstraintViolationWithTemplate("certifiedBy must be present when status is CERTIFIED");
		verify(builderMock).addConstraintViolation();
	}
}
