package com.finserv.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PersonalInfoResponseDTO {

    private Long personalInfoId;

    private Long userId;

    private String fullName;

    private String email;

    private String mobileNumber;

    private String address;

    private String city;

    private String state;

    private String pincode;

    private Double loanAmount;

    private LocalDateTime createdAt;
}