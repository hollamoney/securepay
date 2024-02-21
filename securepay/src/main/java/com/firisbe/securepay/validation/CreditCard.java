package com.firisbe.securepay.validation;

import com.firisbe.securepay.validation.validator.CreditCardValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CreditCardValidator.class)
public @interface CreditCard {

    String message() default "Invalid credit card number; it should be in the format '1234-5678-9012-3456'";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
