package com.finserv.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WhatsAppLeadDTO {
    private Long id;
    private String name;
    private String mobileNumber;
    private LocalDateTime createdAt;
}
