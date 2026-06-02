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

    // LOGIN API
    @PostMapping("/login")
    public ResponseEntity<ResponseDto<LoginResponseDTO>> login(@RequestBody LoginRequestDTO request) {
        LoginResponseDTO response = authService.login(request);
        return ResponseEntity.ok(new ResponseDto<>(200, "Login Successfully", response));
    }

    // LOGOUT API
    @PostMapping("/logout")
    public ResponseEntity<ResponseDto<String>> logout(HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");
        authService.logout(authHeader);
        return ResponseEntity.ok(new ResponseDto<>(200, "Logout Successfully", null));
    }
}