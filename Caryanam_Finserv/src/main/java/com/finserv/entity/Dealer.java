package com.finserv.entity;

import com.finserv.enums.DealerStatus;
import com.finserv.enums.Role;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Dealer_Reg")
@Data
public class Dealer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dealerId;

    @Column(unique = true, nullable = false)
    private String dealerCode;

    private String fullName;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String mobileNumber;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private DealerStatus status;

    // ONLY ONE PREPERSIST METHOD
    @PrePersist
    public void prePersist() {

        // Default Status
        if (this.status == null) {
            this.status = DealerStatus.ACTIVE;
        }

        // Created Time
        this.createdAt = LocalDateTime.now();

        // Dealer Code Generate
        this.dealerCode = "DLR-" +
                UUID.randomUUID()
                        .toString()
                        .substring(0, 6)
                        .toUpperCase();
    }

    private String otp;

    private LocalDateTime otpGeneratedTime;

    private Boolean isOtpVerified = false;
}