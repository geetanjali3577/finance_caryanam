package com.finserv.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DealerRegisterDTO {

    private String fullName;
    private String email;
    private String mobileNumber;
    private String password;
    private String role;
}
