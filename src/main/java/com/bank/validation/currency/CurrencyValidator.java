package com.bank.validation.currency;

import com.bank.model.types.Currency;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Slf4j
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
            log.error("Error : {}", e.getMessage());
            return false;
        }
    }
}
