package com.finserv.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Standard chatbot response wrapper for role-based answers.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatbotResponseDTO {

    private boolean success;
    private String role;
    private String intent;
    private String message;
    private Object data;
    private List<String> suggestions;
}
