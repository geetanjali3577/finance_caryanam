package com.finserv.serviceImpl;

import com.finserv.dto.VerifyOtpDTO;
import com.finserv.emailservice.EmailService;
import com.finserv.entity.EmailVerification;
import com.finserv.repository.EmailVerificationRepository;
import com.finserv.service.EmailVerificationService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailVerificationServiceImpl
        implements EmailVerificationService {

    private final EmailVerificationRepository emailVerificationRepository;

    private final EmailService emailService;

    @Override
    public String sendRegisterOtp(String email) {

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

        emailVerificationRepository.save(ev);

        emailService.sendMail(
                email,
                "Register OTP",
                "Your OTP is : " + otp
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

        if (!ev.getOtp().equals(dto.getOtp())) {

            return "Invalid OTP";
        }

        ev.setVerified(true);

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