package com.paygrid.dockerized.config;

import com.paygrid.dockerized.model.dto.CronogramaPagoEmailDTO;
import com.paygrid.dockerized.model.dto.DeudaResponseDTO;
import com.paygrid.dockerized.model.entity.Usuario;
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
        List<Usuario> usuarios = usuarioRepository.findAll();

        for (Usuario usuario : usuarios) {
            List<DeudaResponseDTO> deudas = deudaService.alertarVencimientosHoy(usuario.getEmail());
            deudas.forEach(deuda -> notificacionService.enviarAlerta(deuda, usuario));

            List<CronogramaPagoEmailDTO> pagos = prestamoService.alertarVencimientosHoy(usuario.getEmail());
            pagos.forEach(pago -> notificacionService.enviarAlertaPrestamo(pago, usuario));
        }
    }
}
