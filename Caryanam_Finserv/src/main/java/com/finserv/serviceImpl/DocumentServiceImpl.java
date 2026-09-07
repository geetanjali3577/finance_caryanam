package com.finserv.serviceImpl;

import com.finserv.dto.DocumentCountDTO;
import com.finserv.dto.DocumentResponseDTO;
import com.finserv.dto.RemarkRequestDTO;
import com.finserv.entity.Dealer;
import com.finserv.entity.Document;
import com.finserv.entity.Notification;
import com.finserv.entity.User;
import com.finserv.enums.DocumentStatus;
import com.finserv.enums.DocumentType;
import com.finserv.exception.BadRequestException;
import com.finserv.repository.DealerRepository;
import com.finserv.repository.DocumentRepository;
import com.finserv.repository.NotificationRepository;
import com.finserv.repository.UserRepository;
import com.finserv.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private static final String UPLOAD_DIR = "media/documents";

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final DealerRepository dealerRepository;
    private final NotificationRepository notificationRepository;

    // =====================================
    // 1. UPLOAD DOCUMENT (FILE / BASE64)
    // =====================================
    @Override
    public Object uploadUnified(Long userId, MultipartFile file, String base64, DocumentType type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User Not Found"));

        boolean exists = documentRepository.existsByUser_UserIdAndDocumentType(userId, type);
        if (exists) {
            throw new BadRequestException(type + " Already Uploaded");
        }

        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Document document = new Document();
            document.setUser(user);
            document.setDocumentType(type);
            document.setStatus(DocumentStatus.PENDING);
            document.setUploadedAt(LocalDateTime.now());

            if (file != null && !file.isEmpty()) {
                String originalFileName = file.getOriginalFilename();
                String cleanFileName = originalFileName != null ? originalFileName.replaceAll("\\s+", "_") : (type + "_" + System.currentTimeMillis());

                Path targetFilePath = uploadPath.resolve(cleanFileName);
                if (Files.exists(targetFilePath)) {
                    cleanFileName = System.currentTimeMillis() + "_" + cleanFileName;
                    targetFilePath = uploadPath.resolve(cleanFileName);
                }

                Files.write(targetFilePath, file.getBytes());

                document.setFileName(originalFileName);
                document.setFilePath(UPLOAD_DIR + "/" + cleanFileName);
                document.setContentType(file.getContentType());
                document.setFileSize(file.getSize());

            } else if (base64 != null && !base64.isBlank()) {
                byte[] decodedBytes;
                try {
                    decodedBytes = Base64.getDecoder().decode(base64);
                } catch (IllegalArgumentException e) {
                    throw new BadRequestException("Invalid Base64 format");
                }

                String cleanFileName = type + "_" + System.currentTimeMillis() + ".pdf";
                Path targetFilePath = uploadPath.resolve(cleanFileName);
                Files.write(targetFilePath, decodedBytes);

                document.setFileName(type + ".pdf");
                document.setFilePath(UPLOAD_DIR + "/" + cleanFileName);
                document.setContentType("application/pdf");
                document.setFileSize((long) decodedBytes.length);

            } else {
                throw new BadRequestException("File or Base64 is required");
            }

            Document savedDocument = documentRepository.save(document);
            return mapToDTO(savedDocument);

        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException("Document Upload Failed: " + e.getMessage());
        }
    }

    // =====================================
    // 2. GET ALL DOCUMENTS BY USER
    // =====================================
    @Override
    public List<DocumentResponseDTO> getDocumentsByUserId(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User Not Found"));

        List<Document> documents = documentRepository.findByUser_UserId(userId);

        return documents.stream()
                .map(this::mapToDTO)
                .toList();
    }

    // =====================================
    // 3. GET SINGLE DOCUMENT
    // =====================================
    @Override
    public DocumentResponseDTO getDocumentById(Long documentId) {
        Document doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new BadRequestException("Document Not Found"));

        return mapToDTO(doc);
    }

    // =====================================
    // MAPPER METHOD
    // =====================================
    private DocumentResponseDTO mapToDTO(Document doc) {
        DocumentResponseDTO dto = new DocumentResponseDTO();
        dto.setDocumentId(doc.getDocumentId());
        dto.setUserId(
                doc.getUser() != null
                        ? doc.getUser().getUserId()
                        : null
        );
        dto.setDocumentType(
                doc.getDocumentType() != null
                        ? doc.getDocumentType().name()
                        : null
        );
        dto.setFileName(doc.getFileName());

        String savedFileName = "";
        if (doc.getFilePath() != null && !doc.getFilePath().isBlank()) {
            savedFileName = Paths.get(doc.getFilePath()).getFileName().toString();
        } else if (doc.getFileName() != null) {
            savedFileName = doc.getFileName().replaceAll("\\s+", "_");
        }

        // Absolute URL
        String fileUrl = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/media/documents/")
                .path(savedFileName)
                .toUriString();

        dto.setFileUrl(fileUrl);
        dto.setStatus(
                doc.getStatus() != null
                        ? doc.getStatus().name()
                        : null
        );
        dto.setUploadedAt(doc.getUploadedAt());
        dto.setRemarks(doc.getRemarks());

        return dto;
    }

    @Override
    public Document getEntityById(Long documentId) {
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document Not Found"));
    }

    @Override
    public void updateStatus(Long documentId, DocumentStatus status) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        // APPROVED validation
        if (status == DocumentStatus.APPROVED && document.getStatus() != DocumentStatus.VERIFIED) {
            throw new RuntimeException("Document must be VERIFIED before APPROVED");
        }

        document.setStatus(status);
        documentRepository.save(document);

        User user = document.getUser();
        if (user != null && user.getDealerCode() != null && !user.getDealerCode().trim().isEmpty()) {
            Optional<Dealer> optionalDealer = dealerRepository.findByDealerCode(user.getDealerCode());
            if (optionalDealer.isPresent()) {
                Dealer dealer = optionalDealer.get();
                Notification notification = new Notification();
                notification.setSenderId(1L); // Admin ID
                notification.setSenderRole("ADMIN");
                notification.setReceiverId(dealer.getDealerId());
                notification.setReceiverRole("DEALER");
                String statusLabel = status == DocumentStatus.APPROVED ? "approved" :
                        (status == DocumentStatus.REJECTED ? "rejected" : "verified");
                notification.setMessage("Admin " + statusLabel + " " + document.getDocumentType() + " document for " + user.getFullName());
                notification.setCreatedAt(LocalDateTime.now());
                notification.setRead(false);
                notificationRepository.save(notification);
            }
        }
    }

    @Override
    public void deleteDocument(Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found with id: " + documentId));

        if (document.getFilePath() != null) {
            try {
                Files.deleteIfExists(Paths.get(document.getFilePath()));
            } catch (Exception ignored) {
            }
        }

        documentRepository.delete(document);
    }

    // UPDATE DOCUMENT
    @Override
    public Document updateDocument(Long documentId, MultipartFile file) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        if (file != null && !file.isEmpty()) {
            try {
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                if (document.getFilePath() != null) {
                    try {
                        Files.deleteIfExists(Paths.get(document.getFilePath()));
                    } catch (Exception ignored) {
                    }
                }

                String originalFileName = file.getOriginalFilename();
                String cleanFileName = originalFileName != null ? originalFileName.replaceAll("\\s+", "_") : ("doc_" + System.currentTimeMillis());

                Path targetFilePath = uploadPath.resolve(cleanFileName);
                if (Files.exists(targetFilePath)) {
                    cleanFileName = System.currentTimeMillis() + "_" + cleanFileName;
                    targetFilePath = uploadPath.resolve(cleanFileName);
                }

                Files.write(targetFilePath, file.getBytes());

                document.setFileName(originalFileName);
                document.setFilePath(UPLOAD_DIR + "/" + cleanFileName);
                document.setContentType(file.getContentType());
                document.setFileSize(file.getSize());

            } catch (Exception e) {
                throw new RuntimeException("Failed to update document file: " + e.getMessage(), e);
            }
        }

        return documentRepository.save(document);
    }

    // ADD REMARKS
    @Override
    public Document addRemarks(Long documentId, RemarkRequestDTO dto) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        document.setRemarks(dto.getRemarks());
        Document savedDoc = documentRepository.save(document);

        User user = document.getUser();
        if (user != null && user.getDealerCode() != null && !user.getDealerCode().trim().isEmpty()) {
            Optional<Dealer> optionalDealer = dealerRepository.findByDealerCode(user.getDealerCode());
            if (optionalDealer.isPresent()) {
                Dealer dealer = optionalDealer.get();
                Notification notification = new Notification();
                notification.setSenderId(1L); // Admin ID
                notification.setSenderRole("ADMIN");
                notification.setReceiverId(dealer.getDealerId());
                notification.setReceiverRole("DEALER");
                notification.setMessage("Admin added remark on " + document.getDocumentType() + " document for " + user.getFullName() + ": " + dto.getRemarks());
                notification.setCreatedAt(LocalDateTime.now());
                notification.setRead(false);
                notificationRepository.save(notification);
            }
        }

        return savedDoc;
    }

    // GET PENDING DOCUMENTS
    @Override
    public List<Document> getPendingDocuments() {
        return documentRepository.findByStatusAndUser_PaymentDoneTrue(DocumentStatus.PENDING);
    }

    // GET VERIFIED DOCUMENTS
    @Override
    public List<Document> getVerifiedDocuments() {
        return documentRepository.findByStatusAndUser_PaymentDoneTrue(DocumentStatus.VERIFIED);
    }

    // Upload Document count
    @Override
    public DocumentCountDTO getDocumentCounts(Long userId) {
        long pending = documentRepository.countByUserUserIdAndStatus(userId, DocumentStatus.PENDING);
        long verified = documentRepository.countByUserUserIdAndStatus(userId, DocumentStatus.VERIFIED);
        long approved = documentRepository.countByUserUserIdAndStatus(userId, DocumentStatus.APPROVED);
        long rejected = documentRepository.countByUserUserIdAndStatus(userId, DocumentStatus.REJECTED);

        return new DocumentCountDTO(pending, verified, approved, rejected);
    }

    @Override
    public ResponseEntity<byte[]> downloadAllDocumentsByToken(String token) {
        User user = userRepository
                .findByDocumentDownloadToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid Download Token"));

        Long userId = user.getUserId();
        List<Document> documents = documentRepository.findByUser_UserId(userId);

        if (documents.isEmpty()) {
            throw new RuntimeException("No Documents Found For User Id : " + userId);
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {

            for (Document document : documents) {
                if (document.getFilePath() != null) {
                    Path path = Paths.get(document.getFilePath());
                    if (Files.exists(path)) {
                        ZipEntry zipEntry = new ZipEntry(document.getFileName() != null ? document.getFileName() : path.getFileName().toString());
                        zos.putNextEntry(zipEntry);
                        zos.write(Files.readAllBytes(path));
                        zos.closeEntry();
                    }
                }
            }

            zos.finish();

            return ResponseEntity.ok()
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=user_" + userId + "_documents.zip"
                    )
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(baos.toByteArray());

        } catch (Exception e) {
            throw new RuntimeException("Error While Creating ZIP File", e);
        }
    }
}