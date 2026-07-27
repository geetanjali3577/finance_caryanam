package com.finserv.repository;

import com.finserv.entity.WhatsAppLead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WhatsAppLeadRepository extends JpaRepository<WhatsAppLead, Long> {
    List<WhatsAppLead> findAllByOrderByCreatedAtDesc();
    boolean existsByMobileNumber(String mobileNumber);
}
