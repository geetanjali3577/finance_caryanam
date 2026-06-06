package com.finserv.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Document summary payload used by chatbot responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatbotDocumentSummaryDTO {

    private long pending;
    private long verified;
    private long approved;
    private long rejected;
    private long totalDocuments;
}
