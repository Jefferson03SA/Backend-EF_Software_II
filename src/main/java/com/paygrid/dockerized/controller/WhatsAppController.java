package com.paygrid.dockerized.controller;

import com.paygrid.dockerized.model.dto.WhatsAppMessageDTO;
import com.paygrid.dockerized.model.entity.WhatsAppSession;
import com.paygrid.dockerized.service.WhatsAppService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/whatsapp")
@RequiredArgsConstructor
public class WhatsAppController {
    private final WhatsAppService whatsAppService;

    @PostMapping("/session/start")
    public ResponseEntity<WhatsAppSession> startSession(@RequestParam String phoneNumber, @RequestParam(required = false) Long userId) {
        return ResponseEntity.ok(whatsAppService.startSession(phoneNumber, userId));
    }

    @PostMapping("/message/send")
    public ResponseEntity<Boolean> sendMessage(@RequestBody WhatsAppMessageDTO messageDTO) {
        boolean sent = whatsAppService.sendMessage(messageDTO);
        return ResponseEntity.ok(sent);
    }

    @GetMapping("/session/status")
    public ResponseEntity<Boolean> getSessionStatus(@RequestParam String phoneNumber) {
        return ResponseEntity.ok(whatsAppService.isSessionActive(phoneNumber));
    }

    @PutMapping("/session/status")
    public ResponseEntity<String> updateSessionStatus(
            @RequestParam String phoneNumber,
            @RequestParam boolean isActive) {
        whatsAppService.updateSessionStatus(phoneNumber, isActive);
        return ResponseEntity.ok("Estado actualizado correctamente");
    }

    @DeleteMapping("/session/unlink")
    public ResponseEntity<String> unlinkSession(@RequestParam String phoneNumber) {
        whatsAppService.unlinkSession(phoneNumber);
        return ResponseEntity.ok("Sesión de WhatsApp desvinculada correctamente");
    }
}