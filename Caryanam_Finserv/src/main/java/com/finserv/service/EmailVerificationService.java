package com.finserv.service;

import com.finserv.dto.VerifyOtpDTO;

public interface EmailVerificationService {

    String sendRegisterOtp(String email);

    String verifyRegisterOtp(VerifyOtpDTO dto);

    boolean isEmailVerified(String email);
}