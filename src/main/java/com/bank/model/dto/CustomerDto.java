package com.bank.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerDto {

    private Integer id;
    private String fullName;
    private String address;
    private String phoneNumber;
    private String ssn;
}
