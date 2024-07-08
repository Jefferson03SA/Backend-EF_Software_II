package com.paygrid.dockerized.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.paygrid.dockerized.model.dto.PrestamoDetalleDTO;
import com.paygrid.dockerized.model.dto.PrestamoRequestDTO;
import com.paygrid.dockerized.model.dto.PrestamoResponseDTO;
import com.paygrid.dockerized.service.PrestamoService;

import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/prestamos")
public class PrestamoController {

    @Autowired
    private PrestamoService prestamoService;

    @PostMapping("/registrar")
    public PrestamoResponseDTO registrarPrestamo(@Valid @RequestBody PrestamoRequestDTO prestamoRequestDTO, Principal principal) {
        String email = principal.getName();
        return prestamoService.registrarPrestamo(prestamoRequestDTO, email);
    }

    @GetMapping
    public List<PrestamoResponseDTO> consultarPrestamos(Principal principal) {
        String email = principal.getName();
        return prestamoService.consultarPrestamos(email);
    }

    @GetMapping("/{prestamoId}/cronograma")
    public PrestamoDetalleDTO consultarCronograma(@PathVariable Long prestamoId) {
        return prestamoService.consultarCronograma(prestamoId);
    }

    @PatchMapping("/{prestamoId}/cronograma/{numero}/pagar")
public void marcarPagoComoRealizado(@PathVariable Long prestamoId, @PathVariable int numero) {
    prestamoService.marcarPagoComoRealizado(prestamoId, numero);
}


    @PostMapping("/enviar-notificaciones")
    public void enviarNotificaciones() {
        prestamoService.enviarNotificaciones();
    }
}
