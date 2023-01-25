package se.sundsvall.billingpreprocessor.api.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import se.sundsvall.billingpreprocessor.api.validation.impl.ValidAddressDetailsConstraintValidator;

@Documented
@Target({ ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidAddressDetailsConstraintValidator.class)
public @interface ValidAddressDetails {
	String message() default "Street, postal code and city must all be present in issuer.addressDetails for EXTERNAL billing record";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
