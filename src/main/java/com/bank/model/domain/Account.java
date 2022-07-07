package com.bank.model.domain;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Table("accounts")
@Data
@NoArgsConstructor
public class Account {

    @Column("id")
    @Id
    private Integer id;

    @Column("iban")
    private String iban;

    private String currency;

    private BigDecimal amount;

    @Column("customer_id")
    private Integer customerId;

    @Column("issued_at")
    private LocalDate issuedAt;
}
