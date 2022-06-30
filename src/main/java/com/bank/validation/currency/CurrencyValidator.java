package com.bank.validation.currency;

import com.bank.model.enumTypes.Currency;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CurrencyValidator implements ConstraintValidator<CurrencyValidation, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        try {
            Currency.valueOf(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
