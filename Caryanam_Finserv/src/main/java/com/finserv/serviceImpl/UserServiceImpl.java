package com.finserv.serviceImpl;

import com.finserv.dto.*;
import com.finserv.emailservice.EmailService;
import com.finserv.entity.Bank;
import com.finserv.entity.Document;
import com.finserv.entity.PersonalInfo;
import com.finserv.entity.User;
import com.finserv.enums.RegistrationType;
import com.finserv.enums.Role;
import com.finserv.enums.UserStatus;
import com.finserv.repository.*;
import com.finserv.service.EmailVerificationService;
import com.finserv.service.UserService;

import com.finserv.whatapp.WhatsAppService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private DealerRepository dealerRepository;
    @Autowired
    private WhatsAppService whatsAppService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailVerificationService emailVerificationService;

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    @Autowired
    private BankRepository bankRepository;

    @Autowired
    private PersonalInfoRepository personalInfoRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Override
    public String generateApplicationId() {

        Optional<User> lastUser =
                userRepository.findTopByOrderByUserIdDesc();

        int nextNumber = 1;

        if (lastUser.isPresent()
                && lastUser.get().getApplicationId() != null) {

            String lastApplicationId =
                    lastUser.get().getApplicationId();

            String numberPart =
                    lastApplicationId.substring(7);

            nextNumber =
                    Integer.parseInt(numberPart) + 1;
        }

        return String.format(
                "CRY2026%04d",
                nextNumber
        );
    }




    @Override
    public UserResponseDTO registerUser(UserRegisterDTO dto) {

        // Individual registration -> Email verification required
        if (dto.getRegistrationType() == RegistrationType.INDIVIDUAL) {

            if (!emailVerificationService
                    .isEmailVerified(dto.getEmail())) {

                throw new RuntimeException(
                        "Please verify email first");
            }
        }

// Dealer registration -> Dealer code validation only
        if (dto.getRegistrationType() == RegistrationType.DEALER) {

            if (dto.getDealerCode() == null
                    || dto.getDealerCode()
                    .trim()
                    .isEmpty()) {

                throw new RuntimeException(
                        "Dealer Code is Required"
                );
            }

            if (!dealerRepository.existsByDealerCode(
                    dto.getDealerCode())) {

                throw new RuntimeException(
                        "Invalid Dealer Code"
                );
            }
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email Already Exists");
        }

        if (userRepository.existsByMobileNumber(
                dto.getMobileNumber())) {

            throw new RuntimeException(
                    "Mobile Number Already Exists"
            );
        }

        if (dto.getRegistrationType()
                == RegistrationType.DEALER) {

            if (dto.getDealerCode() == null
                    || dto.getDealerCode()
                    .trim()
                    .isEmpty()) {

                throw new RuntimeException(
                        "Dealer Code is Required"
                );
            }

            if (!dealerRepository.existsByDealerCode(
                    dto.getDealerCode())) {

                throw new RuntimeException(
                        "Invalid Dealer Code"
                );
            }
        }

        User user = new User();

        user.setFullName(dto.getFullName());

        user.setEmail(
                dto.getEmail()
                        .toLowerCase()
                        .trim()
        );

        user.setMobileNumber(dto.getMobileNumber());

        user.setPassword(
                passwordEncoder.encode(
                        dto.getPassword()
                )
        );

        user.setRegistrationType(
                dto.getRegistrationType()
        );

        // AUTO APPLICATION ID
        user.setApplicationId(
                generateApplicationId()
        );

        // SAVE DEALER CODE
        if (dto.getRegistrationType()
                == RegistrationType.DEALER) {

            user.setDealerCode(
                    dto.getDealerCode()
            );
        }

        user.setRole(Role.USER);

        user.setStatus(UserStatus.ACTIVE);

        user.setPaymentDone(false);


        user.setCreatedAt(LocalDateTime.now());


        User savedUser = userRepository.save(user);

        UserResponseDTO response = new UserResponseDTO();

        response.setUserId(savedUser.getUserId());

        response.setApplicationId(savedUser.getApplicationId());

        response.setFullName(savedUser.getFullName());

        response.setEmail(
                savedUser.getEmail()
        );

        response.setMobileNumber(
                savedUser.getMobileNumber()
        );

        response.setRegistrationType(
                savedUser.getRegistrationType().name()
        );

        response.setDealerCode(
                savedUser.getDealerCode()
        );

        response.setRole(
                savedUser.getRole().name()
        );

        response.setCreatedAt(
                savedUser.getCreatedAt()
        );

        return response;
    }

    @Override
    public UserResponseDTO getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        UserResponseDTO dto = new UserResponseDTO();


        dto.setUserId(user.getUserId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setApplicationId(user.getApplicationId());
        dto.setDealerCode(user.getDealerCode());
        dto.setRegistrationType(
                user.getRegistrationType() != null ? user.getRegistrationType().name() : null
        );
        dto.setRole(user.getRole() != null ? user.getRole().name() : null);
        dto.setCreatedAt(user.getCreatedAt());


        if (user.getPersonalInfo() != null && user.getPersonalInfo().getMobileNumber() != null) {
            dto.setMobileNumber(user.getPersonalInfo().getMobileNumber());
        } else {
            dto.setMobileNumber(user.getMobileNumber());
        }

        return dto;
    }
    @Override
    public List<UserResponseDTO> getAllUsers() {
        List<User> users = userRepository.findAllPaidUsers();

        return users.stream().map(user -> {

            UserResponseDTO dto = new UserResponseDTO();

            // USER DATA
            dto.setUserId(user.getUserId());
            dto.setFullName(user.getFullName());
            dto.setEmail(user.getEmail());
            dto.setRegistrationType(
                    user.getRegistrationType() != null ? user.getRegistrationType().name() : null
            );
            dto.setRole(user.getRole() != null ? user.getRole().name() : null);
            dto.setApplicationId(user.getApplicationId());
            dto.setDealerCode(user.getDealerCode());
            dto.setCreatedAt(user.getCreatedAt());

            //  PERSONAL INFO (JOIN DATA)
            if (user.getPersonalInfo() != null && user.getPersonalInfo().getMobileNumber() != null) {
                dto.setMobileNumber(user.getPersonalInfo().getMobileNumber());
            } else {
                dto.setMobileNumber(user.getMobileNumber());
            }

            return dto;

        }).toList();
    }
    @Override
    public UserResponseDTO updateUser(Long id, UserRegisterDTO dto) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        // ======================
        // USER UPDATE
        // ======================
        if (dto.getFullName() != null && !dto.getFullName().isEmpty()) {
            user.setFullName(dto.getFullName());
        }

        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
            user.setEmail(dto.getEmail());
        }

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        if (dto.getDealerCode() != null && !dto.getDealerCode().isEmpty()) {
            user.setDealerCode(dto.getDealerCode());
        }

        if (dto.getRegistrationType() != null) {
            user.setRegistrationType(dto.getRegistrationType());
        }

        if (dto.getRole() != null) {
            user.setRole(Role.valueOf(dto.getRole()));
        }

        if (dto.getMobileNumber() != null && !dto.getMobileNumber().isEmpty()) {
            user.setMobileNumber(dto.getMobileNumber());
        }

        // ======================
        // PERSONAL INFO SYNC UPDATE
        // ======================
        if (user.getPersonalInfo() != null) {

            PersonalInfo info = user.getPersonalInfo();

            // sync fields from User → PersonalInfo
            if (dto.getFullName() != null) {
                info.setFullName(dto.getFullName());
            }

            if (dto.getEmail() != null) {
                info.setEmail(dto.getEmail());
            }

            if (dto.getMobileNumber() != null) {
                info.setMobileNumber(dto.getMobileNumber());
            }
        }

        userRepository.save(user);

        return mapToDTO(user);
    }

    @Override
    public List<UserResponseDTO> searchByName(String name) {

        List<User> users =
                userRepository.findByFullNameContainingIgnoreCase(name);

        return users.stream()
                .map(this::mapToDTO)
                .toList();
    }
    private UserResponseDTO mapToDTO(User user) {

        UserResponseDTO dto = new UserResponseDTO();

        // ======================
        // USER FIELDS
        // ======================
        dto.setUserId(user.getUserId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setApplicationId(user.getApplicationId());
        dto.setDealerCode(user.getDealerCode());

        dto.setRegistrationType(
                user.getRegistrationType() != null
                        ? user.getRegistrationType().name()
                        : null
        );

        dto.setRole(
                user.getRole() != null
                        ? user.getRole().name()
                        : null
        );

        dto.setCreatedAt(user.getCreatedAt());

        // ======================
        // PERSONAL INFO (IMPORTANT)
        // ======================
        dto.setMobileNumber(
                user.getPersonalInfo() != null && user.getPersonalInfo().getMobileNumber() != null
                        ? user.getPersonalInfo().getMobileNumber()
                        : user.getMobileNumber()
        );

        return dto;
    }
    @Override
    public String sendOtp(String email) {

        Optional<User> optionalUser =
                userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return "User not found";
        }

        User user = optionalUser.get();

        if (user.getOtpGeneratedTime() != null) {

            LocalDateTime nextOtpTime =
                    user.getOtpGeneratedTime().plusMinutes(2);

            if (LocalDateTime.now().isBefore(nextOtpTime)) {

                return "Please wait 2 minutes before requesting a new OTP";
            }
        }
        // generate otp
        int otp = (int) (Math.random() * 900000) + 100000;

        // save otp
        user.setOtp(String.valueOf(otp));
        user.setOtpGeneratedTime(LocalDateTime.now());

        userRepository.save(user);

        // send mail
        String subject = "Forgot Password OTP";

        String body =
                "Dear " + user.getFullName() + ",\n\n" +

                        "We received a request to reset your account password.\n\n" +

                        "Your One-Time Password (OTP) is: " + otp + "\n\n" +

                        "This OTP is valid for 5 minutes.\n" +
                        "Please do not share this OTP with anyone for security reasons.\n\n" +

                        "If you did not request a password reset, " +
                        "please ignore this email or contact our support team immediately.\n\n" +

                        "Regards,\n" +
                        "Vahanfinserv Team\n" +
                        "support@Vahanfinserv.com";

        emailService.sendMail(
                user.getEmail(),
                subject,
                body
        );

        return "OTP sent successfully";
    }
    @Override
    public String verifyOtp(VerifyOtpDTO dto) {

        Optional<User> optionalUser =
                userRepository.findByEmail(dto.getEmail());

        if (optionalUser.isEmpty()) {
            return "User not found";
        }

        User user = optionalUser.get();

        // check otp exists
        if (user.getOtp() == null) {
            return "OTP not found";
        }

        // check otp
        if (!user.getOtp().equals(dto.getOtp())) {
            return "Invalid OTP";
        }

        // expiry check
        if (user.getOtpGeneratedTime()
                .plusMinutes(5)
                .isBefore(LocalDateTime.now())) {

            return "OTP expired";
        }

        // verified true
        user.setIsOtpVerified(true);

        userRepository.save(user);

        return "OTP verified successfully";
    }
    @Override
    public String resetPassword(ResetPasswordDTO dto) {

        Optional<User> optionalUser =
                userRepository.findByEmail(dto.getEmail());

        if (optionalUser.isEmpty()) {
            return "User not found";
        }

        User user = optionalUser.get();

        // check otp verified
        if (!user.getIsOtpVerified()) {
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
                user.getPassword())) {

            return "New Password cannot be same as Old Password";
        }
        // update password
        user.setPassword(
                passwordEncoder.encode(dto.getNewPassword())
        );

        // clear otp data
        user.setOtp(null);
        user.setOtpGeneratedTime(null);
        user.setIsOtpVerified(false);

        userRepository.save(user);

        // send mail
        String subject = "Password Reset Successfully";

        String body =
                "Dear " + user.getFullName() + ",\n\n" +

                        "We would like to inform you that your account password " +
                        "has been reset successfully.\n\n" +

                        "You can now login using your new password securely.\n\n" +

                        "If you did not perform this password reset request, " +
                        "please contact our support team immediately.\n\n" +

                        "For your security, never share your password or OTP with anyone.\n\n" +

                        "Regards,\n" +
                        "Vahanfinserv Team\n" +
                        "support@Vahanfinserv.com";

        emailService.sendMail(
                user.getEmail(),
                subject,
                body
        );

        return "Password reset successfully";
    }
    @Override
    public void assignBankAndSendMail(Long userId, Long bankId) {

    User user =
            userRepository.findById(userId)
                    .orElseThrow(() ->
                            new RuntimeException("User Not Found"));

    Bank bank =
            bankRepository.findById(bankId)
                    .orElseThrow(() ->
                            new RuntimeException("Bank Not Found"));

    PersonalInfo personalInfo =
            personalInfoRepository
                    .findByUser_UserId(userId)
                    .orElseThrow(() ->
                            new RuntimeException("Personal Info Not Found"));

    List<Document> documents =
            documentRepository.findByUser_UserId(userId);

    user.setBank(bank);
    userRepository.save(user);

    // Email
    try {

        System.out.println("EMAIL SENDING...");

        emailService.sendCustomerDetailsToBank(
                bank,
                user,
                personalInfo,
                documents
        );

        System.out.println("EMAIL SENT SUCCESSFULLY");

    } catch (Exception e) {

        System.out.println("EMAIL FAILED");
        e.printStackTrace();
    }
        String token = UUID.randomUUID()
                .toString()
                .replace("-", "");

        System.out.println("TOKEN GENERATED = " + token);

        user.setDocumentDownloadToken(token);

        User savedUser = userRepository.save(user);

        System.out.println("TOKEN AFTER SAVE = "
                + savedUser.getDocumentDownloadToken());
    // WhatsApp
    try {

        System.out.println("WHATSAPP SENDING...");
        System.out.println("BANK NUMBER = " + bank.getContactNumber());

        whatsAppService.sendCustomerDetailsToBank(
                bank,
                user,
                personalInfo,
                documents
        );

        System.out.println("WHATSAPP SENT SUCCESSFULLY");

    } catch (Exception e) {

        System.out.println("WHATSAPP FAILED");
        e.printStackTrace();
    }
}
//.................payment
     @Override
   public RazorpayOrderResponse createOrder(Long userId) {

    try {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User Not Found"));

        RazorpayClient razorpay =
                new RazorpayClient(keyId, keySecret);

        JSONObject options = new JSONObject();

        options.put("amount", 11682); // 116.82 * 100
        options.put("currency", "INR");
        options.put("receipt", "user_" + userId);


        Order order = razorpay.orders.create(options);

        user.setRazorpayOrderId(order.get("id"));

        userRepository.save(user);

        RazorpayOrderResponse response =
                new RazorpayOrderResponse();

        response.setOrderId(order.get("id"));
        response.setCurrency(order.get("currency"));
        response.setAmount(order.get("amount"));

        return response;

    } catch (Exception e) {

        throw new RuntimeException(
                "Order Creation Failed : "
                        + e.getMessage());
    }
}
    @Override
    public void paymentSuccess(Long userId, String orderId, String paymentId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User Not Found"));

        user.setPaymentDone(true);
        user.setPaymentAmount(116.82);

        user.setRazorpayOrderId(orderId);
        user.setRazorpayPaymentId(paymentId);

        user.setPaymentStatus("SUCCESS");

        user.setStatus(UserStatus.PENDING);

        user.setPaymentDate(LocalDateTime.now());

        userRepository.save(user);
    }

    @Override
    public UserResponseDTO searchByEmail(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found with email: " + email));

        return mapToDTO(user);
    }

    @Override
    public List<UserResponseDTO> searchUsersByBank(String bankName) {

        List<User> users =
                userRepository.searchByBank(bankName);

        return users.stream().map(user -> {

            UserResponseDTO dto = new UserResponseDTO();

            dto.setUserId(user.getUserId());
            dto.setFullName(user.getFullName());
            dto.setEmail(user.getEmail());

            if (user.getPersonalInfo() != null) {
                dto.setMobileNumber(
                        user.getPersonalInfo()
                                .getMobileNumber()
                );
            }

            return dto;

        }).toList();
    }

    @Override
    public List<PaymentHistoryDTO> getPaymentHistory() {

        List<User> users = userRepository.findAllPaymentHistory();

        return users.stream().map(user -> {

            PaymentHistoryDTO dto = new PaymentHistoryDTO();

            dto.setUserId(user.getUserId());
            dto.setApplicationId(user.getApplicationId());
            dto.setFullName(user.getFullName());
            dto.setEmail(user.getEmail());
            dto.setMobileNumber(user.getMobileNumber());

            dto.setPaymentStatus(
                    Boolean.TRUE.equals(user.getPaymentDone())
                            ? "APPROVED"
                            : "PENDING"
            );

            dto.setPaymentDate(user.getCreatedAt());

            return dto;

        }).toList();
    }

    @Override
    public PaymentHistoryDTO getPaymentDetails(Long userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        PaymentHistoryDTO dto = new PaymentHistoryDTO();
        dto.setUserId(user.getUserId());
        dto.setApplicationId(user.getApplicationId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setMobileNumber(user.getMobileNumber());
        dto.setPaymentAmount(user.getPaymentAmount());
        dto.setPaymentStatus(
                Boolean.TRUE.equals(user.getPaymentDone())
                        ? "APPROVED" : "PENDING");

        dto.setPaymentDate(user.getCreatedAt());

        return dto;
    }

    @Override
    public DealerUsersResponseDTO getUsersByDealerCode(String dealerCode) {

        List<User> users =
                userRepository.findAllByDealerCode(dealerCode);

        List<UserResponseDTO> userList =
                users.stream().map(user -> {

                    UserResponseDTO dto =
                            new UserResponseDTO();

                    dto.setUserId(user.getUserId());
                    dto.setFullName(user.getFullName());
                    dto.setEmail(user.getEmail());
                    dto.setMobileNumber(user.getMobileNumber());
                    dto.setDealerCode(user.getDealerCode());

                    return dto;

                }).toList();

        DealerUsersResponseDTO response =
                new DealerUsersResponseDTO();

        response.setDealerCode(dealerCode);
        response.setTotalUsers(users.size());
        response.setUsers(userList);

        return response;
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User Not Found With Id : " + id));userRepository.delete(user);
    }

    @Override
    public void deleteDealerUser(
            String dealerCode,
            Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        if (!dealerCode.equals(user.getDealerCode())) {
            throw new RuntimeException(
                    "You are not authorized to delete this user");
        }

        userRepository.delete(user);
    }


    @Override
    public String changePassword(ChangePasswordDTO dto) {

        Optional<User> optionalUser =
                userRepository.findByEmail(dto.getEmail());

        if (optionalUser.isEmpty()) {
            return "User not found";
        }

        User user = optionalUser.get();

        // Old password check
        if (!passwordEncoder.matches(
                dto.getOldPassword(),
                user.getPassword())) {

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
                user.getPassword())) {

            return "New Password cannot be same as Old Password";
        }

        // Update password
        user.setPassword(
                passwordEncoder.encode(dto.getNewPassword()));

        userRepository.save(user);

        return "Password Changed Successfully";
    }
}