package com.finserv.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.jspecify.annotations.Nullable;

@Data
public class LoginRequestDTO {

    private String email;
    private String password;



}
