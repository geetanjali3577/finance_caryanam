package com.finserv.service;

import com.finserv.dto.*;
import com.finserv.entity.LoanApplication;
import com.finserv.enums.LoanApplicationStatus;

import java.util.List;

public interface LoanApplicationService {

    LoanApplication applyByUser(Long userId, CreateLoanApplicationDTO dto);

    LoanApplication applyByDealer(Long dealerId, DealerLoanApplicationRequestDTO dto);

    LoanApplication savePersonalInfo(String applicationNumber, LoanPersonalInfoDTO dto);

    LoanApplication saveKyc(String applicationNumber, LoanKycDTO dto);

    LoanApplication saveResidential(String applicationNumber, LoanResidentialDTO dto);

    LoanApplication saveIncome(String applicationNumber, LoanIncomeDTO dto);

    LoanApplication saveVehicle(String applicationNumber, LoanVehicleDTO dto);

    LoanApplication submitApplication(String applicationNumber);

    LoanApplication getByApplicationNumber(String applicationNumber);

    List<LoanApplication> getByUserId(Long userId);

    List<LoanApplication> getByDealerId(Long dealerId);

    List<LoanApplication> getAllApplications();

    LoanApplication updateStatus(String applicationNumber, LoanApplicationStatus status, String remark);

}