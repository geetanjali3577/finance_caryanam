package com.finserv.service;

import com.finserv.dto.ChatbotResponseDTO;

/**
 * Service contract for rule-based role-aware chatbot responses.
 */
public interface ChatbotService {

    ChatbotResponseDTO getUserApplicationSummary();

    ChatbotResponseDTO getUserDocumentStatus();

    ChatbotResponseDTO getUserPendingDocuments();

    ChatbotResponseDTO getUserLoanAmount();

    ChatbotResponseDTO getDealerMyId();

    ChatbotResponseDTO getDealerUsers();

    ChatbotResponseDTO getDealerPendingDocuments();

    ChatbotResponseDTO getDealerDocumentSummary(String month);

    ChatbotResponseDTO getAdminUsers();

    ChatbotResponseDTO getAdminDealerWiseSummary();

    ChatbotResponseDTO getAdminDocumentSummary(String month);

    ChatbotResponseDTO getAdminPendingDocuments();
}

