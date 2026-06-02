package com.finserv.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DocumentResponseDTO {

    private Long documentId;
    private String documentType;
    private String fileName;
    private String fileUrl;   // <-- add
    private String status;
    private Long userId;
    private String remarks;
    private LocalDateTime uploadedAt;
}