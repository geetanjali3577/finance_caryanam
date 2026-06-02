package com.finserv.service;

import com.finserv.dto.BankRequestDto;
import com.finserv.dto.BankResponseDto;

import java.util.List;

public interface BankService {

    BankResponseDto saveBank(BankRequestDto dto);

    List<BankResponseDto> getAllBanks();

    BankResponseDto getBankById(Long bankId);

    BankResponseDto updateBank(Long bankId, BankRequestDto dto);

    String deleteBank(Long bankId);


}