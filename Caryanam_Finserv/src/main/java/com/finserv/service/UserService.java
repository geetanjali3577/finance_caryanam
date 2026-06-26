package com.finserv.service;

import com.finserv.dto.*;
import org.springframework.beans.factory.annotation.Value;

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
    //.......................payment
    RazorpayOrderResponse createOrder(Long userId);
    void paymentSuccess(Long userId,
                        String orderId,
                        String paymentId);

    List<UserResponseDTO> searchUsersByBank(String bankName);

    List<PaymentHistoryDTO> getPaymentHistory();

    PaymentHistoryDTO getPaymentDetails(Long userId);

    DealerUsersResponseDTO getUsersByDealerCode(String dealerCode);

    void deleteDealerUser(String dealerCode, Long userId);

    String changePassword(ChangePasswordDTO dto);
}