package com.finserv.entity;

import com.finserv.enums.RegistrationType;
import com.finserv.enums.Role;
import com.finserv.enums.UserStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "UserReg")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String fullName;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String mobileNumber;

    private String password;

    // DEALER or INDIVIDUAL
    @Enumerated(EnumType.STRING)
    private RegistrationType registrationType;

    // Dealer Code
    private String dealerCode;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    private LocalDateTime createdAt;

    @Column(
            name = "application_id",
            unique = true,
            nullable = false
    )
    private String applicationId;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private PersonalInfo personalInfo;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    private String otp;
    private Boolean isOtpVerified = false;

    private LocalDateTime otpGeneratedTime;
}