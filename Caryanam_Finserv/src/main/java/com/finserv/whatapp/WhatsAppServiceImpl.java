package com.finserv.whatapp;

import com.finserv.entity.Bank;

import com.finserv.entity.Document;
import com.finserv.entity.PersonalInfo;
import com.finserv.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WhatsAppServiceImpl implements WhatsAppService {

    @Value("${whatsapp.access.token}")
    private String accessToken;

    @Value("${whatsapp.phone.number.id}")
    private String phoneNumberId;

    private final RestTemplate restTemplate;

    @Override
    public void sendCustomerDetailsToBank(
            Bank bank,
            User user,
            PersonalInfo personalInfo,
            List<Document> documents)  {

        try {

            String message =
                    "🏦 NEW LOAN APPLICATION\n\n" +

                            "👤 CUSTOMER DETAILS\n" +
                            "Name : " + user.getFullName() + "\n" +
                            "Email : " + user.getEmail() + "\n" +
                            "Mobile : " + user.getMobileNumber() + "\n\n" +

                            "📍 ADDRESS DETAILS\n" +
                            "Address : " + personalInfo.getAddress() + "\n" +
                            "City : " + personalInfo.getCity() + "\n" +
                            "State : " + personalInfo.getState() + "\n" +
                            "Pincode : " + personalInfo.getPincode() + "\n\n" +

                            "💰 LOAN DETAILS\n" +
                            "Loan Amount : ₹" + personalInfo.getLoanAmount() + "\n\n" +

                            "📄 DOCUMENTS\n";

            for (Document doc : documents) {

                message += "• "
                        + doc.getDocumentType()
                        + " : "
                        + doc.getFileName()
                        + "\n";

                message += "Download Link : "
                        + "https://vahanfinserv.com/api/documents/download/"
                        + doc.getDocumentId()
                        + "\n\n";
            }

            message += "\nRegards,\nFinserv Team";
            sendTextMessage(bank.getContactNumber(), message);
            System.out.println("========== WHATSAPP MESSAGE ==========");
            System.out.println(message);
            System.out.println("======================================");
           // sendTextMessage(bank.getContactNumber(), message);

        } catch (Exception e) {
            throw new RuntimeException("WhatsApp Send Failed", e);
        }

    }

    private void sendTextMessage(String mobileNumber, String message) {


        String url =
                "https://graph.facebook.com/v25.0/"
                        + phoneNumberId
                        + "/messages";

        HttpHeaders headers = new HttpHeaders();

        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> text = new HashMap<>();
        text.put("body", message);

        Map<String, Object> payload = new HashMap<>();

        payload.put("messaging_product", "whatsapp");

        String formattedNumber = mobileNumber
                .replace("+", "")
                .replace(" ", "");

        if (!formattedNumber.startsWith("91")) {
            formattedNumber = "91" + formattedNumber;
        }

        System.out.println("Sending WhatsApp To : " + formattedNumber);

        payload.put("to", formattedNumber);

        payload.put("type", "text");
        payload.put("text", text);

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(payload, headers);

        try {

            ResponseEntity<String> response =
                    restTemplate.postForEntity(
                            url,
                            request,
                            String.class
                    );

            System.out.println(
                    "WhatsApp Status : "
                            + response.getStatusCode()
            );

            System.out.println(
                    "WhatsApp Response : "
                            + response.getBody()
            );

        } catch (Exception e) {

            System.out.println("===== WHATSAPP ERROR =====");
            e.printStackTrace();

            throw new RuntimeException(
                    "WhatsApp Send Failed",
                    e
            );
        }


    }


}
