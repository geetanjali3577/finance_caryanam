package com.finserv.emailservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import com.finserv.entity.*;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.util.List;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;



    @Override
    public void sendMail(String to, String subject, String body) {

        try {

            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom("support@vahanfinserv.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);

            System.out.println("MAIL SENT SUCCESSFULLY");

        } catch (Exception e) {

            System.out.println("MAIL FAILED");
            e.printStackTrace();
        }
    }

    @Override
    public void sendCustomerDetailsToBank(

            Bank bank,
            User user,
            PersonalInfo personalInfo,
            List<Document> documents
    ) {

        try {

            MimeMessage message =
                    mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            message,
                            true
                    );

            helper.setFrom("support@vahanfinserv.com"); // Add this line

            helper.setTo(bank.getEmail());

            helper.setSubject(
                    "New Customer Application"
            );

            String body =
                    "Dear Bank Team,\n\n" +
                            "A new loan application has been submitted. Please find the customer details below:\n\n" +

                            "==============================\n" +
                            "CUSTOMER DETAILS\n" +
                            "==============================\n" +
                            "Name            : " + user.getFullName() + "\n" +
                            "Email           : " + user.getEmail() + "\n" +
                            "Mobile Number   : " + user.getMobileNumber() + "\n\n" +

                            "==============================\n" +
                            "ADDRESS DETAILS\n" +
                            "==============================\n" +
                            "Address         : " + personalInfo.getAddress() + "\n" +
                            "City            : " + personalInfo.getCity() + "\n" +
                            "State           : " + personalInfo.getState() + "\n" +
                            "Pincode         : " + personalInfo.getPincode() + "\n\n" +

                            "==============================\n" +
                            "LOAN DETAILS\n" +
                            "==============================\n" +
                            "Required Amount : ₹" + personalInfo.getLoanAmount() + "\n\n" +

                            "The customer's supporting documents are attached with this email.\n\n" +

                            "Regards,\n" +
                            "Finserv Team";

            helper.setText(body);

            for (Document doc : documents) {

                if (doc.getFileData() != null) {

                    helper.addAttachment(
                            doc.getFileName(),
                            new ByteArrayResource(
                                    doc.getFileData()
                            )
                    );
                }
            }

            mailSender.send(message);

        }   catch (Exception e) {

            System.out.println("===== MAIL ERROR =====");
            e.printStackTrace();

            throw new RuntimeException(e);
        }
        }

}