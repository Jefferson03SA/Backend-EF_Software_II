package com.paygrid.dockerized.service;

import com.paygrid.dockerized.model.dto.CronogramaPagoEmailDTO;
import com.paygrid.dockerized.model.dto.DeudaResponseDTO;
import com.paygrid.dockerized.model.entity.Usuario;
import com.paygrid.dockerized.model.entity.UserNotification;
import com.paygrid.dockerized.model.enums.NotificationType;
import com.paygrid.dockerized.repository.UserNotificationRepository;
import com.paygrid.dockerized.service.WhatsAppService;
import com.paygrid.dockerized.model.dto.WhatsAppMessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class NotificacionService {

    @Autowired
    private UserNotificationRepository userNotificationRepository;

    @Autowired
    private WhatsAppService whatsAppService;

    /**
     * Notifica al usuario: guarda en la base de datos y envía por WhatsApp.
     */
    public void notificarUsuario(Usuario usuario, String mensaje, NotificationType tipo) {
        // 1. Guardar en la base de datos
        UserNotification notification = new UserNotification();
        notification.setUserId(usuario.getId().toString());
        notification.setPhoneNumber(usuario.getPhoneNumber());
        notification.setMessage(mensaje);
        notification.setType(tipo);
        notification.setSent(false);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setUpdatedAt(LocalDateTime.now());
        userNotificationRepository.save(notification);

        // 2. Enviar por WhatsApp
        if (usuario.getPhoneNumber() != null && !usuario.getPhoneNumber().isEmpty()) {
            WhatsAppMessageDTO dto = new WhatsAppMessageDTO(usuario.getPhoneNumber(), mensaje);
            try {
                whatsAppService.sendMessage(dto);
                notification.setSent(true);
                notification.setSentAt(LocalDateTime.now());
                userNotificationRepository.save(notification);
            } catch (Exception e) {
                // Manejar error de envío, pero la notificación ya está en la DB
            }
        }
    }

    @Autowired
    private JavaMailSender mailSender;

    public void enviarAlerta(DeudaResponseDTO deudaResponseDTO, Usuario usuario) {
        String toAddress = usuario.getEmail();
        String subject = "Alerta de Vencimiento de Deuda";
        String content = "<p>Estimado " + usuario.getUsername() + ",</p>"
                + "<p>La siguiente deuda está a punto de vencer hoy:</p>"
                + "<ul>"
                + "<li><strong>Empresa:</strong> " + deudaResponseDTO.getEmpresa() + "</li>"
                + "<li><strong>Monto:</strong> " + deudaResponseDTO.getMonto() + "</li>"
                + "<li><strong>Fecha de Vencimiento:</strong> " + deudaResponseDTO.getFechaVencimiento() + "</li>"
                + "</ul>"
                + "<p>Por favor, asegúrese de pagar a tiempo para evitar cargos adicionales.</p>"
                + "<p>Saludos,<br>Su equipo de Gestión de Deudas</p>";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

            helper.setFrom("proyect2024up@gmail.com");
            helper.setTo(toAddress);
            helper.setSubject(subject);
            helper.setText(content, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new IllegalStateException("Error al enviar el correo electrónico", e);
        }
    }

    public void enviarAlertaPrestamo(CronogramaPagoEmailDTO cronogramaPagoEmailDTO, Usuario usuario) {
        String toAddress = usuario.getEmail();
        String subject = "Alerta de Vencimiento de Cuota de Préstamo";
        String content = "<p>Estimado " + usuario.getUsername() + ",</p>"
                + "<p>La cuota número <strong>" + cronogramaPagoEmailDTO.getNumero()
                + "</strong> de su préstamo a la entidad <strong>" + cronogramaPagoEmailDTO.getEntidad()
                + "</strong> está a punto de vencer hoy:</p>"
                + "<ul>"
                + "<li><strong>Fecha de Vencimiento:</strong> " + cronogramaPagoEmailDTO.getFechaVencimiento() + "</li>"
                + "<li><strong>Monto de la Cuota:</strong> " + cronogramaPagoEmailDTO.getCuota() + "</li>"
                + "</ul>"
                + "<p>Por favor, asegúrese de pagar a tiempo para evitar cargos adicionales.</p>"
                + "<p>Saludos,<br>Su equipo de Gestión de Deudas</p>";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

            helper.setFrom("proyect2024up@gmail.com");
            helper.setTo(toAddress);
            helper.setSubject(subject);
            helper.setText(content, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new IllegalStateException("Error al enviar el correo electrónico", e);
        }
    }
}
