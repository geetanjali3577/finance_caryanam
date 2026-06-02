package com.finserv.dto;

import com.finserv.enums.EmploymentType;
import lombok.Data;

@Data
public class LoanIncomeDTO {

    private EmploymentType employmentType;

    private Long salarySlipDocumentId;

    private Long bankStatementDocumentId;

    private Long appointmentLetterDocumentId;

    private Long itrDocumentId;
}