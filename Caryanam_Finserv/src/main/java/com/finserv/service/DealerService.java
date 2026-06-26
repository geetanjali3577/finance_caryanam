package com.finserv.service;

import com.finserv.dto.*;
import java.util.*;


public interface DealerService {

    DealerResponseDTO registerDealer(DealerRegisterDTO dto);

    String sendOtp(String email);

    String verifyOtp(VerifyOtpDTO dto);

    String resetPassword(ResetPasswordDTO dto);

    DealerResponseDTO updateDealer(Long id, DealerRegisterDTO dto);

    List<DealerResponseDTO> getAllDealers();

    DealerResponseDTO searchByDealerCode(String dealerCode);

    void deleteDealer(Long dealerId);

    String changePassword(ChangePasswordDTO dto);
}