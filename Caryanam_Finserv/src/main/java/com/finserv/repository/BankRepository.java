package com.finserv.repository;

import com.finserv.entity.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankRepository extends JpaRepository<Bank, Long> {
    boolean existsByBankName(String bankName);

    boolean existsByEmail(String email);

    boolean existsByContactNumber(String contactNumber);
}