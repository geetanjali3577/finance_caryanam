package com.finserv.dto;

import com.finserv.enums.RegistrationType;
import com.finserv.enums.UserStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DealerUserResponseDTO {

    private Long userId;

    private String fullName;

    private String email;

    private String mobileNumber;

    private RegistrationType registrationType;

    private UserStatus status;

    private LocalDateTime createdAt;
}