package com.finserv.controller;

import java.util.List;

import com.finserv.dto.*;
import com.finserv.repository.DealerRepository;
import com.finserv.service.DealerService;


import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dealer")
@RequiredArgsConstructor
public class DealerController {

    private final DealerService dealerService;
    private final DealerRepository dealerRepository;

    // ================= REGISTER DEALER =================
    @PostMapping("/register")
    public ResponseEntity<ResponseDto<DealerResponseDTO>> registerDealer(
            @RequestBody DealerRegisterDTO dto) {

        // NULL CHECK
        if (dto == null) {
            return ResponseEntity.badRequest()
                    .body(new ResponseDto<>(
                            400,
                            "Request Body is Missing",
                            null
                    ));
        }

        // FULL NAME VALIDATION
        if (dto.getFullName() == null
                || dto.getFullName().trim().isEmpty()) {

            return ResponseEntity.badRequest()
                    .body(new ResponseDto<>(
                            400,
                            "Full Name is Required",
                            null
                    ));
        }

        // FULL NAME CHARACTER LIMIT
        if (dto.getFullName().length() > 30) {

            return ResponseEntity.badRequest()
                    .body(new ResponseDto<>(
                            400,
                            "Full Name must be maximum 30 characters",
                            null
                    ));
        }

        // EMAIL VALIDATION
        if (dto.getEmail() == null
                || dto.getEmail().trim().isEmpty()) {

            return ResponseEntity.badRequest()
                    .body(new ResponseDto<>(
                            400,
                            "Email is Required",
                            null
                    ));
        }

        // EMAIL CHARACTER LIMIT
        if (dto.getEmail().length() > 50) {

            return ResponseEntity.badRequest()
                    .body(new ResponseDto<>(
                            400,
                            "Email must be maximum 50 characters",
                            null
                    ));
        }

        // MOBILE VALIDATION
        if (dto.getMobileNumber() == null
                || dto.getMobileNumber().trim().isEmpty()) {

            return ResponseEntity.badRequest()
                    .body(new ResponseDto<>(
                            400,
                            "Mobile Number is Required",
                            null
                    ));
        }

        // MOBILE NUMBER LIMIT
        if (dto.getMobileNumber().length() > 10) {

            return ResponseEntity.badRequest()
                    .body(new ResponseDto<>(
                            400,
                            "Mobile Number must be maximum 10 digits",
                            null
                    ));
        }

        // PASSWORD VALIDATION
        if (dto.getPassword() == null
                || dto.getPassword().trim().isEmpty()) {

            return ResponseEntity.badRequest()
                    .body(new ResponseDto<>(
                            400,
                            "Password is Required",
                            null
                    ));
        }

        // PASSWORD CHARACTER LIMIT
        if (dto.getPassword().length() > 20) {

            return ResponseEntity.badRequest()
                    .body(new ResponseDto<>(
                            400,
                            "Password must be maximum 20 characters",
                            null
                    ));
        }

        DealerResponseDTO response =
                dealerService.registerDealer(dto);

        return ResponseEntity.status(201)
                .body(new ResponseDto<>(
                        201,
                        "Dealer Registered Successfully",
                        response
                ));
    }

    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(
            @RequestParam String email) {

        return ResponseEntity.ok(
                dealerService.sendOtp(email)
        );
    }


    // VERIFY OTP


    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(
            @RequestBody VerifyOtpDTO dto) {

        return ResponseEntity.ok(
                dealerService.verifyOtp(dto)
        );
    }


    // RESET PASSWORD


    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestBody ResetPasswordDTO dto) {

        return ResponseEntity.ok(
                dealerService.resetPassword(dto)
        );
    }

    // ================= UPDATE DEALER =================
    @PutMapping("/update/{id}")
    public ResponseEntity<ResponseDto<DealerResponseDTO>> updateDealer(
            @PathVariable Long id,
            @RequestBody DealerRegisterDTO dto
    ) {

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

        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ResponseDto<>(400, "Email is Required", null));
        }

        if (dto.getMobileNumber() == null || dto.getMobileNumber().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ResponseDto<>(400, "Mobile Number is Required", null));
        }

        DealerResponseDTO response = dealerService.updateDealer(id, dto);

        return ResponseEntity.ok(
                new ResponseDto<>(200, "Dealer Updated Successfully", response)
        );
    }

    // ================= GET ALL DEALERS =================
    @GetMapping("/all")
    public ResponseEntity<ResponseDto<List<DealerResponseDTO>>> getAllDealers() {
        List<DealerResponseDTO> response = dealerService.getAllDealers();
        return ResponseEntity.ok(
                new ResponseDto<>(200, "All Dealers Fetched Successfully", response)
        );
    }
}