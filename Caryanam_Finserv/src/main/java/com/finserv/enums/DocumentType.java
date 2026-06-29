package com.finserv.enums;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = DocumentTypeDeserializer.class)
public enum DocumentType {

    // ID PROOFS
    AADHAAR_1,
    AADHAAR_2,
    PAN,

    // ADDRESS PROOFS
    LIGHT_BILL,
    RENTAL_AGREEMENT,


    // INCOME DOCUMENTS
    SALARY_SLIP_1,
    SALARY_SLIP_2,
    SALARY_SLIP_3,
    BANK_STATEMENT,
    ITR_RETURN,
    APPOINTMENT_LETTER,


    // VEHICLE DOCUMENTS
    RC_1,
    RC_2,
    INSURANCE,


    ODOMETER_READING,
    CHASSIS_NUMBER,
    CAR_FRONT_SIDE_PHOTO,
    CAR_BACK_SIDE_PHOTO,





}