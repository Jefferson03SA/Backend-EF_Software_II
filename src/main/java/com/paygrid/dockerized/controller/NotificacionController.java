package com.paygrid.dockerized.controller;

import com.paygrid.dockerized.model.entity.UserNotification;
import com.paygrid.dockerized.model.entity.Usuario;
import com.paygrid.dockerized.repository.UserNotificationRepository;
import com.paygrid.dockerized.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/notificaciones")
public class NotificacionController {
    @Autowired
    private UserNotificationRepository userNotificationRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public List<UserNotification> getNotificaciones(Principal principal) {
        String email = principal.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        return userNotificationRepository.findByUserId(usuario.getId().toString());
    }
}
