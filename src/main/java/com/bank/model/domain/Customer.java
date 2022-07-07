package com.bank.model.domain;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("customer")
@Data
@NoArgsConstructor
public class Customer {

    @Column("id")
    @Id
    private Integer id;

    @Column("fullName")
    private String fullName;

    private String address;

    @Column("phone_number")
    private String phoneNumber;

    private String ssn;
}
