package com.finserv.controller;

import com.finserv.dto.LoginRequestDTO;
import com.finserv.dto.LoginResponseDTO;
import com.finserv.dto.ResponseDto;
import com.finserv.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // =====================================================
    // LOGIN API
    // =====================================================
    //ALL
    @PostMapping("/login")
    public ResponseEntity<ResponseDto<LoginResponseDTO>> login(
            @RequestBody LoginRequestDTO request) {

        // REQUEST BODY VALIDATION
        if (request == null) {

            return ResponseEntity.badRequest()
                    .body(new ResponseDto<>(
                            400,
                            "Request Body is Missing",
                            null
                    ));
        }

        // EMAIL VALIDATION
        if (request.getEmail() == null
                || request.getEmail().trim().isEmpty()) {

            return ResponseEntity.badRequest()
                    .body(new ResponseDto<>(
                            400,
                            "Email is Required",
                            null
                    ));
        }

        // EMAIL LENGTH VALIDATION
        if (request.getEmail().length() > 50) {

            return ResponseEntity.badRequest()
                    .body(new ResponseDto<>(
                            400,
                            "Email must be maximum 50 characters",
                            null
                    ));
        }

        // EMAIL FORMAT VALIDATION
        if (!request.getEmail()
                .matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {

            return ResponseEntity.badRequest()
                    .body(new ResponseDto<>(
                            400,
                            "Invalid Email Format",
                            null
                    ));
        }

        // PASSWORD VALIDATION
        if (request.getPassword() == null
                || request.getPassword().trim().isEmpty()) {

            return ResponseEntity.badRequest()
                    .body(new ResponseDto<>(
                            400,
                            "Password is Required",
                            null
                    ));
        }

        // PASSWORD MIN LENGTH
        if (request.getPassword().length() < 6) {

            return ResponseEntity.badRequest()
                    .body(new ResponseDto<>(
                            400,
                            "Password must be minimum 6 characters",
                            null
                    ));
        }

        // PASSWORD MAX LENGTH
        if (request.getPassword().length() > 20) {

            return ResponseEntity.badRequest()
                    .body(new ResponseDto<>(
                            400,
                            "Password must be maximum 20 characters",
                            null
                    ));
        }

        // LOGIN SERVICE CALL
        LoginResponseDTO response =
                authService.login(request);

        return ResponseEntity.ok(
                new ResponseDto<>(
                        200,
                        "Login Successfully",
                        response
                )
        );
    }

    // =====================================================
    // LOGOUT API
    // =====================================================
    @PostMapping("/logout")
    public ResponseEntity<ResponseDto<String>> logout(
            HttpServletRequest request) {

        String authHeader =
                request.getHeader("Authorization");

        // TOKEN VALIDATION
        if (authHeader == null
                || authHeader.trim().isEmpty()) {

            return ResponseEntity.badRequest()
                    .body(new ResponseDto<>(
                            400,
                            "Authorization Token is Missing",
                            null
                    ));
        }

        // BEARER TOKEN VALIDATION
        if (!authHeader.startsWith("Bearer ")) {

            return ResponseEntity.badRequest()
                    .body(new ResponseDto<>(
                            400,
                            "Invalid Authorization Format",
                            null
                    ));
        }

        authService.logout(authHeader);

        return ResponseEntity.ok(
                new ResponseDto<>(
                        200,
                        "Logout Successfully",
                        null
                )
        );
    }
}