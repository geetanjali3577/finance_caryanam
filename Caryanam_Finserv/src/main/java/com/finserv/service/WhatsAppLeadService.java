package com.finserv.service;

import com.finserv.dto.WhatsAppLeadDTO;
import com.finserv.dto.WhatsAppLeadRequest;
import com.finserv.dto.WhatsAppLeadResponse;
import java.util.List;

public interface WhatsAppLeadService {
    WhatsAppLeadResponse saveLead(WhatsAppLeadRequest request);
    List<WhatsAppLeadDTO> getAllLeads();
}
