package com.finserv.dto;

import com.finserv.entity.Bank;
import com.finserv.entity.Document;
import com.finserv.entity.PersonalInfo;
import com.finserv.entity.User;
import lombok.Data;

import java.util.List;

@Data
public class WhatsAppRequest {

    private Bank bank;
    private User user;
    private PersonalInfo personalInfo;
    private List<Document> documents;
}