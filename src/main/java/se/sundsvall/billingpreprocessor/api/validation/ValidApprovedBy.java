package se.sundsvall.billingpreprocessor.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import se.sundsvall.billingpreprocessor.api.validation.impl.ValidApprovedByConstraintValidator;

@Documented
@Target({
	ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE
})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidApprovedByConstraintValidator.class)
public @interface ValidApprovedBy {
	String message() default "can not be blank";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
