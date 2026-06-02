package com.finserv.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponseDTO {

    private Long userId;

    private String applicationId;

    private String fullName;

    private String email;

    private String mobileNumber;

    // DEALER or INDIVIDUAL
    private String registrationType;

    // Dealer Code
    private String dealerCode;

    private String role;

    private LocalDateTime createdAt;

}