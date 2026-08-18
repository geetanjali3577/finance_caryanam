package com.finserv.service;

import com.finserv.dto.VerifyMobileOtpDTO;

public interface MobileVerificationService {

    String sendUserRegisterMobileOtp(String mobileNumber);

    String sendDealerRegisterMobileOtp(String mobileNumber);

    String verifyRegisterMobileOtp(VerifyMobileOtpDTO dto);

    boolean isMobileVerified(String mobileNumber);
}
