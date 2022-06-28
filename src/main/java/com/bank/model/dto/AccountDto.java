package com.bank.model.dto;

import com.bank.validation.currency.CurrencyValidation;
import com.bank.validation.iban.IbanValidation;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class AccountDto {

    private Integer id;

    @IbanValidation()
    @NotNull(message = "Iban can not be null")
    private String iban;


    @CurrencyValidation()
    private String currency;

    @DecimalMin(value = "0", message = "Balance can not be negative")
    private BigDecimal amount;

    private Integer customerId;
    private LocalDate issuedAt;
}
