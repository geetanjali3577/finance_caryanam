package com.finserv.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DealerResponseDTO {

    private Long dealerId;

    private String dealerCode;

    private String fullName;

    private String email;

    private String mobileNumber;

    private String role;

    private LocalDateTime createdAt;


}
