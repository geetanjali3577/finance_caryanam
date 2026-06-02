package com.finserv.dto;

import lombok.Data;

@Data
public class BankResponseDto {

    private Long bankId;

    private String bankName;

    private String representativeName;

    private String contactNumber;

    private String email;

}