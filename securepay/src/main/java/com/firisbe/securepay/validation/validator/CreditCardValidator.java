package com.firisbe.securepay.validation.validator;

import com.firisbe.securepay.validation.CreditCard;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class CreditCardValidator implements ConstraintValidator<CreditCard, String> {

    private static final String CREDIT_CARD_REGEX = "^(\\d{4}-){3}\\d{4}$";

    @Override
    public void initialize(CreditCard constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return Pattern.matches(CREDIT_CARD_REGEX, value);
    }
}