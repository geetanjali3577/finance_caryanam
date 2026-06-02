package com.finserv.controller;

import com.finserv.dto.PersonalInfoRequestDTO;
import com.finserv.dto.PersonalInfoResponseDTO;
import com.finserv.dto.ResponseDto;
import com.finserv.exception.BadRequestException;
import com.finserv.service.PersonalInfoService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/personal-info")
@RequiredArgsConstructor
public class PersonalInfoController {

    private final PersonalInfoService personalInfoService;

    //====================================================
    // SAVE PERSONAL INFO
    //====================================================
    @PostMapping("/save")
    public ResponseEntity<ResponseDto<?>> savePersonalInfo(

            @RequestBody
            PersonalInfoRequestDTO dto
    ) {

        // NULL CHECK
        if (dto == null) {

            throw new BadRequestException(
                    "Request Body is Missing"
            );
        }

        // USER ID VALIDATION
        if (dto.getUserId() == null
                || dto.getUserId() <= 0) {

            throw new BadRequestException(
                    "Invalid User ID"
            );
        }

        // USER ID LIMIT
        if (dto.getUserId() > 999999999L) {

            throw new BadRequestException(
                    "User ID is too large"
            );
        }

        // ADDRESS VALIDATION
        if (dto.getAddress() == null
                || dto.getAddress()
                .trim()
                .isEmpty()) {

            throw new BadRequestException(
                    "Address is Required"
            );
        }

        // ADDRESS MIN LENGTH
        if (dto.getAddress().trim().length() < 5) {

            throw new BadRequestException(
                    "Address must be minimum 5 characters"
            );
        }

        // ADDRESS MAX LENGTH
        if (dto.getAddress().length() > 500) {

            throw new BadRequestException(
                    "Address must be less than 500 characters"
            );
        }

        // ADDRESS FORMAT
        if (!dto.getAddress()
                .matches("^[A-Za-z0-9 ,./#()-]+$")) {

            throw new BadRequestException(
                    "Address contains invalid characters"
            );
        }

        // CITY VALIDATION
        if (dto.getCity() == null
                || dto.getCity()
                .trim()
                .isEmpty()) {

            throw new BadRequestException(
                    "City is Required"
            );
        }

        // CITY MIN LENGTH
        if (dto.getCity().trim().length() < 2) {

            throw new BadRequestException(
                    "City must be minimum 2 characters"
            );
        }

        // CITY MAX LENGTH
        if (dto.getCity().length() > 50) {

            throw new BadRequestException(
                    "City must be less than 50 characters"
            );
        }

        // CITY FORMAT
        if (!dto.getCity()
                .matches("^[A-Za-z ]+$")) {

            throw new BadRequestException(
                    "City must contain only letters"
            );
        }

        // STATE VALIDATION
        if (dto.getState() == null
                || dto.getState()
                .trim()
                .isEmpty()) {

            throw new BadRequestException(
                    "State is Required"
            );
        }

        // STATE MIN LENGTH
        if (dto.getState().trim().length() < 2) {

            throw new BadRequestException(
                    "State must be minimum 2 characters"
            );
        }

        // STATE MAX LENGTH
        if (dto.getState().length() > 50) {

            throw new BadRequestException(
                    "State must be less than 50 characters"
            );
        }

        // STATE FORMAT
        if (!dto.getState()
                .matches("^[A-Za-z ]+$")) {

            throw new BadRequestException(
                    "State must contain only letters"
            );
        }

        // PINCODE VALIDATION
        if (dto.getPincode() == null
                || dto.getPincode()
                .trim()
                .isEmpty()) {

            throw new BadRequestException(
                    "Pincode is Required"
            );
        }

        // PINCODE LENGTH VALIDATION
        if (dto.getPincode().length() != 6) {

            throw new BadRequestException(
                    "Pincode must be exactly 6 digits"
            );
        }

        // PINCODE FORMAT
        if (!dto.getPincode()
                .matches("\\d{6}")) {

            throw new BadRequestException(
                    "Pincode must contain only numbers"
            );
        }

        // PINCODE START VALIDATION
        if (dto.getPincode().startsWith("0")) {

            throw new BadRequestException(
                    "Pincode cannot start with 0"
            );
        }


        // LOAN AMOUNT VALIDATION
        if (dto.getLoanAmount() == null
                || dto.getLoanAmount() <= 0) {

            throw new BadRequestException(
                    "Loan Amount must be greater than 0"
            );
        }

// MAX LIMIT
        if (dto.getLoanAmount() > 100000000) {

            throw new BadRequestException(
                    "Loan Amount is too large"
            );
        }

        // SERVICE CALL
        Object data =
                personalInfoService
                        .savePersonalInfo(dto);

        return ResponseEntity.ok(

                new ResponseDto<>(

                        200,

                        "Personal Information Saved Successfully",

                        data
                )
        );
    }

    // UPADTED INFO

    @PutMapping("/update/{userId}")
    public ResponseEntity<ResponseDto<PersonalInfoResponseDTO>> updatePersonalInfo(
            @PathVariable Long userId,
            @RequestBody PersonalInfoRequestDTO dto
    ) {

        PersonalInfoResponseDTO response =
                personalInfoService.updatePersonalInfo(userId, dto);

        return ResponseEntity.ok(
                new ResponseDto<>(200, "Personal Info Updated Successfully", response)
        );
    }



    @GetMapping("/all")
    public ResponseEntity<ResponseDto<List<PersonalInfoResponseDTO>>> getAllPersonalInfo() {

        List<PersonalInfoResponseDTO> response =
                personalInfoService.getAllPersonalInfo();

        if (response.isEmpty()) {
            return ResponseEntity.ok(
                    new ResponseDto<>(200, "No Data Found", response)
            );
        }

        return ResponseEntity.ok(
                new ResponseDto<>(200, "All Personal Info Fetched Successfully", response)
        );
    }




}

