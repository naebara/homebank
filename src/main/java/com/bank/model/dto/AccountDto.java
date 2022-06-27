package com.bank.model.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class AccountDto {

    private Integer id;
    private String number;
    private Currency currency;
    private BigDecimal amount;
    private Integer customerId;
    private LocalDate issuedAt;
}
