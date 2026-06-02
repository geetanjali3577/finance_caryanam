package com.finserv.repository;


import com.finserv.entity.Dealer;
import com.finserv.enums.DealerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DealerRepository extends JpaRepository<Dealer, Long> {

    Optional<Dealer> findByDealerCode(String dealerCode);



    Optional<Dealer> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByMobileNumber(String mobileNumber);

    boolean existsByDealerCode(String dealerCode);

    Optional<Dealer> findByDealerCodeAndStatus(String dealerCode, DealerStatus dealerStatus);
}