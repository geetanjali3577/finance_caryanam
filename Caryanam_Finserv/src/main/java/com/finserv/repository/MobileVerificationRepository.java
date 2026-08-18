package com.finserv.repository;

import com.finserv.entity.MobileVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MobileVerificationRepository
        extends JpaRepository<MobileVerification, Long> {

    Optional<MobileVerification> findByMobileNumber(String mobileNumber);

    boolean existsByMobileNumber(String mobileNumber);
}
