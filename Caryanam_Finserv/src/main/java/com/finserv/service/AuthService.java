package com.finserv.service;


import com.finserv.dto.LoginRequestDTO;
import com.finserv.dto.LoginResponseDTO;

public interface AuthService {

    LoginResponseDTO login(LoginRequestDTO dto);

    void logout(String token);


}
