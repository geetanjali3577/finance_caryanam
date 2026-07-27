package com.finserv.controller;

import com.finserv.dto.WhatsAppLeadDTO;
import com.finserv.dto.WhatsAppLeadRequest;
import com.finserv.dto.WhatsAppLeadResponse;
import com.finserv.service.WhatsAppLeadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/whatsapp-leads")
@RequiredArgsConstructor
@CrossOrigin
public class WhatsAppLeadController {

    private final WhatsAppLeadService leadService;

    @PostMapping
    public ResponseEntity<WhatsAppLeadResponse> saveLead(@Valid @RequestBody WhatsAppLeadRequest request) {
        WhatsAppLeadResponse response = leadService.saveLead(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<WhatsAppLeadDTO>> getAllLeads() {
        List<WhatsAppLeadDTO> leads = leadService.getAllLeads();
        return ResponseEntity.ok(leads);
    }

    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> checkLeadExists(@RequestParam String mobileNumber) {
        boolean exists = leadService.existsByMobileNumber(mobileNumber);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }
}
