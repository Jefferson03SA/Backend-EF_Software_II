package com.paygrid.dockerized.controller;

import com.paygrid.dockerized.model.dto.DeudaRequestDTO;
import com.paygrid.dockerized.model.dto.DeudaResponseDTO;
import com.paygrid.dockerized.model.entity.Usuario;
import com.paygrid.dockerized.service.DeudaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/deudas")
public class DeudaController {

    @Autowired
    private DeudaService deudaService;

    @PostMapping
    public ResponseEntity<DeudaResponseDTO> registrarDeuda(HttpSession session, @Valid @RequestBody DeudaRequestDTO deudaRequestDTO) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Long usuarioId = usuario.getId();
        DeudaResponseDTO deudaResponseDTO = deudaService.registrarDeuda(usuarioId, deudaRequestDTO);
        return new ResponseEntity<>(deudaResponseDTO, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<DeudaResponseDTO>> obtenerDeudasPorUsuario(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Long usuarioId = usuario.getId();
        List<DeudaResponseDTO> deudas = deudaService.obtenerDeudasPorUsuario(usuarioId);
        return new ResponseEntity<>(deudas, HttpStatus.OK);
    }

    @PutMapping("/{id}/pagar")
    public ResponseEntity<DeudaResponseDTO> marcarDeudaComoPagada(@PathVariable Long id) {
        DeudaResponseDTO deudaResponseDTO = deudaService.marcarDeudaComoPagada(id);
        return new ResponseEntity<>(deudaResponseDTO, HttpStatus.OK);
    }
}
