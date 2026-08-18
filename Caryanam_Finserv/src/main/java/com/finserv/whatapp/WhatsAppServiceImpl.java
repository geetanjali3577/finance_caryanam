package com.finserv.whatapp;

import com.finserv.entity.Bank;

import com.finserv.entity.Document;
import com.finserv.entity.PersonalInfo;
import com.finserv.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
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

    @Value("${whatsapp.template.otp:otp_varification}")
    private String otpTemplateName;

    private final RestTemplate restTemplate;

    @Override
    public void sendMobileVerificationOtp(String mobileNumber, String otp) {
        try {
            String maskedNumber = (mobileNumber != null && mobileNumber.length() >= 4)
                    ? "****" + mobileNumber.substring(mobileNumber.length() - 4)
                    : mobileNumber;
            System.out.println("WhatsApp OTP request initiated for mobile ending " + maskedNumber);

            String url = "https://graph.facebook.com/v25.0/"
                    + phoneNumberId
                    + "/messages";

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            String formattedNumber = mobileNumber
                    .replace("+", "")
                    .replace(" ", "")
                    .trim();

            if (!formattedNumber.startsWith("91")) {
                formattedNumber = "91" + formattedNumber;
            }

            Map<String, Object> payload = new HashMap<>();
            payload.put("messaging_product", "whatsapp");
            payload.put("to", formattedNumber);
            payload.put("type", "template");

            Map<String, Object> template = new HashMap<>();
            template.put("name", otpTemplateName);

            Map<String, Object> language = new HashMap<>();
            language.put("code", "en_US");
            template.put("language", language);

            // Body component with OTP parameter
            Map<String, Object> bodyComponent = new HashMap<>();
            bodyComponent.put("type", "body");
            bodyComponent.put("parameters", List.of(
                    Map.of("type", "text", "text", otp),
                    Map.of("type", "text", "text", "Login")
            ));

            // Button component for Copy Code button (sub_type: url, index: 0)
            Map<String, Object> buttonComponent = new HashMap<>();
            buttonComponent.put("type", "button");
            buttonComponent.put("sub_type", "url");
            buttonComponent.put("index", "0");
            buttonComponent.put("parameters", List.of(
                    Map.of("type", "text", "text", otp)
            ));

            template.put("components", List.of(bodyComponent, buttonComponent));
            payload.put("template", template);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    url,
                    request,
                    String.class
            );

            System.out.println("WhatsApp OTP Status : " + response.getStatusCode());

        } catch (HttpClientErrorException e) {
            System.out.println("===== WHATSAPP OTP ERROR =====");
            System.out.println("Status Code : " + e.getStatusCode());
            System.out.println("Response : " + e.getResponseBodyAsString());
            throw new RuntimeException("WhatsApp API failure: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            System.out.println("===== WHATSAPP OTP ERROR =====");
            e.printStackTrace();
            throw new RuntimeException("Failed to send WhatsApp OTP", e);
        }
    }

    @Override
    public void sendCustomerDetailsToBank(
            Bank bank,
            User user,
            PersonalInfo personalInfo,
            List<Document> documents) {

        try {

            sendTemplateMessage(
                    bank.getContactNumber(),
                    user,
                    personalInfo,
                    documents
            );

            System.out.println("======================================");
            System.out.println("WhatsApp Message Sent Successfully");
            System.out.println("Bank Mobile : " + bank.getContactNumber());
            System.out.println("Customer : " + user.getFullName());
            System.out.println("======================================");

        } catch (HttpClientErrorException e) {

            System.out.println("===== WHATSAPP ERROR =====");
            System.out.println("Status Code : " + e.getStatusCode());
            System.out.println("Response : " + e.getResponseBodyAsString());

            throw e;

        } catch (Exception e) {

            System.out.println("===== WHATSAPP ERROR =====");
            e.printStackTrace();

            throw new RuntimeException(
                    "Failed to send WhatsApp message",
                    e
            );
        }
    }


    private void sendTemplateMessage(
            String mobileNumber,
            User user,
            PersonalInfo personalInfo,
            List<Document> documents) {

        String url = "https://graph.facebook.com/v25.0/"
                + phoneNumberId
                + "/messages";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String formattedNumber = mobileNumber
                .replace("+", "")
                .replace(" ", "");

        if (!formattedNumber.startsWith("91")) {
            formattedNumber = "91" + formattedNumber;
        }

        // ZIP Download URL
        String zipDownloadUrl =
                "https://v1.vahanfinserv.com/api/documents/zip?token="
                        + user.getDocumentDownloadToken();
//        String zipDownloadUrl =
//

//                "https://vahanfinserv.com/api/documents/zip?"
//                        + user.getDocumentDownloadToken();

        Map<String, Object> payload = new HashMap<>();
        payload.put("messaging_product", "whatsapp");
        payload.put("to", formattedNumber);
        payload.put("type", "template");

        Map<String, Object> template = new HashMap<>();
        template.put("name", "loan_application");

        Map<String, Object> language = new HashMap<>();
        language.put("code", "en");
        template.put("language", language);

        List<Map<String, Object>> parameters = List.of(
                Map.of("type", "text", "text", user.getFullName()),
                Map.of("type", "text", "text", user.getEmail()),
                Map.of("type", "text", "text", user.getMobileNumber()),
                Map.of("type", "text", "text", personalInfo.getAddress()),
                Map.of("type", "text", "text", personalInfo.getCity()),
                Map.of("type", "text", "text", personalInfo.getState()),
                Map.of("type", "text", "text", personalInfo.getPincode()),
                Map.of("type", "text", "text",
                        String.valueOf(personalInfo.getLoanAmount())),
                Map.of("type", "text", "text", zipDownloadUrl)
        );

        Map<String, Object> bodyComponent = new HashMap<>();
        bodyComponent.put("type", "body");
        bodyComponent.put("parameters", parameters);

        template.put("components", List.of(bodyComponent));

        payload.put("template", template);

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(payload, headers);

        try {

            System.out.println("===== WHATSAPP REQUEST =====");
            System.out.println(payload);

            ResponseEntity<String> response =
                    restTemplate.postForEntity(
                            url,
                            request,
                            String.class
                    );

            System.out.println("WhatsApp Status : "
                    + response.getStatusCode());

            System.out.println("WhatsApp Response : "
                    + response.getBody());

        } catch (HttpClientErrorException e) {

            System.out.println("===== WHATSAPP ERROR =====");
            System.out.println("Status Code : "
                    + e.getStatusCode());

            System.out.println("Response : "
                    + e.getResponseBodyAsString());

            throw e;
        }
    }
}



