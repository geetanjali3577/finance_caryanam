package com.finserv.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.finserv.enums.DocumentStatus;
import com.finserv.enums.DocumentType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Data
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long documentId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(length = 100)
    private DocumentType documentType;

    private String fileName;

    private String filePath;

    private String contentType;

    private Long fileSize;

    private String remarks;

    @Enumerated(EnumType.STRING)
    private DocumentStatus status;

    private LocalDateTime uploadedAt;
}