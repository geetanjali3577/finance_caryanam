package com.finserv.serviceImpl;

import com.finserv.dto.VerifyMobileOtpDTO;
import com.finserv.entity.MobileVerification;
import com.finserv.repository.DealerRepository;
import com.finserv.repository.MobileVerificationRepository;
import com.finserv.repository.UserRepository;
import com.finserv.service.MobileVerificationService;
import com.finserv.whatapp.WhatsAppService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MobileVerificationServiceImpl implements MobileVerificationService {

    private final MobileVerificationRepository mobileVerificationRepository;
    private final WhatsAppService whatsAppService;
    private final UserRepository userRepository;
    private final DealerRepository dealerRepository;

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public String sendUserRegisterMobileOtp(String mobileNumber) {
        validateMobileNumber(mobileNumber);

        if (userRepository.existsByMobileNumber(mobileNumber)) {
            throw new RuntimeException("Mobile Number Already Exists");
        }

        return generateAndSendOtp(mobileNumber);
    }

    @Override
    public String sendDealerRegisterMobileOtp(String mobileNumber) {
        validateMobileNumber(mobileNumber);

        if (dealerRepository.existsByMobileNumber(mobileNumber)) {
            throw new RuntimeException("Mobile Number Already Exists");
        }

        return generateAndSendOtp(mobileNumber);
    }

    private String generateAndSendOtp(String mobileNumber) {
        // Generate secure 6-digit OTP
        int otpNumber = 100000 + secureRandom.nextInt(900000);
        String otp = String.valueOf(otpNumber);

        MobileVerification mv = mobileVerificationRepository
                .findByMobileNumber(mobileNumber)
                .orElse(new MobileVerification());

        mv.setMobileNumber(mobileNumber);
        mv.setOtp(otp);
        mv.setVerified(false);
        mv.setOtpGeneratedTime(LocalDateTime.now());
        mv.setOtpExpiryTime(LocalDateTime.now().plusMinutes(10));

        mobileVerificationRepository.save(mv);

        // Send OTP through WhatsApp Cloud API
        whatsAppService.sendMobileVerificationOtp(mobileNumber, otp);

        return "OTP Sent Successfully to WhatsApp";
    }

    @Override
    public String verifyRegisterMobileOtp(VerifyMobileOtpDTO dto) {
        if (dto == null || dto.getMobileNumber() == null || dto.getOtp() == null) {
            throw new RuntimeException("Mobile number and OTP are required");
        }

        String mobile = dto.getMobileNumber().trim();
        String otp = dto.getOtp().trim();

        MobileVerification mv = mobileVerificationRepository
                .findByMobileNumber(mobile)
                .orElseThrow(() -> new RuntimeException("Mobile number not found"));

        // Expiry check (10 minutes)
        if (mv.getOtpExpiryTime() != null && mv.getOtpExpiryTime().isBefore(LocalDateTime.now())) {
            return "OTP Expired";
        }

        // OTP match check
        if (mv.getOtp() == null || !mv.getOtp().equals(otp)) {
            return "Invalid OTP";
        }

        // Mark verified and clear OTP to prevent reuse
        mv.setVerified(true);
        mv.setOtp(null);
        mv.setOtpExpiryTime(null);

        mobileVerificationRepository.save(mv);

        return "OTP Verified Successfully";
    }

    @Override
    public boolean isMobileVerified(String mobileNumber) {
        if (mobileNumber == null || mobileNumber.trim().isEmpty()) {
            return false;
        }

        return mobileVerificationRepository
                .findByMobileNumber(mobileNumber.trim())
                .map(MobileVerification::isVerified)
                .orElse(false);
    }

    private void validateMobileNumber(String mobileNumber) {
        if (mobileNumber == null || mobileNumber.trim().isEmpty()) {
            throw new RuntimeException("Mobile Number is Required");
        }

        String cleaned = mobileNumber.trim();
        if (!cleaned.matches("^[6-9]\\d{9}$")) {
            throw new RuntimeException("Invalid Mobile Number. Must be a valid 10-digit Indian number starting with 6, 7, 8, or 9");
        }
    }
}
