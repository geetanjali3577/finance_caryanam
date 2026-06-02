package com.finserv.service;

import com.finserv.dto.KycDocumentDto;
import com.finserv.dto.UserKycDto;

public interface UserKycService {

    // USER
    UserKycDto saveKyc(UserKycDto dto);

    UserKycDto getKycByUserId(Long userId);

    // ADMIN DOCUMENT STATUS UPDATE
    KycDocumentDto updateDocumentStatus(
            Long documentId,
            KycDocumentDto dto
    );

    // ADMIN FINAL KYC STATUS
    UserKycDto updateFinalStatus(
            Long kycId,
            UserKycDto dto
    );

    // ASSIGN BANK
    UserKycDto assignBank(
            Long kycId,
            String bankName
    );

    // DISBURSEMENT
    String disburseLoan(Long kycId);


}