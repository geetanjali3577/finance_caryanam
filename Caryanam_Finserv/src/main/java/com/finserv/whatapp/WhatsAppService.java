package com.finserv.whatapp;

import com.finserv.entity.Bank;
import com.finserv.entity.Document;
import com.finserv.entity.PersonalInfo;
import com.finserv.entity.User;

import java.util.List;

public interface WhatsAppService {

    void sendCustomerDetailsToBank(
            Bank bank,
            User user,
            PersonalInfo personalInfo,
            List<Document> documents
    );
}
