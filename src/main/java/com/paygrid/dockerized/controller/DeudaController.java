package com.paygrid.dockerized.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.paygrid.dockerized.model.dto.DeudaRequestDTO;
import com.paygrid.dockerized.model.dto.DeudaResponseDTO;
import com.paygrid.dockerized.service.DeudaService;

import jakarta.validation.Valid;
import java.security.Principal;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/deudas")
public class DeudaController {

    @Autowired
    private DeudaService deudaService;

    @PostMapping("/registro")
    public DeudaResponseDTO registrarDeuda(@Valid @RequestBody DeudaRequestDTO deudaRequestDTO, Principal principal) {
        String email = principal.getName();
        return deudaService.registrarDeuda(deudaRequestDTO, email);
    }

    @GetMapping("/consulta")
    public List<DeudaResponseDTO> consultarDeudasPorMes(@RequestParam YearMonth mes, Principal principal) {
        String email = principal.getName();
        return deudaService.consultarDeudasPorMes(email, mes);
    }

    @PatchMapping("/{deudaId}/pagar")
    public void marcarComoPagada(@PathVariable Long deudaId, Principal principal) {
        String email = principal.getName();
        deudaService.marcarComoPagada(deudaId, email);
    }

    @GetMapping("/alertas")
    public List<DeudaResponseDTO> alertarVencimientosHoy(Principal principal) {
        String email = principal.getName();
        return deudaService.alertarVencimientosHoy(email);
    }
}