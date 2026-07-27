package com.finserv.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WhatsAppLeadResponse {
    private boolean success;
    private String message;
}
