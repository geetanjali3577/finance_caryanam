package com.finserv.repository;

import com.finserv.entity.LoanApplication;
import com.finserv.enums.LoanApplicationStatus;
import com.finserv.enums.RegistrationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Long> {

    Optional<LoanApplication> findByApplicationNumber(String applicationNumber);

    List<LoanApplication> findByUserId(Long userId);

    List<LoanApplication> findByDealerId(Long dealerId);

    List<LoanApplication> findByRegistrationType(RegistrationType registrationType);
    boolean existsByDealerIdAndUserIdAndStatusNot(
            Long dealerId,
            Long userId,
            LoanApplicationStatus status
    );
}