package com.finserv.controller;

import com.finserv.entity.Document;
import com.finserv.repository.DocumentRepository;
import com.finserv.service.ZipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/documents")
public class ZipController {

    private final DocumentRepository documentRepository;
    private final ZipService zipService;

    @GetMapping("/zip/{userId}")
    public ResponseEntity<byte[]> downloadZip(
            @PathVariable Long userId)
            throws IOException {

        List<Document> documents =
                documentRepository.findByUser_UserId(userId);

        byte[] zipData =
                zipService.createZip(documents);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=Loan_Documents.zip"
                )
                .contentType(
                        MediaType.APPLICATION_OCTET_STREAM
                )
                .body(zipData);
    }
}
