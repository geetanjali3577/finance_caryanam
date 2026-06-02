package com.finserv.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DocumentResponseDTO {

    private Long documentId;

    private Long userId;

    private String documentType;

    private String fileName;

    private String contentType;

    private Long fileSize;

    private String status;

    private LocalDateTime uploadedAt;

    private String remarks;
}