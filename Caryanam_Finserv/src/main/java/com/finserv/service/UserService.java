package com.finserv.service;

import com.finserv.dto.*;

import java.util.List;

public interface UserService {

    UserResponseDTO registerUser(
            UserRegisterDTO dto
    );


    String generateApplicationId();

    UserResponseDTO searchByEmail(String email);
    UserResponseDTO getUserById(Long id);

    List<UserResponseDTO> getAllUsers();

    UserResponseDTO updateUser(Long id, UserRegisterDTO dto);

    List<UserResponseDTO> searchByName(String name);

   void deleteUser(Long id);

    String sendOtp(String email);

    String verifyOtp(VerifyOtpDTO dto);

    String resetPassword(ResetPasswordDTO dto);

    void assignBankAndSendMail(Long userId, Long bankId);
    //.......................
    void paymentSuccess(Long userId);

    List<UserResponseDTO> searchUsersByBank(String bankName);

    List<PaymentHistoryDTO> getPaymentHistory();

    PaymentHistoryDTO getPaymentDetails(Long userId);

    DealerUsersResponseDTO getUsersByDealerCode(String dealerCode);
}