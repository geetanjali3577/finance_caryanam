package com.finserv.controller;

import com.finserv.dto.DocumentCountDTO;
import com.finserv.dto.DocumentResponseDTO;
import com.finserv.dto.RemarkRequestDTO;
import com.finserv.dto.ResponseDto;
import com.finserv.entity.Document;
import com.finserv.enums.DocumentStatus;
import com.finserv.enums.DocumentType;
import com.finserv.exception.BadRequestException;
import com.finserv.service.DocumentService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;


    // UPLOAD DOCUMENT

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDto<?>> uploadDocument(
            @RequestParam("userId") Long userId,
            @RequestParam("type") DocumentType type,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "base64", required = false) String base64) {

        // USER ID VALIDATION
        if (userId == null || userId <= 0) {

            throw new BadRequestException(
                    "Invalid User ID"
            );
        }

        // USER ID LIMIT
        if (userId > 999999999L) {

            throw new BadRequestException(
                    "User ID is too large"
            );
        }

        // DOCUMENT TYPE VALIDATION
        if (type == null) {

            throw new BadRequestException(
                    "Document Type is Required"
            );
        }

        // FILE OR BASE64 VALIDATION
        if ((file == null || file.isEmpty())
                && (base64 == null
                || base64.isBlank())) {

            throw new BadRequestException(
                    "Either File or Base64 is Required"
            );
        }

        // BOTH FILE AND BASE64 NOT ALLOWED
        if (file != null
                && !file.isEmpty()
                && base64 != null
                && !base64.isBlank()) {

            throw new BadRequestException(
                    "Send Either File OR Base64"
            );
        }

        // FILE VALIDATION
        if (file != null && !file.isEmpty()) {

            // FILE NAME VALIDATION
            if (file.getOriginalFilename() == null
                    || file.getOriginalFilename()
                    .trim()
                    .isEmpty()) {

                throw new BadRequestException(
                        "File Name is Missing"
                );
            }

            // FILE NAME LENGTH
            if (file.getOriginalFilename()
                    .length() > 100) {

                throw new BadRequestException(
                        "File Name Too Long"
                );
            }

            // FILE SIZE VALIDATION
            if (file.getSize()
                    > 5 * 1024 * 1024) {

                throw new BadRequestException(
                        "File Size Must Be Less Than 5MB"
                );
            }

            // FILE TYPE VALIDATION
            String contentType =
                    file.getContentType();

            if (contentType == null
                    || (!contentType.equals(
                    "application/pdf")
                    && !contentType.equals(
                    "image/jpeg")
                    && !contentType.equals(
                    "image/png"))) {

                throw new BadRequestException(
                        "Only PDF, JPG and PNG Files Allowed"
                );
            }
        }

        // BASE64 VALIDATION
        if (base64 != null
                && !base64.isBlank()) {

            // MIN LIMIT
            if (base64.length() < 20) {

                throw new BadRequestException(
                        "Invalid Base64 Content"
                );
            }

            // MAX LIMIT
            if (base64.length() > 10000000) {

                throw new BadRequestException(
                        "Base64 Content Too Large"
                );
            }
        }

        // SERVICE CALL
        Object data =
                documentService.uploadUnified(
                        userId,
                        file,
                        base64,
                        type
                );

        return ResponseEntity.ok(

                new ResponseDto<>(

                        200,

                        "Document Uploaded Successfully",

                        data
                )
        );
    }

    // =====================================
    // 2. GET ALL DOCUMENTS BY USER
    // =====================================
    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseDto<List<DocumentResponseDTO>>> getByUserId(
            @PathVariable Long userId
    ) {

        List<DocumentResponseDTO> response =
                documentService.getDocumentsByUserId(userId);

        return ResponseEntity.ok(
                new ResponseDto<>(
                        200,
                        "Documents Fetched Successfully",
                        response
                )
        );
    }

    // =====================================
    // 3. GET SINGLE DOCUMENT
    // =====================================
    @GetMapping("/{documentId}")
    public ResponseEntity<ResponseDto<DocumentResponseDTO>> getByDocumentId(
            @PathVariable Long documentId
    ) {

        DocumentResponseDTO response =
                documentService.getDocumentById(documentId);

        return ResponseEntity.ok(
                new ResponseDto<>(
                        200,
                        "Document Fetched Successfully",
                        response
                )
        );
    }


    @GetMapping("/download/{documentId}")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable Long documentId) {

        Document doc = documentService.getEntityById(documentId);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + doc.getFileName())
                .header("Content-Type", doc.getContentType())
                .body(doc.getFileData());
    }


    @PutMapping("/status/{documentId}")
    public ResponseEntity<ResponseDto<String>> updateStatus(

            @PathVariable Long documentId,
            @RequestParam DocumentStatus status
    ) {

        documentService.updateStatus(documentId, status);

        return ResponseEntity.ok(
                new ResponseDto<>(
                        200,
                        "Document Status Updated",
                        "SUCCESS"
                )
        );
    }


    @GetMapping("/preview/{documentId}")
    public ResponseEntity<byte[]> previewDocument(@PathVariable Long documentId) {

        Document doc = documentService.getEntityById(documentId);

        return ResponseEntity.ok()
                .header("Content-Type", doc.getContentType())
                .header("Content-Disposition", "inline; filename=" + doc.getFileName())
                .body(doc.getFileData());
    }


    @DeleteMapping("/{documentId}")
    public ResponseEntity<ResponseDto> deleteDocument(
            @PathVariable Long documentId) {

        documentService.deleteDocument(documentId);

        return ResponseEntity.ok(
                new ResponseDto<>(
                        200,
                        "Document Deleted Successfully",
                        "Deleted Document Id: " + documentId
                )
        );
    }


    //  UPDATE DOCUMENT
    @PutMapping("/{documentId}")
    public ResponseEntity<ResponseDto> updateDocument(

            @PathVariable Long documentId,
            @RequestParam("file") MultipartFile file
    ) {

        Document document = documentService.updateDocument(documentId, file);

        return ResponseEntity.ok(
                new ResponseDto<>(
                        200,
                        "Document Updated Successfully",
                        document
                )
        );
    }

    //  ADD REMARKS USER TO ADMIN
    @PutMapping("/{documentId}/remarks")
    public ResponseEntity<ResponseDto> addRemarks(

            @PathVariable Long documentId,
            @RequestBody RemarkRequestDTO dto
    ) {

        Document document =
                documentService.addRemarks(documentId, dto);

        return ResponseEntity.ok(
                new ResponseDto<>(
                        200,
                        "Remarks Added Successfully",
                        document
                )
        );
    }

    // GET PENDING DOCUMENTS TO ADMIN
    @GetMapping("/pending")
    public ResponseEntity<ResponseDto> getPendingDocuments() {

        List<Document> pendingDocs =
                documentService.getPendingDocuments();

        return ResponseEntity.ok(
                new ResponseDto<>(
                        200,
                        "Pending Documents Fetched Successfully",
                        pendingDocs
                )
        );
    }

    //  GET VERIFIED DOCUMENTS TO ADMIN
    @GetMapping("/verified")
    public ResponseEntity<ResponseDto> getVerifiedDocuments() {

        List<Document> verifiedDocs =
                documentService.getVerifiedDocuments();

        return ResponseEntity.ok(
                new ResponseDto<>(
                        200,
                        "Verified Documents Fetched Successfully",
                        verifiedDocs
                )
        );
    }

    @GetMapping("/count/{userId}")
    public ResponseEntity<ResponseDto> getDocumentCounts(
            @PathVariable Long userId) {

        DocumentCountDTO counts =
                documentService.getDocumentCounts(userId);

        return ResponseEntity.ok(
                new ResponseDto<>(
                        200,
                        "Document Counts Fetched Successfully",
                        counts
                )
        );
    }
}


