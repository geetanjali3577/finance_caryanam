package com.finserv.dto;

import lombok.Data;

@Data
public class PersonalInfoRequestDTO {

    private Long userId;

    private String address;

    private String mobileNumber;

    private String city;

    private String state;

    private String pincode;

    private Double loanAmount;
}