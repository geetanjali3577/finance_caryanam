package com.finserv.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WhatsAppLeadRequest {

    @NotBlank(message = "Name is required.")
    private String name;

    @NotBlank(message = "Mobile number is required.")
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile Number must contain exactly 10 digits.")
    private String mobileNumber;
}
