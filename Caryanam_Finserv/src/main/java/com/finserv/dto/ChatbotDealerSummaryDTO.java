package com.finserv.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Dealer-facing chatbot summary for users and document activity.
 */
@Data
public class ChatbotDealerSummaryDTO {

    private Long dealerId;
    private String dealerCode;
    private String dealerName;
    private String email;
    private long usersCount;
    private long paymentPendingCount;
    private long paymentApprovedCount;
    private long documentCount;
    private ChatbotDocumentSummaryDTO documentSummary;
    private LocalDateTime generatedAt;
}
