package com.finserv.dto;

import lombok.Data;

@Data
public class BankRequestDto {

    private String bankName;

    private String representativeName;

    private String contactNumber;

    private String email;


}