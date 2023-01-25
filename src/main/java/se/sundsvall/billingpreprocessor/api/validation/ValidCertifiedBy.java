package se.sundsvall.billingpreprocessor.api.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import se.sundsvall.billingpreprocessor.api.validation.impl.ValidCertifiedByConstraintValidator;

@Documented
@Target({ ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidCertifiedByConstraintValidator.class)
public @interface ValidCertifiedBy {
	String message() default "can not be blank";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
