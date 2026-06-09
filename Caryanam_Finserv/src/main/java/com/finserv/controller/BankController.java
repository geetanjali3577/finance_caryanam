package com.finserv.controller;


import com.finserv.dto.BankRequestDto;
import com.finserv.dto.BankResponseDto;
import com.finserv.service.BankService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/banks")
@RequiredArgsConstructor
@CrossOrigin("*")
public class BankController {

    //ONLY ADMIN
    private final BankService bankService;

    @GetMapping("/api/admin/profile")
    public ResponseEntity<?> profile(Authentication auth) {
        return ResponseEntity.ok(auth.getPrincipal());
    }

    // ADD BANK
    @PostMapping
    public ResponseEntity<?> addBank(
            @RequestBody BankRequestDto dto) {

        // BANK NAME VALIDATION
        if (dto.getBankName() == null
                || dto.getBankName().trim().isEmpty()) {

            return ResponseEntity.badRequest()
                    .body("Bank Name is Required");
        }

        if (dto.getBankName().length() > 50) {

            return ResponseEntity.badRequest()
                    .body("Bank Name must be less than 50 characters");
        }

        // REPRESENTATIVE NAME VALIDATION
        if (dto.getRepresentativeName() == null
                || dto.getRepresentativeName().trim().isEmpty()) {

            return ResponseEntity.badRequest()
                    .body("Representative Name is Required");
        }

        // CONTACT NUMBER VALIDATION
        if (dto.getContactNumber() == null
                || dto.getContactNumber().trim().isEmpty()) {

            return ResponseEntity.badRequest()
                    .body("Contact Number is Required");
        }

        if (!dto.getContactNumber()
                .matches("^[6-9]\\d{9}$")) {

            return ResponseEntity.badRequest()
                    .body("Invalid Contact Number");
        }

        // EMAIL VALIDATION
        if (dto.getEmail() == null
                || dto.getEmail().trim().isEmpty()) {

            return ResponseEntity.badRequest()
                    .body("Email is Required");
        }

        if (!dto.getEmail()
                .matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {

            return ResponseEntity.badRequest()
                    .body("Invalid Email Format");
        }

        return ResponseEntity.ok(bankService.saveBank(dto));
    }

    // UPDATE BANK
    @PutMapping("/{bankId}")
    public ResponseEntity<?> updateBank(
            @PathVariable Long bankId,
            @RequestBody BankRequestDto dto) {

        // BANK ID VALIDATION
        if (bankId == null || bankId <= 0) {

            return ResponseEntity.badRequest()
                    .body("Valid Bank Id is Required");
        }

        return ResponseEntity.ok(
                bankService.updateBank(bankId, dto));
    }

    // GET BANK BY ID
    @GetMapping("/{bankId}")
    public ResponseEntity<?> getBankById(
            @PathVariable Long bankId) {

        // BANK ID VALIDATION
        if (bankId == null || bankId <= 0) {

            return ResponseEntity.badRequest()
                    .body("Valid Bank Id is Required");
        }

        return ResponseEntity.ok(
                bankService.getBankById(bankId));
    }

    // GET ALL BANKS
    @GetMapping
    public ResponseEntity<List<BankResponseDto>> getAllBanks() {

        return ResponseEntity.ok(
                bankService.getAllBanks());
    }

    // DELETE BANK
    @DeleteMapping("/{bankId}")
    public ResponseEntity<?> deleteBank(
            @PathVariable Long bankId) {

        // BANK ID VALIDATION
        if (bankId == null || bankId <= 0) {

            return ResponseEntity.badRequest()
                    .body("Valid Bank Id is Required");
        }

        bankService.deleteBank(bankId);

        return ResponseEntity.ok(
                "Bank Deleted Successfully");
    }
}