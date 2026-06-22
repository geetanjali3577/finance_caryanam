package com.finserv.controller;

import com.finserv.entity.Document;
import com.finserv.entity.User;
import com.finserv.repository.DocumentRepository;
import com.finserv.repository.UserRepository;
import com.finserv.service.ZipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/documents")
public class ZipController {

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final ZipService zipService;
//
//    @GetMapping("/zip/{userId}")
//    public ResponseEntity<byte[]> downloadZip(
//            @PathVariable Long userId)
//            throws IOException {
//
//        List<Document> documents =
//                documentRepository.findByUser_UserId(userId);
//
//        byte[] zipData =
//                zipService.createZip(documents);
//
//        return ResponseEntity.ok()
//                .header(
//                        HttpHeaders.CONTENT_DISPOSITION,
//                        "attachment; filename=Loan_Documents.zip"
//                )
//                .contentType(
//                        MediaType.APPLICATION_OCTET_STREAM
//                )
//                .body(zipData);
//    }

    @GetMapping("/zip")
    public ResponseEntity<byte[]> downloadZip(
            @RequestParam String token)
            throws IOException {
        System.out.println("TOKEN = " + token);
        User user = userRepository
                .findByDocumentDownloadToken(token)
                .orElseThrow(() ->
                        new RuntimeException("Invalid Token"));
        System.out.println("TOKEN = " + user.getDocumentDownloadToken());
        List<Document> documents =
                documentRepository.findByUser_UserId(user.getUserId());
        System.out.println("DOC COUNT = " + documents.size());

        byte[] zipData =
                zipService.createZip(documents);

//        return ResponseEntity.ok()
//                .header(
//                        HttpHeaders.CONTENT_DISPOSITION,
//                        "attachment; filename=Loan_Documents.zip"
//                )
//                .contentType(
//                        MediaType.APPLICATION_OCTET_STREAM
//                )
//                .body(zipData);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=documents.zip")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(zipData.length)
                .body(zipData);
    }
}
