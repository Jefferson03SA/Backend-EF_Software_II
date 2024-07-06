package com.paygrid.dockerized.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.paygrid.dockerized.model.dto.DeudaRequestDTO;
import com.paygrid.dockerized.model.dto.DeudaResponseDTO;
import com.paygrid.dockerized.model.entity.Usuario;
import com.paygrid.dockerized.service.DeudaService;
import com.paygrid.dockerized.service.UsuarioService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/deudas")
public class DeudaController {

    @Autowired
    private DeudaService deudaService;

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/registrar")
    public ResponseEntity<DeudaResponseDTO> registrarDeuda(@RequestBody DeudaRequestDTO deudaRequestDTO, Principal principal) {
  
        Usuario usuario = usuarioService.obtenerUsuarioEntidadPorEmail(principal.getName());
        DeudaResponseDTO nuevaDeuda = deudaService.registrarDeuda(deudaRequestDTO, usuario.getId());
        return new ResponseEntity<>(nuevaDeuda, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<DeudaResponseDTO>> obtenerDeudas(Principal principal) {

        Usuario usuario = usuarioService.obtenerUsuarioEntidadPorEmail(principal.getName());
        List<DeudaResponseDTO> deudas = deudaService.obtenerDeudasPorUsuario(usuario.getId());
        return new ResponseEntity<>(deudas, HttpStatus.OK);
    }

    @PutMapping("/marcar-pagada/{id}")
    public ResponseEntity<DeudaResponseDTO> marcarDeudaComoPagada(@PathVariable Long id) {
        DeudaResponseDTO deuda = deudaService.marcarDeudaComoPagada(id);
        return new ResponseEntity<>(deuda, HttpStatus.OK);
    }
}
