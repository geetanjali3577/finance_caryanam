package com.finserv.emailservice;

public interface EmailService {


    void sendMail(String to, String subject, String body);
}