package com.finserv.serviceImpl;

import com.finserv.dto.*;
import com.finserv.entity.Dealer;
import com.finserv.entity.LoanApplication;
import com.finserv.entity.User;
import com.finserv.enums.LoanApplicationStatus;
import com.finserv.enums.LoanDocumentStep;
import com.finserv.enums.RegistrationType;
import com.finserv.exception.BadRequestException;
import com.finserv.repository.DealerRepository;
import com.finserv.repository.DocumentRepository;
import com.finserv.repository.LoanApplicationRepository;
import com.finserv.repository.UserRepository;
import com.finserv.service.LoanApplicationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanApplicationServiceImpl implements LoanApplicationService {

    private final LoanApplicationRepository loanApplicationRepository;

    private final UserRepository userRepository;

    private final DealerRepository dealerRepository;

    private final DocumentRepository documentRepository;

    @Override
    @Transactional
    public LoanApplication applyByUser(
            Long userId,
            CreateLoanApplicationDTO dto
    ) {

        User user =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "User Not Found"
                                )
                        );

        LoanApplication loan =
                new LoanApplication();

        loan.setApplicationNumber(
                "LOAN-" + System.currentTimeMillis()
        );

        loan.setUserId(
                user.getUserId()
        );

        loan.setDealerId(
                null
        );

        loan.setFullName(
                user.getFullName()
        );

        loan.setEmail(
                user.getEmail()
        );

        loan.setMobileNumber(
                user.getMobileNumber()
        );

        loan.setLoanAmount(
                dto.getLoanAmount()
        );

        loan.setRegistrationType(
                RegistrationType.INDIVIDUAL
        );

        loan.setCurrentStep(
                LoanDocumentStep.PERSONAL_INFORMATION
        );

        loan.setStatus(
                LoanApplicationStatus.PENDING
        );

        loan.setCreatedAt(
                LocalDateTime.now()
        );

        loan.setUpdatedAt(
                LocalDateTime.now()
        );

        return loanApplicationRepository.save(
                loan
        );
    }

    @Override
    @Transactional
    public LoanApplication applyByDealer(
            Long dealerId,
            DealerLoanApplicationRequestDTO dto
    ) {

        Dealer dealer =
                dealerRepository.findById(dealerId)
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Dealer Not Found"
                                )
                        );

        User user =
                userRepository.findByMobileNumber(
                                dto.getMobileNumber()
                        )
                        .orElseGet(() -> {
                            String emailClean = dto.getEmail() != null ? dto.getEmail().toLowerCase().trim() : "";
                            if (!emailClean.isEmpty()) {
                                java.util.Optional<User> existingUserOpt = userRepository.findByEmail(emailClean);
                                if (existingUserOpt.isPresent()) {
                                    User existingUser = existingUserOpt.get();
                                    existingUser.setMobileNumber(dto.getMobileNumber());
                                    return userRepository.save(existingUser);
                                }
                            }

                            User newUser =
                                    new User();

                            newUser.setFullName(
                                    dto.getFullName()
                            );

                            newUser.setEmail(
                                    dto.getEmail()
                            );

                            newUser.setMobileNumber(
                                    dto.getMobileNumber()
                            );

                            newUser.setApplicationId(
                                    "APP-" + System.currentTimeMillis()
                            );

                            return userRepository.save(
                                    newUser
                            );
                        });

        boolean alreadyExists =
                loanApplicationRepository
                        .existsByDealerIdAndUserIdAndStatusNot(
                                dealer.getDealerId(),
                                user.getUserId(),
                                LoanApplicationStatus.REJECTED
                        );

        if (alreadyExists) {
            throw new BadRequestException(
                    "Active loan application already exists for this user"
            );
        }

        LoanApplication loan =
                new LoanApplication();

        loan.setApplicationNumber(
                "LOAN-" + System.currentTimeMillis()
        );

        loan.setUserId(
                user.getUserId()
        );

        loan.setDealerId(
                dealer.getDealerId()
        );

        loan.setFullName(
                user.getFullName()
        );

        loan.setEmail(
                user.getEmail()
        );

        loan.setMobileNumber(
                user.getMobileNumber()
        );

        loan.setLoanAmount(
                dto.getLoanAmount()
        );

        loan.setAddress(
                dto.getAddress()
        );

        loan.setCity(
                dto.getCity()
        );

        loan.setState(
                dto.getState()
        );

        loan.setPincode(
                dto.getPincode()
        );

        loan.setRegistrationType(
                RegistrationType.DEALER
        );

        loan.setCurrentStep(
                LoanDocumentStep.KYC
        );

        loan.setStatus(
                LoanApplicationStatus.PENDING
        );

        loan.setCreatedAt(
                LocalDateTime.now()
        );

        loan.setUpdatedAt(
                LocalDateTime.now()
        );

        return loanApplicationRepository.save(
                loan
        );
    }

    @Override
    @Transactional
    public LoanApplication savePersonalInfo(
            String applicationNumber,
            LoanPersonalInfoDTO dto
    ) {

        LoanApplication loan =
                getLoan(applicationNumber);

        loan.setFullName(
                dto.getFullName()
        );

        loan.setEmail(
                dto.getEmail()
        );

        loan.setMobileNumber(
                dto.getMobileNumber()
        );

        loan.setAddress(
                dto.getAddress()
        );

        loan.setCity(
                dto.getCity()
        );

        loan.setState(
                dto.getState()
        );

        loan.setPincode(
                dto.getPincode()
        );

        loan.setLoanAmount(
                dto.getLoanAmount()
        );

        loan.setCurrentStep(
                LoanDocumentStep.KYC
        );

        loan.setUpdatedAt(
                LocalDateTime.now()
        );

        if (loan.getUserId() != null) {
            userRepository.findById(loan.getUserId()).ifPresent(u -> {
                u.setMobileNumber(dto.getMobileNumber());
                userRepository.save(u);
            });
        }

        return loanApplicationRepository.save(
                loan
        );
    }

    @Override
    @Transactional
    public LoanApplication saveKyc(
            String applicationNumber,
            LoanKycDTO dto
    ) {

        validateDocument(
                dto.getPanDocumentId(),
                "PAN Document Not Found"
        );

        validateDocument(
                dto.getAadhaarFrontDocumentId(),
                "Aadhaar Front Document Not Found"
        );

        validateDocument(
                dto.getAadhaarBackDocumentId(),
                "Aadhaar Back Document Not Found"
        );

        LoanApplication loan =
                getLoan(applicationNumber);

        loan.setPanDocumentId(
                dto.getPanDocumentId()
        );

        loan.setAadhaarFrontDocumentId(
                dto.getAadhaarFrontDocumentId()
        );

        loan.setAadhaarBackDocumentId(
                dto.getAadhaarBackDocumentId()
        );

        loan.setCurrentStep(
                LoanDocumentStep.RESIDENTIAL
        );

        loan.setUpdatedAt(
                LocalDateTime.now()
        );

        return loanApplicationRepository.save(
                loan
        );
    }

    @Override
    @Transactional
    public LoanApplication saveResidential(
            String applicationNumber,
            LoanResidentialDTO dto
    ) {

        validateDocument(
                dto.getResidentialProofDocumentId(),
                "Residential Proof Document Not Found"
        );

        LoanApplication loan =
                getLoan(applicationNumber);

        loan.setResidentialProofDocumentId(
                dto.getResidentialProofDocumentId()
        );

        loan.setCurrentStep(
                LoanDocumentStep.INCOME
        );

        loan.setUpdatedAt(
                LocalDateTime.now()
        );

        return loanApplicationRepository.save(
                loan
        );
    }

    @Override
    @Transactional
    public LoanApplication saveIncome(
            String applicationNumber,
            LoanIncomeDTO dto
    ) {

        if (dto.getSalarySlipDocumentId() != null) {
            validateDocument(
                    dto.getSalarySlipDocumentId(),
                    "Salary Slip Document Not Found"
            );
        }

        if (dto.getBankStatementDocumentId() != null) {
            validateDocument(
                    dto.getBankStatementDocumentId(),
                    "Bank Statement Document Not Found"
            );
        }

        if (dto.getAppointmentLetterDocumentId() != null) {
            validateDocument(
                    dto.getAppointmentLetterDocumentId(),
                    "Appointment Letter Document Not Found"
            );
        }

        if (dto.getItrDocumentId() != null) {
            validateDocument(
                    dto.getItrDocumentId(),
                    "ITR Document Not Found"
            );
        }

        LoanApplication loan =
                getLoan(applicationNumber);

        loan.setEmploymentType(
                dto.getEmploymentType()
        );

        loan.setSalarySlipDocumentId(
                dto.getSalarySlipDocumentId()
        );

        loan.setBankStatementDocumentId(
                dto.getBankStatementDocumentId()
        );

        loan.setAppointmentLetterDocumentId(
                dto.getAppointmentLetterDocumentId()
        );

        loan.setItrDocumentId(
                dto.getItrDocumentId()
        );

        loan.setCurrentStep(
                LoanDocumentStep.VEHICLE
        );

        loan.setUpdatedAt(
                LocalDateTime.now()
        );

        return loanApplicationRepository.save(
                loan
        );
    }

    @Override
    @Transactional
    public LoanApplication saveVehicle(
            String applicationNumber,
            LoanVehicleDTO dto
    ) {

        validateDocument(
                dto.getRcDocumentId(),
                "RC Document Not Found"
        );

        validateDocument(
                dto.getInsuranceDocumentId(),
                "Insurance Document Not Found"
        );

        if (dto.getCarFrontDocumentId() != null) {
            validateDocument(
                    dto.getCarFrontDocumentId(),
                    "Car Front Document Not Found"
            );
        }

        if (dto.getCarBackDocumentId() != null) {
            validateDocument(
                    dto.getCarBackDocumentId(),
                    "Car Back Document Not Found"
            );
        }

        LoanApplication loan =
                getLoan(applicationNumber);

        loan.setRcDocumentId(
                dto.getRcDocumentId()
        );

        loan.setInsuranceDocumentId(
                dto.getInsuranceDocumentId()
        );

        loan.setCarFrontDocumentId(
                dto.getCarFrontDocumentId()
        );

        loan.setCarBackDocumentId(
                dto.getCarBackDocumentId()
        );

        loan.setOdometerReading(
                dto.getOdometerReading()
        );

        loan.setChassisNumber(
                dto.getChassisNumber()
        );

        loan.setCurrentStep(
                LoanDocumentStep.VERIFY
        );

        loan.setUpdatedAt(
                LocalDateTime.now()
        );

        return loanApplicationRepository.save(
                loan
        );
    }

    @Override
    @Transactional
    public LoanApplication submitApplication(
            String applicationNumber
    ) {

        LoanApplication loan =
                getLoan(applicationNumber);

        if (loan.getPanDocumentId() == null
                || loan.getAadhaarFrontDocumentId() == null
                || loan.getAadhaarBackDocumentId() == null) {

            throw new BadRequestException(
                    "KYC documents are missing"
            );
        }

        if (loan.getRcDocumentId() == null
                || loan.getInsuranceDocumentId() == null) {

            throw new BadRequestException(
                    "Vehicle documents are missing"
            );
        }

        loan.setStatus(
                LoanApplicationStatus.DOCUMENTS_SUBMITTED
        );

        loan.setUpdatedAt(
                LocalDateTime.now()
        );

        return loanApplicationRepository.save(
                loan
        );
    }

    @Override
    public LoanApplication getByApplicationNumber(
            String applicationNumber
    ) {

        return getLoan(applicationNumber);
    }

    @Override
    public List<LoanApplication> getByUserId(
            Long userId
    ) {

        return loanApplicationRepository.findByUserId(
                userId
        );
    }

    @Override
    public List<LoanApplication> getByDealerId(
            Long dealerId
    ) {

        return loanApplicationRepository.findByDealerId(
                dealerId
        );
    }

    @Override
    public List<LoanApplication> getAllApplications() {

        return loanApplicationRepository.findAll();
    }

    private LoanApplication getLoan(
            String applicationNumber
    ) {

        return loanApplicationRepository
                .findByApplicationNumber(applicationNumber)
                .orElseThrow(() ->
                        new BadRequestException(
                                "Loan Application Not Found"
                        )
                );
    }

    private void validateDocument(
            Long documentId,
            String message
    ) {

        if (documentId == null || documentId <= 0) {
            throw new BadRequestException(
                    message
            );
        }

        boolean exists =
                documentRepository.existsById(
                        documentId
                );

        if (!exists) {
            throw new BadRequestException(
                    message
            );
        }
    }

    @Override
    @jakarta.transaction.Transactional
    public LoanApplication updateStatus(String applicationNumber, LoanApplicationStatus status, String remark) {
        LoanApplication loan = getLoan(applicationNumber);
        if (status != null) {
            loan.setStatus(status);
        }
        if (remark != null) {
            loan.setRemark(remark);
        }
        loan.setUpdatedAt(LocalDateTime.now());
        return loanApplicationRepository.save(loan);
    }
}