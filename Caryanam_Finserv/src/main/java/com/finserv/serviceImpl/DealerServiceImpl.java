package com.finserv.serviceImpl;

import com.finserv.dto.DealerRegisterDTO;
import com.finserv.dto.DealerResponseDTO;
import com.finserv.dto.ResetPasswordDTO;
import com.finserv.dto.VerifyOtpDTO;
import com.finserv.emailservice.EmailService;
import com.finserv.entity.Dealer;
import com.finserv.enums.DealerStatus;
import com.finserv.enums.Role;
import com.finserv.repository.DealerRepository;
import com.finserv.repository.UserRepository;
import com.finserv.service.DealerService;
import com.finserv.dto.ChangePasswordDTO;

import com.finserv.service.EmailVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

@Service
public class DealerServiceImpl implements DealerService {

    @Autowired
    private DealerRepository dealerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailVerificationService emailVerificationService;


    @Override
    public DealerResponseDTO registerDealer(DealerRegisterDTO dto) {

        if(!emailVerificationService
                .isEmailVerified(dto.getEmail())){

            throw new RuntimeException(
                    "Please verify email first");
        }

        if (dealerRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email Already Exists");
        }

        if (dealerRepository.existsByMobileNumber(
                dto.getMobileNumber())) {

            throw new RuntimeException(
                    "Mobile Number Already Exists");
        }

        Dealer dealer = new Dealer();

        dealer.setFullName(dto.getFullName());
        dealer.setEmail(dto.getEmail());
        dealer.setMobileNumber(dto.getMobileNumber());

        dealer.setPassword(
                passwordEncoder.encode(dto.getPassword()));

        dealer.setRole(Role.DEALER);

        dealer.setStatus(DealerStatus.ACTIVE);

        dealer.setCreatedAt(LocalDateTime.now());

        Dealer savedDealer =
                dealerRepository.save(dealer);

        return DealerResponseDTO.builder()
                .dealerId(savedDealer.getDealerId())
                .dealerCode(savedDealer.getDealerCode())
                .fullName(savedDealer.getFullName())
                .email(savedDealer.getEmail())
                .mobileNumber(savedDealer.getMobileNumber())
                .role(savedDealer.getRole().name())
                .createdAt(savedDealer.getCreatedAt())
                .build();
    }

    @Override
    public String sendOtp(String email) {

        Optional<Dealer> optionalDealer =
                dealerRepository.findByEmail(email);

        if (optionalDealer.isEmpty()) {
            return "Dealer not found";
        }

        Dealer dealer = optionalDealer.get();

        // =====================================
        // CHECK 2 MINUTE LIMIT
        // =====================================

        if (dealer.getOtpGeneratedTime() != null) {

            LocalDateTime otpExpireTime =
                    dealer.getOtpGeneratedTime()
                            .plusMinutes(2);

            if (LocalDateTime.now()
                    .isBefore(otpExpireTime)) {

                long secondsLeft =
                        Duration.between(
                                LocalDateTime.now(),
                                otpExpireTime
                        ).getSeconds();

                return "Please wait "
                        + secondsLeft +
                        " seconds before requesting new OTP";
            }
        }

        // =====================================
        // GENERATE OTP
        // =====================================

        int otp =
                (int) (Math.random() * 900000) + 100000;

        dealer.setOtp(String.valueOf(otp));

        dealer.setOtpGeneratedTime(
                LocalDateTime.now()
        );

        dealerRepository.save(dealer);

        // =====================================
        // SEND MAIL
        // =====================================

        String subject = "Forgot Password OTP";

        String body =
                "Dear " + dealer.getFullName() + ",\n\n" +

                        "We received a request to reset your password.\n\n" +

                        "Your OTP is : " + otp + "\n\n" +

                        "This OTP is valid for 5 minutes.\n\n" +

                        "Regards,\n" +
                        "Caryanam Finserv Team";

        emailService.sendMail(
                dealer.getEmail(),
                subject,
                body
        );

        return "OTP sent successfully";
    }

//    @Override
//    public String verifyOtp(VerifyOtpDTO dto) {
//
//        Optional<Dealer> optionalDealer =
//                dealerRepository.findByEmail(dto.getEmail());
//
//        if (optionalDealer.isEmpty()) {
//            return "Dealer not found";
//        }
//
//        Dealer dealer = optionalDealer.get();
//
//        if (dealer.getOtp() == null) {
//            return "OTP not found";
//        }
//
//        if (!dealer.getOtp().equals(dto.getOtp())) {
//            return "Invalid OTP";
//        }
//
//        if (dealer.getOtpGeneratedTime()
//                .plusMinutes(5)
//                .isBefore(LocalDateTime.now())) {
//
//            return "OTP expired";
//        }
//
//        dealer.setIsOtpVerified(true);
//
//        dealerRepository.save(dealer);
//
//        return "OTP verified successfully";
//    }
@Override
public String verifyOtp(VerifyOtpDTO dto) {

    Optional<Dealer> optionalDealer =
            dealerRepository.findByEmail(dto.getEmail());

    if (optionalDealer.isEmpty()) {
        return "Dealer not found";
    }

    Dealer dealer = optionalDealer.get();

    if (dealer.getOtp() == null) {
        return "OTP not found";
    }

    if (!dealer.getOtp().equals(dto.getOtp())) {
        return "Invalid OTP";
    }

    if (dealer.getOtpGeneratedTime()
            .plusMinutes(5)
            .isBefore(LocalDateTime.now())) {

        return "OTP expired";
    }

    dealer.setIsOtpVerified(true);

    dealerRepository.save(dealer);

    // Send Verification Success Mail
    String subject = "OTP Verification Successful";

    String body =
            "Dear " + dealer.getFullName() + ",\n\n" +

                    "Your OTP has been verified successfully.\n\n" +

                    "Your account verification process is now complete.\n\n" +

                    "You can continue using our services without any interruption.\n\n" +

                    "If you did not perform this verification, please contact our support team immediately.\n\n" +

                    "Regards,\n" +
                    "Vahanfinserv Team\n" +
                    "support@Vahanfinserv.com";

    emailService.sendMail(
            dealer.getEmail(),
            subject,
            body
    );

    return "OTP verified successfully";
}

    @Override
    public String resetPassword(ResetPasswordDTO dto) {

        Optional<Dealer> optionalDealer =
                dealerRepository.findByEmail(dto.getEmail());

        if (optionalDealer.isEmpty()) {
            return "Dealer not found";
        }

        Dealer dealer = optionalDealer.get();

        if (dealer.getIsOtpVerified() == null
                || !dealer.getIsOtpVerified()) {

            return "Please verify OTP first";
        }
        // New Password Required
        if (dto.getNewPassword() == null
                || dto.getNewPassword().trim().isEmpty()) {

            return "New Password is Required";
        }

        // Password Length Validation
        if (dto.getNewPassword().length() < 4) {
            return "Password must be at least 8 characters";
        }

        // Same Password Validation
        if (passwordEncoder.matches(
                dto.getNewPassword(),
                dealer.getPassword())) {

            return "New Password cannot be same as Old Password";
        }



        // update password
        dealer.setPassword(
                passwordEncoder.encode(
                        dto.getNewPassword()
                )
        );

        // clear otp data
        dealer.setOtp(null);
        dealer.setOtpGeneratedTime(null);
        dealer.setIsOtpVerified(false);

        dealerRepository.save(dealer);

        // =====================================
        // SEND MAIL
        // =====================================

        String subject =
                "Password Reset Successfully";

        String body =
                "Dear " + dealer.getFullName() + ",\n\n" +

                        "Your password has been reset successfully.\n\n" +

                        "If you did not perform this action, " +
                        "please contact support immediately.\n\n" +

                        "Regards,\n" +
                        "Vahanfinserv Team\n" +
                        "support@Vahanfinserv.com";

        emailService.sendMail(
                dealer.getEmail(),
                subject,
                body
        );

        return "Password reset successfully";
    }

    @Override
    public DealerResponseDTO updateDealer(Long id, DealerRegisterDTO dto) {
        Dealer dealer = dealerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dealer Not Found"));

        if (dto.getFullName() != null && !dto.getFullName().isEmpty()) {
            dealer.setFullName(dto.getFullName());
        }

        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
            String newEmail = dto.getEmail().toLowerCase().trim();
            if (!dealer.getEmail().equalsIgnoreCase(newEmail) && dealerRepository.existsByEmail(newEmail)) {
                throw new RuntimeException("Email Already Exists");
            }
            dealer.setEmail(newEmail);
        }

        if (dto.getMobileNumber() != null && !dto.getMobileNumber().isEmpty()) {
            if (!dealer.getMobileNumber().equals(dto.getMobileNumber()) && dealerRepository.existsByMobileNumber(dto.getMobileNumber())) {
                throw new RuntimeException("Mobile Number Already Exists");
            }
            dealer.setMobileNumber(dto.getMobileNumber());
        }

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            dealer.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        Dealer savedDealer = dealerRepository.save(dealer);
        return DealerResponseDTO.builder()
                .dealerId(savedDealer.getDealerId())
                .dealerCode(savedDealer.getDealerCode())
                .fullName(savedDealer.getFullName())
                .email(savedDealer.getEmail())
                .mobileNumber(savedDealer.getMobileNumber())
                .role(savedDealer.getRole().name())
                .createdAt(savedDealer.getCreatedAt())
                .build();
    }

    @Override
    public List<DealerResponseDTO> getAllDealers() {
        List<Dealer> dealers = dealerRepository.findAll();
        return dealers.stream().map(d -> DealerResponseDTO.builder()
                .dealerId(d.getDealerId())
                .dealerCode(d.getDealerCode())
                .fullName(d.getFullName())
                .email(d.getEmail())
                .mobileNumber(d.getMobileNumber())
                .role(d.getRole().name())
                .createdAt(d.getCreatedAt())
                .build()).toList();
    }

    @Override
    public DealerResponseDTO searchByDealerCode(String dealerCode) {

        Dealer dealer = dealerRepository
                .findByDealerCode(dealerCode)
                .orElseThrow(() ->
                        new RuntimeException("Dealer not found"));

        return mapToDealerResponseDTO(dealer);
    }

    private DealerResponseDTO mapToDealerResponseDTO(Dealer dealer) {

        return DealerResponseDTO.builder()
                .dealerId(dealer.getDealerId())
                .dealerCode(dealer.getDealerCode())
                .fullName(dealer.getFullName())
                .email(dealer.getEmail())
                .mobileNumber(dealer.getMobileNumber())
                .createdAt(dealer.getCreatedAt())
                .build();
    }

    @Override
    public void deleteDealer(Long dealerId) {

        Dealer dealer = dealerRepository.findById(dealerId)
                .orElseThrow(() ->
                        new RuntimeException("Dealer not found"));

        dealerRepository.delete(dealer);
    }

    @Override
    public String changePassword(ChangePasswordDTO dto) {

        Optional<Dealer> optionalDealer =
                dealerRepository.findByEmail(dto.getEmail());

        if (optionalDealer.isEmpty()) {
            return "Dealer not found";
        }

        Dealer dealer = optionalDealer.get();

        // Old password check
        if (!passwordEncoder.matches(
                dto.getOldPassword(),
                dealer.getPassword())) {

            return "Old Password is Incorrect";
        }

        // Confirm password check
        if (!dto.getNewPassword()
                .equals(dto.getConfirmPassword())) {

            return "New Password and Confirm Password do not match";
        }

        // Same password check
        if (passwordEncoder.matches(
                dto.getNewPassword(),
                dealer.getPassword())) {

            return "New Password cannot be same as Old Password";
        }

        // Update password
        dealer.setPassword(
                passwordEncoder.encode(dto.getNewPassword()));

        dealerRepository.save(dealer);

        return "Password Changed Successfully";
    }

}