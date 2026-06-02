 package com.finserv.serviceImpl;

import com.finserv.dto.BankRequestDto;
import com.finserv.dto.BankResponseDto;
import com.finserv.entity.Bank;
import com.finserv.repository.BankRepository;
import com.finserv.service.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BankServiceImpl implements BankService {

    @Autowired
    private BankRepository bankRepository;

    @Override
    public BankResponseDto saveBank(BankRequestDto dto) {
        // BANK NAME DUPLICATE CHECK
        if (bankRepository.existsByBankName(dto.getBankName())) {

            throw new RuntimeException("Bank Name Already Exists");
        }

        // EMAIL DUPLICATE CHECK
        if (bankRepository.existsByEmail(dto.getEmail())) {

            throw new RuntimeException("Email Already Exists");
        }

        // CONTACT NUMBER DUPLICATE CHECK
        if (bankRepository.existsByContactNumber(dto.getContactNumber())) {

            throw new RuntimeException("Contact Number Already Exists");
        }

        Bank bank = new Bank();

        bank.setBankName(dto.getBankName());
        bank.setRepresentativeName(dto.getRepresentativeName());
        bank.setContactNumber(dto.getContactNumber());
        bank.setEmail(dto.getEmail());


        Bank savedBank = bankRepository.save(bank);

        return mapToDto(savedBank);
    }

    @Override
    public List<BankResponseDto> getAllBanks() {

        return bankRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public BankResponseDto getBankById(Long bankId) {

        Bank bank = bankRepository.findById(bankId)
                .orElseThrow(() -> new RuntimeException("Bank Not Found"));

        return mapToDto(bank);
    }

    @Override
    public BankResponseDto updateBank(Long bankId, BankRequestDto dto) {

        Bank bank = bankRepository.findById(bankId)
                .orElseThrow(() -> new RuntimeException("Bank Not Found"));

        bank.setBankName(dto.getBankName());
        bank.setRepresentativeName(dto.getRepresentativeName());
        bank.setContactNumber(dto.getContactNumber());
        bank.setEmail(dto.getEmail());

        Bank updatedBank = bankRepository.save(bank);

        return mapToDto(updatedBank);
    }

    @Override
    public String deleteBank(Long bankId) {

        Bank bank = bankRepository.findById(bankId)
                .orElseThrow(() -> new RuntimeException("Bank Not Found"));

        bankRepository.delete(bank);

        return "Bank Deleted Successfully";
    }

    private BankResponseDto mapToDto(Bank bank) {

        BankResponseDto dto = new BankResponseDto();

        dto.setBankId(bank.getBankId());
        dto.setBankName(bank.getBankName());
        dto.setRepresentativeName(bank.getRepresentativeName());
        dto.setContactNumber(bank.getContactNumber());
        dto.setEmail(bank.getEmail());


        return dto;
    }
}