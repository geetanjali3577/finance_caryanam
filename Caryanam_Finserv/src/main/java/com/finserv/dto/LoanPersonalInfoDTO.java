package com.finserv.dto;

import lombok.Data;

@Data
public class LoanPersonalInfoDTO {

    private String fullName;

    private String email;

    private String mobileNumber;

    private String address;

    private String city;

    private String state;

    private String pincode;

    private Double loanAmount;
}