package se.sundsvall.billingpreprocessor.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import se.sundsvall.billingpreprocessor.api.validation.impl.ValidIssuerConstraintValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target({ ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidIssuerConstraintValidator.class)
public @interface ValidIssuer {
	String message() default "issuer can not be null";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
