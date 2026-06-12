package com.finserv.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentHistoryDTO  {


    private Long userId;
    private String applicationId;
    private String fullName;
    private String email;
    private String mobileNumber;
    private String paymentStatus;
    private Double paymentAmount;
    private LocalDateTime paymentDate;
}
