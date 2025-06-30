package com.paygrid.dockerized.config;

import com.paygrid.dockerized.model.dto.CronogramaPagoEmailDTO;
import com.paygrid.dockerized.model.dto.DeudaResponseDTO;
import com.paygrid.dockerized.model.entity.Deuda;
import com.paygrid.dockerized.model.entity.Usuario;
import com.paygrid.dockerized.model.enums.NotificationType;
import com.paygrid.dockerized.repository.UsuarioRepository;
import com.paygrid.dockerized.service.DeudaService;
import com.paygrid.dockerized.service.NotificacionService;
import com.paygrid.dockerized.service.PrestamoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificacionProgramada {

    @Autowired
    private DeudaService deudaService;

    @Autowired
    private PrestamoService prestamoService;

    @Autowired
    private NotificacionService notificacionService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Scheduled(cron = "0 0 8 * * ?")
    public void enviarNotificacionesDiarias() {
        // --- Lógica para notificaciones de HOY (Email) ---
        List<Usuario> usuarios = usuarioRepository.findAll();
        for (Usuario usuario : usuarios) {
            // Deudas que vencen hoy
            List<DeudaResponseDTO> deudasHoy = deudaService.alertarVencimientosHoy(usuario.getEmail());
            deudasHoy.forEach(deuda -> notificacionService.enviarAlerta(deuda, usuario));

            // Préstamos que vencen hoy
            List<CronogramaPagoEmailDTO> pagos = prestamoService.alertarVencimientosHoy(usuario.getEmail());
            pagos.forEach(pago -> notificacionService.enviarAlertaPrestamo(pago, usuario));
        }
        
        // --- Lógica para recordatorios de MAÑANA (App/WhatsApp) ---
        List<Deuda> deudasManana = deudaService.obtenerDeudasQueVencenManana();
        for (Deuda deuda : deudasManana) {
            Usuario usuario = deuda.getUsuario();
            String mensaje = "¡Recordatorio! Tu deuda con '" + deuda.getEmpresa() + "' por S/ " + deuda.getMonto() + " vence mañana.";
            notificacionService.notificarUsuario(usuario, mensaje, NotificationType.DUE_DATE_REMINDER);
        }
    }
}
