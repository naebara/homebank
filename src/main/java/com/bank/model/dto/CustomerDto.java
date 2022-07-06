package com.bank.model.dto;

import com.bank.validation.phonenumber.PhoneNumberValidation;
import com.bank.validation.ssn.SsnValidation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDto {

    private Integer id;

    @NotNull(message = "Full name can not be null")
    @Size(message = "Full name must be in range (5, 20) characters", min = 5, max = 20)
    private String fullName;

    @NotNull(message = "Address can not be null")
    @Size(message = "Address must be in range (3, 50) characters", min = 3, max = 50)
    private String address;

    @NotNull(message = "Phone number can not be null")
    @PhoneNumberValidation()
    private String phoneNumber;

    @SsnValidation()
    @NotNull(message = "Ssn can not be null")
    private String ssn;
}
