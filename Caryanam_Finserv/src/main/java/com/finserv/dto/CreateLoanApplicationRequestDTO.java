package com.finserv.dto;

import lombok.Data;

@Data
public class CreateLoanApplicationRequestDTO {

    private Long userId;

    private Double loanAmount;

    private String address;

    private String city;

    private String state;

    private String pincode;
}