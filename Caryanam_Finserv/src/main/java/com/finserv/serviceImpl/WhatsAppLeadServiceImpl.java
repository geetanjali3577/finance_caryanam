package com.finserv.serviceImpl;

import com.finserv.dto.WhatsAppLeadDTO;
import com.finserv.dto.WhatsAppLeadRequest;
import com.finserv.dto.WhatsAppLeadResponse;
import com.finserv.entity.WhatsAppLead;
import com.finserv.repository.WhatsAppLeadRepository;
import com.finserv.service.WhatsAppLeadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WhatsAppLeadServiceImpl implements WhatsAppLeadService {

    private final WhatsAppLeadRepository leadRepository;

    @Override
    @Transactional
    public WhatsAppLeadResponse saveLead(WhatsAppLeadRequest request) {
        WhatsAppLead lead = WhatsAppLead.builder()
                .name(request.getName())
                .mobileNumber(request.getMobileNumber())
                .build();

        leadRepository.save(lead);

        return WhatsAppLeadResponse.builder()
                .success(true)
                .message("Lead saved successfully.")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WhatsAppLeadDTO> getAllLeads() {
        return leadRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(lead -> WhatsAppLeadDTO.builder()
                        .id(lead.getId())
                        .name(lead.getName())
                        .mobileNumber(lead.getMobileNumber())
                        .createdAt(lead.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByMobileNumber(String mobileNumber) {
        return leadRepository.existsByMobileNumber(mobileNumber);
    }
}
