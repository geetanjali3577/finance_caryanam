package com.finserv.repository;

import com.finserv.entity.Document;
import com.finserv.enums.DocumentStatus;
import com.finserv.enums.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface DocumentRepository
        extends JpaRepository<Document, Long> {
    List<Document> findByUser_UserId(Long userId);

    long countByUserUserIdAndStatus(
            Long userId,
            DocumentStatus status
    );

    List<Document> findByStatus(DocumentStatus status);
    boolean existsByUser_UserIdAndDocumentType(Long userId, DocumentType documentType);

    List<Document> findByStatusAndUser_PaymentDoneTrue(
            DocumentStatus status
    );

    List<Document> findByUser_UserIdAndStatus(Long userId, DocumentStatus documentStatus);

    List<Document> findByUser_DealerCodeAndStatus(String dealerCode, DocumentStatus documentStatus);


    List<Document> findByUser_DealerCode(String dealerCode);
}