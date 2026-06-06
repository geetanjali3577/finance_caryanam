package com.finserv.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * User-facing chatbot summary for a single application.
 */
@Data
public class ChatbotUserSummaryDTO {

    private Long userId;
    private String name;
    private String applicationId;
    private Boolean paymentDone;
    private String applicationStatus;
    private Double loanAmount;
    private ChatbotDocumentSummaryDTO documentSummary;
    private Long profileCompletionPercent;
    private LocalDateTime createdAt;
}


