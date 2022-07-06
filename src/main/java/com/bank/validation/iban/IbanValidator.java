package com.bank.validation.iban;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IbanValidator implements ConstraintValidator<IbanValidation, String> {
    @Override
    public boolean isValid(String iban, ConstraintValidatorContext context) {
        if (iban == null) {
            return false;
        }
        int ibanMinSize = 15;
        int ibanMaxSize = 34;
        long ibanMax = 999999999;
        long ibanModulus = 97;

        String trimmed = iban.replaceAll(" ", "");
        trimmed = trimmed.trim();

        if (trimmed.length() < ibanMinSize || trimmed.length() > ibanMaxSize) {
            return false;
        }

        String reformat = trimmed.substring(4) + trimmed.substring(0, 4);
        long total = 0;

        for (int i = 0; i < reformat.length(); i++) {

            int charValue = Character.getNumericValue(reformat.charAt(i));

            if (charValue < 0 || charValue > 35) {
                return false;
            }

            total = (charValue > 9 ? total * 100 : total * 10) + charValue;

            if (total > ibanMax) {
                total = (total % ibanModulus);
            }
        }

        return (total % ibanModulus) == 1;
    }
}
