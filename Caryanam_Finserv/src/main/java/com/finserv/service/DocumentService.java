package com.finserv.service;

import com.finserv.dto.DocumentCountDTO;
import com.finserv.dto.DocumentResponseDTO;
import com.finserv.dto.RemarkRequestDTO;
import com.finserv.entity.Document;
import com.finserv.enums.DocumentStatus;
import com.finserv.enums.DocumentType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentService {

    List<DocumentResponseDTO> getDocumentsByUserId(Long userId);

    DocumentResponseDTO getDocumentById(Long documentId);

    Document getEntityById(Long documentId);

    void updateStatus(Long documentId, DocumentStatus status);

        void deleteDocument(Long documentId);

    Document updateDocument(Long documentId, MultipartFile file);

    Document addRemarks(Long documentId, RemarkRequestDTO dto);

    List<Document> getPendingDocuments();

    List<Document> getVerifiedDocuments();

    DocumentCountDTO getDocumentCounts(Long userId);

    Object uploadUnified(Long userId, MultipartFile file, String base64, DocumentType type);



    ResponseEntity<byte[]> downloadAllDocumentsByToken(String token);
}