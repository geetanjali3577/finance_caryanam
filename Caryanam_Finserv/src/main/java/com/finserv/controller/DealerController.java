package com.finserv.controller;

import java.util.List;

import com.finserv.dto.*;
import com.finserv.repository.DealerRepository;
import com.finserv.service.DealerService;


import com.finserv.service.EmailVerificationService;
import com.finserv.service.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dealer")
@RequiredArgsConstructor
public class DealerController {
//ALL DEALER END POINTS
    private final DealerService dealerService;
    private final DealerRepository dealerRepository;
    private final EmailVerificationService emailVerificationService;
    private final UserService userService;

    // ================= REGISTER DEALER =================

    //DEALER
    @PostMapping("/register")
    public ResponseEntity<ResponseDto<DealerResponseDTO>> registerDealer(
            @RequestBody DealerRegisterDTO dto) {


        if (dto == null) {
            return ResponseEntity.badRequest()
                    .body(new ResponseDto<>(400, "Request Body is Missing", null));
        }

        if (dto.getFullName() == null || dto.getFullName().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ResponseDto<>(400, "Full Name is Required", null));
        }
        if (!dto.getFullName().matches("^[A-Za-z]+(?: [A-Za-z]+)*$")) {
            return ResponseEntity.badRequest()
                    .body(new ResponseDto<>(400, "Name must contain only alphabets and spaces (3-50 characters)", null));
        }

        // FULL NAME CHARACTER LIMIT
        if (dto.getFullName().length() > 30) {

            return ResponseEntity.badRequest()
                    .body(new ResponseDto<>(400, "Full Name must be maximum 30 characters", null));
        }

        // EMAIL VALIDATION
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {

            return ResponseEntity.badRequest()
                    .body(new ResponseDto<>(400, "Email is Required", null));
        }

        // EMAIL CHARACTER LIMIT
        if (dto.getEmail().length() > 50) {
            return ResponseEntity.badRequest()
                    .body(new ResponseDto<>(400, "mail must be maximum 50 characters", null));
        }
        if (!dto.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            return ResponseEntity.badRequest().body(new ResponseDto<>(400, "Only mail format allowed", null));
        }
        if (dto.getMobileNumber() == null || dto.getMobileNumber().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ResponseDto<>(400, "Mobile Number is Required", null));
        }
        if (!dto.getMobileNumber() .matches("\\d+")) {
            return ResponseEntity.badRequest()
                    .body(new ResponseDto<>(400, "Mobile Number must contain only digits", null));
        }

        if (dto.getMobileNumber() .length() != 10) {
            return ResponseEntity.badRequest()
                    .body(new ResponseDto<>(400, "Mobile Number must be exactly 10 digits", null));
        }
        if (!dto.getMobileNumber() .matches("^[6-9]\\d{9}$")) {
            return ResponseEntity.badRequest()
                    .body(new ResponseDto<>(400, "Enter a valid Indian Mobile Number", null));
        }


        if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ResponseDto<>(400, "Password is Required", null));
        }
        if (dto.getPassword().length() > 20) {
            return ResponseEntity.badRequest()
                    .body(new ResponseDto<>(400, "Password must be maximum 20 characters", null));
        }

        DealerResponseDTO response = dealerService.registerDealer(dto);
        return ResponseEntity.status(201)
                .body(new ResponseDto<>(201, "Dealer Registered Successfully", response));
    }

    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@RequestParam String email) {
        return ResponseEntity.ok(dealerService.sendOtp(email));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody VerifyOtpDTO dto) {
        return ResponseEntity.ok(dealerService.verifyOtp(dto));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordDTO dto) {
        return ResponseEntity.ok(dealerService.resetPassword(dto));
    }

    // ================= UPDATE DEALER =================
    @PutMapping("/update/{id}")
    public ResponseEntity<ResponseDto<DealerResponseDTO>> updateDealer(@PathVariable Long id, @RequestBody DealerRegisterDTO dto) {

        if (id == null || id <= 0) {
            return ResponseEntity.badRequest()
                    .body(new ResponseDto<>(400, "Valid Dealer Id is Required", null));
        }

        if (dto == null) {
            return ResponseEntity.badRequest()
                    .body(new ResponseDto<>(400, "Request Body is Missing", null));
        }

        if (dto.getFullName() == null || dto.getFullName().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ResponseDto<>(400, "Name is Required", null));
        }
        if (!dto.getFullName().matches("^[A-Za-z]+(?: [A-Za-z]+)*$")) {
            return ResponseEntity.badRequest()
                    .body(new ResponseDto<>(400,
                            "Name must contain only alphabets and spaces (3-50 characters)",
                            null));
        }

        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ResponseDto<>(400, "Email is Required", null));
        }
        if (!dto.getEmail().matches("^[A-Za-z0-9._%+-]+@gmail\\.com$")) {
            return ResponseEntity.badRequest().body(new ResponseDto<>(400, "Only Gmail format allowed", null));
        }

        if (dto.getMobileNumber() == null || dto.getMobileNumber().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ResponseDto<>(400, "Mobile Number is Required", null));
        }

        if (!dto.getMobileNumber() .matches("\\d+")) {
            return ResponseEntity.badRequest()
                    .body(new ResponseDto<>(400, "Mobile Number must contain only digits", null));
        }


        if (dto.getMobileNumber() .length() != 10) {
            return ResponseEntity.badRequest()
                    .body(new ResponseDto<>(400, "Mobile Number must be exactly 10 digits", null));
        }


        if (!dto.getMobileNumber() .matches("^[6-9]\\d{9}$")) {
            return ResponseEntity.badRequest()
                    .body(new ResponseDto<>(400, "Enter a valid Indian Mobile Number", null));
        }

        DealerResponseDTO response = dealerService.updateDealer(id, dto);

        return ResponseEntity.ok(
                new ResponseDto<>(200, "Dealer Updated Successfully", response));
    }

    // ================= GET ALL DEALERS =================
    //ADMIN
    @GetMapping("/all")
    public ResponseEntity<ResponseDto<List<DealerResponseDTO>>> getAllDealers() {
        List<DealerResponseDTO> response = dealerService.getAllDealers();
        return ResponseEntity.ok(new ResponseDto<>(200, "All Dealers Fetched Successfully", response));
    }

    //ADMIN
    @GetMapping("/search/dealer-code")
    public ResponseEntity<DealerResponseDTO> searchByDealerCode(@RequestParam String dealerCode)
    {

        DealerResponseDTO dealer =
                dealerService.searchByDealerCode(dealerCode);

        return ResponseEntity.ok(dealer);
    }

    @PostMapping("/register/send-otp")
    public ResponseEntity<String> sendRegisterOtp(
            @RequestParam String email){

        return ResponseEntity.ok(
                emailVerificationService.sendRegisterOtp(email)
        );
    }


    @PostMapping("/register/verify-otp")
    public ResponseEntity<String> verifyRegisterOtp(
            @RequestBody VerifyOtpDTO dto){

        return ResponseEntity.ok(
                emailVerificationService.verifyRegisterOtp(dto)
        );
    }


    @DeleteMapping("/{dealerCode}/user/{userId}")
    public ResponseEntity<String> deleteDealerUser(
            @PathVariable String dealerCode,
            @PathVariable Long userId) {

        userService.deleteDealerUser(dealerCode, userId);

        return ResponseEntity.ok("User deleted successfully");
    }

    @DeleteMapping("delete/{dealerId}")
    public ResponseEntity<String> deleteDealer(
            @PathVariable Long dealerId) {

        dealerService.deleteDealer(dealerId);

        return ResponseEntity.ok("Dealer account deleted successfully");
    }

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @RequestBody ChangePasswordDTO dto) {

        return ResponseEntity.ok(
                dealerService.changePassword(dto));
    }
}