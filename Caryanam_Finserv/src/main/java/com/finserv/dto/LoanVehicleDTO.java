package com.finserv.dto;

import lombok.Data;

@Data
public class LoanVehicleDTO {

    private Long rcDocumentId;

    private Long insuranceDocumentId;

    private Long carFrontDocumentId;

    private Long carBackDocumentId;

    private String odometerReading;

    private String chassisNumber;
}