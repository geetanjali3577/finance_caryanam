package com.finserv.controller;

import com.finserv.dto.ChatbotResponseDTO;
import com.finserv.service.ChatbotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
public class ChatbotController {

    //ALL
    private final ChatbotService chatbotService;

    @GetMapping("/user/application-summary")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ChatbotResponseDTO> getUserApplicationSummary() {
        return ResponseEntity.ok(chatbotService.getUserApplicationSummary());
    }


    @GetMapping("/user/document-status")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ChatbotResponseDTO> getUserDocumentStatus() {
        return ResponseEntity.ok(chatbotService.getUserDocumentStatus());
    }


    @GetMapping("/user/pending-documents")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ChatbotResponseDTO> getUserPendingDocuments() {
        return ResponseEntity.ok(chatbotService.getUserPendingDocuments());
    }


    @GetMapping("/user/loan-amount")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ChatbotResponseDTO> getUserLoanAmount() {
        return ResponseEntity.ok(chatbotService.getUserLoanAmount());
    }


    @GetMapping("/dealer/my-id")
    @PreAuthorize("hasRole('DEALER')")
    public ResponseEntity<ChatbotResponseDTO> getDealerMyId() {
        return ResponseEntity.ok(chatbotService.getDealerMyId());
    }


    @GetMapping("/dealer/users")
    @PreAuthorize("hasRole('DEALER')")
    public ResponseEntity<ChatbotResponseDTO> getDealerUsers() {
        return ResponseEntity.ok(chatbotService.getDealerUsers());
    }

    @GetMapping("/dealer/pending-documents")
    @PreAuthorize("hasRole('DEALER')")
    public ResponseEntity<ChatbotResponseDTO> getDealerPendingDocuments() {
        return ResponseEntity.ok(chatbotService.getDealerPendingDocuments());
    }


    @GetMapping("/dealer/document-summary")
    @PreAuthorize("hasRole('DEALER')")
    public ResponseEntity<ChatbotResponseDTO> getDealerDocumentSummary(
            @RequestParam(value = "month", required = false, defaultValue = "current") String month
    ) {
        return ResponseEntity.ok(chatbotService.getDealerDocumentSummary(month));
    }


    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ChatbotResponseDTO> getAdminUsers() {
        return ResponseEntity.ok(chatbotService.getAdminUsers());
    }


    @GetMapping("/admin/dealer-wise-summary")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ChatbotResponseDTO> getAdminDealerWiseSummary() {
        return ResponseEntity.ok(chatbotService.getAdminDealerWiseSummary());
    }

    @GetMapping("/admin/document-summary")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ChatbotResponseDTO> getAdminDocumentSummary(
            @RequestParam(value = "month", required = false, defaultValue = "current") String month
    ) {
        return ResponseEntity.ok(chatbotService.getAdminDocumentSummary(month));
    }

    @GetMapping("/admin/pending-documents")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ChatbotResponseDTO> getAdminPendingDocuments() {
        return ResponseEntity.ok(chatbotService.getAdminPendingDocuments());
    }
}

