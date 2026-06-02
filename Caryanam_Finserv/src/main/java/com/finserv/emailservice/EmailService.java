package com.finserv.emailservice;

import com.finserv.entity.Bank;
import com.finserv.entity.Document;
import com.finserv.entity.PersonalInfo;
import com.finserv.entity.User;

import java.util.List;

public interface EmailService {


    void sendMail(String to, String subject, String body);


    void sendCustomerDetailsToBank(
            Bank bank,
            User user,
            PersonalInfo personalInfo,
            List<Document> documents
    );
}