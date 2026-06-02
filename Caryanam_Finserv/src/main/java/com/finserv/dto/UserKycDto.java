package com.finserv.dto;

import com.finserv.enums.EmploymentType;
import com.finserv.enums.KycStatus;
import lombok.Data;

import java.util.List;

@Data
public class UserKycDto {

    private Long kycId;

    private Long userId;

    private EmploymentType employmentType;

    private String assignedBank;

    private Boolean kycCompleted;

    private Boolean disbursed;

    private KycStatus finalStatus;

    // DOCUMENTS
    private List<KycDocumentDto> documents;
}