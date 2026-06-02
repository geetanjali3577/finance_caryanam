package com.finserv.dto;

import lombok.Data;

@Data
public class DealerLoanApplicationRequestDTO {

    private String fullName;
    private String email;
    private String mobileNumber;

    private Double loanAmount;

    private String address;
    private String city;
    private String state;
    private String pincode;
}