package com.finserv.entity;

import com.finserv.enums.EmploymentType;
import com.finserv.enums.LoanApplicationStatus;
import com.finserv.enums.LoanDocumentStep;
import com.finserv.enums.RegistrationType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "loan_application")
@Data
public class LoanApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long loanApplicationId;

    private String applicationNumber;

    private Long userId;

    private Long dealerId;

    @Enumerated(EnumType.STRING)
    private RegistrationType registrationType;

    @Enumerated(EnumType.STRING)
    private LoanDocumentStep currentStep;

    @Enumerated(EnumType.STRING)
    private LoanApplicationStatus status;

    private String fullName;

    private String email;

    private String mobileNumber;

    @Column(length = 500)
    private String address;

    private String city;

    private String state;

    private String pincode;

    private Double loanAmount;

    @Enumerated(EnumType.STRING)
    private EmploymentType employmentType;

    private Long panDocumentId;

    private Long aadhaarFrontDocumentId;

    private Long aadhaarBackDocumentId;

    private Long residentialProofDocumentId;

    private Long salarySlipDocumentId;

    private Long bankStatementDocumentId;

    private Long appointmentLetterDocumentId;

    private Long itrDocumentId;

    private Long rcDocumentId;

    private Long insuranceDocumentId;

    private Long carFrontDocumentId;

    private Long carBackDocumentId;

    private String odometerReading;

    private String chassisNumber;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    @Column(length = 1000)
    private String remark;

    private String rejectedStep;
}