package com.finserv.controller;

import com.finserv.configuration.CustomUserDetails;
import com.finserv.dto.*;
import com.finserv.service.LoanApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.finserv.enums.LoanApplicationStatus;
import java.util.List;

@RestController
@RequestMapping("/api/loan-applications")
@RequiredArgsConstructor
public class LoanApplicationController {

    private final LoanApplicationService loanApplicationService;

    @PostMapping("/apply-by-dealer/{dealerId}")
    public ResponseEntity<?> applyByDealer(
            @PathVariable Long dealerId,
            @RequestBody DealerLoanApplicationRequestDTO dto
    ) {

        if (dealerId == null) {
            return ResponseEntity.badRequest().body("Dealer id is required");
        }

        if (dto.getFullName() == null || dto.getFullName().isBlank()) {
            return ResponseEntity.badRequest().body("Full name is required");
        }

        if (dto.getMobileNumber() == null || dto.getMobileNumber().isBlank()) {
            return ResponseEntity.badRequest().body("Mobile number is required");
        }

        if (!dto.getMobileNumber().matches("^[6-9][0-9]{9}$")) {
            return ResponseEntity.badRequest().body("Enter valid mobile number");
        }

        return ResponseEntity.ok(
                loanApplicationService.applyByDealer(
                        dealerId,
                        dto
                )
        );
    }

    @PostMapping("/personal/{applicationNumber}")
    public ResponseEntity<?> savePersonalInfo(
            @PathVariable String applicationNumber,
            @RequestBody LoanPersonalInfoDTO dto
    ) {
        return ResponseEntity.ok(
                loanApplicationService.savePersonalInfo(
                        applicationNumber,
                        dto
                )
        );
    }

    @PutMapping("/personal/{applicationNumber}")
    public ResponseEntity<?> savePersonalInfoPut(
            @PathVariable String applicationNumber,
            @RequestBody LoanPersonalInfoDTO dto
    ) {
        return ResponseEntity.ok(
                loanApplicationService.savePersonalInfo(
                        applicationNumber,
                        dto
                )
        );
    }

    @PostMapping("/kyc/{applicationNumber}")
    public ResponseEntity<?> saveKyc(
            @PathVariable String applicationNumber,
            @RequestBody LoanKycDTO dto
    ) {
        return ResponseEntity.ok(
                loanApplicationService.saveKyc(
                        applicationNumber,
                        dto
                )
        );
    }

    @PutMapping("/kyc/{applicationNumber}")
    public ResponseEntity<?> updateKyc(
            @PathVariable String applicationNumber,
            @RequestBody LoanKycDTO dto
    ) {
        return ResponseEntity.ok(
                loanApplicationService.saveKyc(
                        applicationNumber,
                        dto
                )
        );
    }

    @PostMapping("/residential/{applicationNumber}")
    public ResponseEntity<?> saveResidential(
            @PathVariable String applicationNumber,
            @RequestBody LoanResidentialDTO dto
    ) {
        return ResponseEntity.ok(
                loanApplicationService.saveResidential(
                        applicationNumber,
                        dto
                )
        );
    }

    @PutMapping("/residential/{applicationNumber}")
    public ResponseEntity<?> updateResidential(
            @PathVariable String applicationNumber,
            @RequestBody LoanResidentialDTO dto
    ) {
        return ResponseEntity.ok(
                loanApplicationService.saveResidential(
                        applicationNumber,
                        dto
                )
        );
    }

    @PostMapping("/income/{applicationNumber}")
    public ResponseEntity<?> saveIncome(
            @PathVariable String applicationNumber,
            @RequestBody LoanIncomeDTO dto
    ) {
        return ResponseEntity.ok(
                loanApplicationService.saveIncome(
                        applicationNumber,
                        dto
                )
        );
    }

    @PutMapping("/income/{applicationNumber}")
    public ResponseEntity<?> updateIncome(
            @PathVariable String applicationNumber,
            @RequestBody LoanIncomeDTO dto
    ) {
        return ResponseEntity.ok(
                loanApplicationService.saveIncome(
                        applicationNumber,
                        dto
                )
        );
    }

    @PostMapping("/vehicle/{applicationNumber}")
    public ResponseEntity<?> saveVehicle(
            @PathVariable String applicationNumber,
            @RequestBody LoanVehicleDTO dto
    ) {
        return ResponseEntity.ok(
                loanApplicationService.saveVehicle(
                        applicationNumber,
                        dto
                )
        );
    }

    @PutMapping("/vehicle/{applicationNumber}")
    public ResponseEntity<?> updateVehicle(
            @PathVariable String applicationNumber,
            @RequestBody LoanVehicleDTO dto
    ) {
        return ResponseEntity.ok(
                loanApplicationService.saveVehicle(
                        applicationNumber,
                        dto
                )
        );
    }

    @PutMapping("/status/{applicationNumber}")
    public ResponseEntity<?> updateStatus(
            @PathVariable String applicationNumber,
            @RequestParam(required = false) LoanApplicationStatus status,
            @RequestParam(required = false) String remark
    ) {
        return ResponseEntity.ok(
                loanApplicationService.updateStatus(applicationNumber, status, remark)
        );
    }

    @GetMapping("/dealer/{dealerId}")
    public ResponseEntity<?> getByDealerId(@PathVariable Long dealerId) {
        return ResponseEntity.ok(loanApplicationService.getByDealerId(dealerId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(loanApplicationService.getByUserId(userId));
    }

    @GetMapping("/{applicationNumber}")
    public ResponseEntity<?> getByApplicationNumber(@PathVariable String applicationNumber) {
        return ResponseEntity.ok(loanApplicationService.getByApplicationNumber(applicationNumber));
    }

    @GetMapping("/admin/all")
    public ResponseEntity<?> getAllApplicationsAdmin() {
        return ResponseEntity.ok(loanApplicationService.getAllApplications());
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllApplications() {
        return ResponseEntity.ok(loanApplicationService.getAllApplications());
    }

    @PostMapping("/submit/{applicationNumber}")
    public ResponseEntity<?> submitApplicationPost(@PathVariable String applicationNumber) {
        return ResponseEntity.ok(loanApplicationService.submitApplication(applicationNumber));
    }

    @PutMapping("/submit/{applicationNumber}")
    public ResponseEntity<?> submitApplication(@PathVariable String applicationNumber) {
        return ResponseEntity.ok(loanApplicationService.submitApplication(applicationNumber));
    }
}