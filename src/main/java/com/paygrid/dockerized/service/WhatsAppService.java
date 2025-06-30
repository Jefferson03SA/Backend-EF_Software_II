package com.paygrid.dockerized.service;

import com.paygrid.dockerized.exception.InactiveSessionException;
import com.paygrid.dockerized.model.dto.WhatsAppMessageDTO;
import com.paygrid.dockerized.model.entity.WhatsAppSession;
import com.paygrid.dockerized.model.entity.Usuario;
import com.paygrid.dockerized.repository.WhatsAppSessionRepository;
import com.paygrid.dockerized.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WhatsAppService {
    private final WhatsAppSessionRepository sessionRepository;
    private final UsuarioRepository usuarioRepository;
    private final RestTemplate restTemplate;

    @Value("${wppconnect.base.url}")
    private String wppconnectBaseUrl;

    public WhatsAppSession startSession(String phoneNumber) {
        // Verificar primero si el número existe en WhatsApp
        try {
            ResponseEntity<WhatsAppNumberStatus> numberStatus = restTemplate.getForEntity(
                wppconnectBaseUrl + "/check-number/" + phoneNumber,
                WhatsAppNumberStatus.class
            );

            if (numberStatus.getBody() == null || !numberStatus.getBody().isExists()) {
                throw new IllegalArgumentException("El número " + phoneNumber + " no existe en WhatsApp");
            }

            // Verificar el estado del servidor bridge
            ResponseEntity<BridgeStatus> response = restTemplate.getForEntity(wppconnectBaseUrl + "/status", BridgeStatus.class);
            boolean isConnected = response.getBody() != null && response.getBody().isConnected();
            
            // Buscar la sesión más reciente o crear una nueva
            WhatsAppSession existingSession = sessionRepository.findLatestByPhoneNumber(phoneNumber)
                .orElse(new WhatsAppSession());
            
            // Actualizar o crear la sesión
            existingSession.setPhoneNumber(phoneNumber);
            existingSession.setActive(isConnected);
            existingSession.setLastConnection(LocalDateTime.now());
            existingSession.setCreatedAt(existingSession.getCreatedAt() != null ? 
                existingSession.getCreatedAt() : LocalDateTime.now());
            existingSession.setUpdatedAt(LocalDateTime.now());
            
            WhatsAppSession savedSession = sessionRepository.save(existingSession);

            // Sincronizar el número de teléfono en Usuario si hay userId asociado
            if (savedSession.getUserId() != null) {
                usuarioRepository.findById(savedSession.getUserId()).ifPresent(usuario -> {
                    if (usuario.getPhoneNumber() == null || !usuario.getPhoneNumber().equals(phoneNumber)) {
                        usuario.setPhoneNumber(phoneNumber);
                        usuarioRepository.save(usuario);
                    }
                });
            }

            return savedSession;
        } catch (Exception e) {
            // Si no podemos conectar con el servidor bridge o el número no existe
            WhatsAppSession session = new WhatsAppSession();
            session.setPhoneNumber(phoneNumber);
            session.setActive(false);
            session.setCreatedAt(LocalDateTime.now());
            session.setUpdatedAt(LocalDateTime.now());
            return sessionRepository.save(session);
        }
    }

    public WhatsAppSession startSession(String phoneNumber, Long userId) {
        // Validar que el número no esté ya registrado en la base de datos
        if (sessionRepository.findLatestByPhoneNumber(phoneNumber).isPresent()) {
            throw new IllegalArgumentException("El número ya está registrado en la aplicación");
        }
        try {
            ResponseEntity<WhatsAppNumberStatus> numberStatus = restTemplate.getForEntity(
                wppconnectBaseUrl + "/check-number/" + phoneNumber,
                WhatsAppNumberStatus.class
            );
            if (numberStatus.getBody() == null || !numberStatus.getBody().isExists()) {
                throw new IllegalArgumentException("El número " + phoneNumber + " no existe en WhatsApp");
            }
            ResponseEntity<BridgeStatus> response = restTemplate.getForEntity(wppconnectBaseUrl + "/status", BridgeStatus.class);
            boolean isConnected = response.getBody() != null && response.getBody().isConnected();
            WhatsAppSession session = new WhatsAppSession();
            session.setPhoneNumber(phoneNumber);
            session.setActive(isConnected);
            session.setLastConnection(LocalDateTime.now());
            session.setCreatedAt(LocalDateTime.now());
            session.setUpdatedAt(LocalDateTime.now());
            session.setUserId(userId);
            WhatsAppSession savedSession = sessionRepository.save(session);
            if (userId != null) {
                usuarioRepository.findById(userId).ifPresent(usuario -> {
                    if (usuario.getPhoneNumber() == null || !usuario.getPhoneNumber().equals(phoneNumber)) {
                        usuario.setPhoneNumber(phoneNumber);
                        usuarioRepository.save(usuario);
                    }
                });
            }
            return savedSession;
        } catch (Exception e) {
            throw new IllegalArgumentException("Error al registrar la sesión de WhatsApp: " + e.getMessage());
        }
    }

    public boolean sendMessage(WhatsAppMessageDTO messageDTO) {
        // Verificar tanto la sesión en BD como el estado del servidor
        if (!isSessionActive(messageDTO.getPhoneNumber())) {
            throw new InactiveSessionException("No hay una sesión activa para este número: " + messageDTO.getPhoneNumber());
        }

        String url = wppconnectBaseUrl + "/send-message";
        try {
            restTemplate.postForObject(url, messageDTO, String.class);
            return true;
        } catch (Exception e) {
            // Si hay error al enviar, marcar la sesión como inactiva
            updateSessionStatus(messageDTO.getPhoneNumber(), false);
            throw new InactiveSessionException("Error al enviar mensaje: " + e.getMessage());
        }
    }

    public boolean isSessionActive(String phoneNumber) {
        // Solo consulta el estado de la última sesión
        return sessionRepository.findLatestByPhoneNumber(phoneNumber)
            .map(WhatsAppSession::isActive)
            .orElse(false);
    }

    public void updateSessionStatus(String phoneNumber, boolean isActive) {
        sessionRepository.findLatestByPhoneNumber(phoneNumber).ifPresent(session -> {
            session.setActive(isActive);
            session.setLastConnection(LocalDateTime.now());
            session.setUpdatedAt(LocalDateTime.now());
            sessionRepository.save(session);
        });
    }

    public void unlinkSession(String phoneNumber) {
        sessionRepository.findLatestByPhoneNumber(phoneNumber).ifPresent(session -> {
            // Eliminar la sesión de WhatsApp
            sessionRepository.delete(session);
            // Si hay userId, limpiar el número en el usuario
            if (session.getUserId() != null) {
                usuarioRepository.findById(session.getUserId()).ifPresent(usuario -> {
                    if (usuario.getPhoneNumber() != null && usuario.getPhoneNumber().equals(phoneNumber)) {
                        usuario.setPhoneNumber(null);
                        usuarioRepository.save(usuario);
                    }
                });
            }
        });
    }
    
    // Clase para deserializar la respuesta de verificación de número
    private static class WhatsAppNumberStatus {
        private boolean exists;
        private String phoneNumber;
        private boolean hasProfilePic;
        private String status;
        private String timestamp;
        
        public boolean isExists() {
            return exists;
        }
        
        public void setExists(boolean exists) {
            this.exists = exists;
        }
        
        public String getPhoneNumber() {
            return phoneNumber;
        }
        
        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }
        
        public boolean isHasProfilePic() {
            return hasProfilePic;
        }
        
        public void setHasProfilePic(boolean hasProfilePic) {
            this.hasProfilePic = hasProfilePic;
        }
        
        public String getStatus() {
            return status;
        }
        
        public void setStatus(String status) {
            this.status = status;
        }
        
        public String getTimestamp() {
            return timestamp;
        }
        
        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }
    }
    
    private static class BridgeStatus {
        private boolean connected;
        private String timestamp;
        
        public boolean isConnected() {
            return connected;
        }
        
        public void setConnected(boolean connected) {
            this.connected = connected;
        }
        
        public String getTimestamp() {
            return timestamp;
        }
        
        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }
    }
}