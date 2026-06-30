package com.finserv.enums;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;

public class

DocumentTypeDeserializer extends JsonDeserializer<DocumentType> {

    @Override
    public DocumentType deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();

        if (value == null || value.isBlank()) {
            return null;
        }

        // Handle legacy "AADHAAR" by mapping to "AADHAAR_1"
        if ("AADHAAR".equalsIgnoreCase(value)) {
            return DocumentType.AADHAAR_1;
        }

        // Try direct enum matching
        try {
            return DocumentType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IOException("Invalid DocumentType: " + value, e);
        }
    }
}
