package com.finserv.dto;

import lombok.Data;

@Data
public class LoanResidentialDTO {

    private Long residentialProofDocumentId;
    private String address;
    private String city;
    private String state;
    private String pincode;
}