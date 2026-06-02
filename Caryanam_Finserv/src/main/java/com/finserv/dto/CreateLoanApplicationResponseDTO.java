package com.finserv.dto;

import com.finserv.enums.LoanApplicationStatus;
import com.finserv.enums.LoanDocumentStep;
import com.finserv.enums.RegistrationType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateLoanApplicationResponseDTO {

    private Long loanApplicationId;

    private String applicationNumber;

    private Long userId;

    private Long dealerId;

    private String fullName;

    private String email;

    private String mobileNumber;

    private Double loanAmount;

    private RegistrationType registrationType;

    private LoanDocumentStep currentStep;

    private LoanApplicationStatus status;

    private LocalDateTime createdAt;
}