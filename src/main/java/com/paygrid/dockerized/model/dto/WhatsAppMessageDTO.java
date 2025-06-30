package com.paygrid.dockerized.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WhatsAppMessageDTO {
    private String phoneNumber;
    private String message;
    private String messageType; // TEXT, IMAGE, DOCUMENT, etc.
    private Map<String, Object> metadata;

    public WhatsAppMessageDTO(String phoneNumber, String message) {
        this.phoneNumber = phoneNumber;
        this.message = message;
        this.messageType = "TEXT";
        this.metadata = null;
    }
} 