package com.finserv.serviceImpl;

import com.finserv.dto.VerifyOtpDTO;
import com.finserv.emailservice.EmailService;
import com.finserv.entity.EmailVerification;
import com.finserv.repository.DealerRepository;
import com.finserv.repository.EmailVerificationRepository;
import com.finserv.repository.UserRepository;
import com.finserv.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailVerificationServiceImpl
        implements EmailVerificationService {

    private final EmailVerificationRepository emailVerificationRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final DealerRepository dealerRepository;

    @Override
    public String sendRegisterOtp(String email) {

        if (userRepository.existsByEmail(email) ||
        dealerRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already registered");
        }

        String otp =
                String.valueOf(
                        100000 + new Random().nextInt(900000));

        EmailVerification ev =
                emailVerificationRepository
                        .findByEmail(email)
                        .orElse(new EmailVerification());

        ev.setEmail(email);
        ev.setOtp(otp);
        ev.setVerified(false);

        ev.setOtpExpiryTime(
                LocalDateTime.now().plusMinutes(5));

        emailVerificationRepository.save(ev);

        emailService.sendMail(
                email,
                "Register OTP",
                "Your OTP is : " + otp +
                        "\n\nThis OTP is valid for 5 minutes."
        );

        return "OTP Sent Successfully";
    }
    @Override
    public String verifyRegisterOtp(
            VerifyOtpDTO dto) {

        EmailVerification ev =
                emailVerificationRepository
                        .findByEmail(dto.getEmail())
                        .orElseThrow(() ->
                                new RuntimeException("Email not found"));

        // Expiry check
        if (ev.getOtpExpiryTime() != null &&
                ev.getOtpExpiryTime().isBefore(LocalDateTime.now())) {

            return "OTP Expired";
        }

        // OTP check
        if (!ev.getOtp().equals(dto.getOtp())) {
            return "Invalid OTP";
        }

        ev.setVerified(true);

        // OTP reuse hou naye mhanun clear kara
        ev.setOtp(null);
        ev.setOtpExpiryTime(null);

        emailVerificationRepository.save(ev);

        return "OTP Verified Successfully";
    }

    @Override
    public boolean isEmailVerified(String email) {

        return emailVerificationRepository
                .findByEmail(email)
                .map(EmailVerification::isVerified)
                .orElse(false);
    }
}