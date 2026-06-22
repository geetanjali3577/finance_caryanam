package com.finserv.repository;
import com.finserv.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByMobileNumber(String mobileNumber);

    List<User> findByFullNameContainingIgnoreCase(String fullName);

    Optional<User> findTopByOrderByUserIdDesc();
    Optional<User> findByMobileNumber(String mobileNumber);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.personalInfo")
    List<User> findAllUsersWithPersonalInfo();

    @Query("""
       SELECT u
       FROM User u
       LEFT JOIN FETCH u.personalInfo
       WHERE u.paymentDone = true
       ORDER BY u.userId DESC
       """)
    List<User> findAllPaidUsers();

    List<User> findByDealerCode(String dealerCode);
    @Query("""
           SELECT u
           FROM User u
           WHERE LOWER(u.bank.bankName)
           LIKE LOWER(CONCAT('%', :bankName, '%'))
           """)
    List<User> searchByBank(@Param("bankName") String bankName);

    @Query("""
            SELECT u
            FROM User u
            ORDER BY u.createdAt DESC
            """)
    List<User> findAllPaymentHistory();

    List<User> findAllByDealerCode(String dealerCode);

    Optional<User> findByDocumentDownloadToken(String token);
}