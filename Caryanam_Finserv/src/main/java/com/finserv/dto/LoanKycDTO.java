package com.finserv.dto;

import lombok.Data;

@Data
public class LoanKycDTO {

    private Long panDocumentId;

    private Long aadhaarFrontDocumentId;

    private Long aadhaarBackDocumentId;
}