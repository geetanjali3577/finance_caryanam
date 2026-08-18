package com.finserv.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "mobile_verification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MobileVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String mobileNumber;

    private String otp;

    @Builder.Default
    private boolean verified = false;

    private LocalDateTime otpGeneratedTime;

    private LocalDateTime otpExpiryTime;
}
