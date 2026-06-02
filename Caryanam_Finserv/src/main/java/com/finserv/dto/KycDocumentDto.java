package com.finserv.dto;

import com.finserv.enums.DocumentType;
import com.finserv.enums.KycStatus;
import lombok.Data;

@Data
public class KycDocumentDto {

    private Long documentId;

    private DocumentType documentType;

    private String documentUrl;

    // ADMIN
    private String adminRemark;

    private KycStatus status;
}