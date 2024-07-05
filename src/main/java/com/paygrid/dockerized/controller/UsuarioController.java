package com.paygrid.dockerized.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import com.paygrid.dockerized.model.dto.LoginRequestDTO;
import com.paygrid.dockerized.model.dto.UsuarioRegistroRequestDTO;
import com.paygrid.dockerized.model.dto.UsuarioResponseDTO;
import com.paygrid.dockerized.model.entity.Usuario;
import com.paygrid.dockerized.service.UsuarioService;
import com.paygrid.dockerized.mapper.UsuarioMapper;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;

    public UsuarioController(UsuarioService usuarioService, UsuarioMapper usuarioMapper) {
        this.usuarioService = usuarioService;
        this.usuarioMapper = usuarioMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<UsuarioResponseDTO> registrarUsuario(@Valid @RequestBody UsuarioRegistroRequestDTO usuarioRegistroRequestDTO) {
        UsuarioResponseDTO usuarioResponseDTO = usuarioService.registrarUsuario(usuarioRegistroRequestDTO);
        return new ResponseEntity<>(usuarioResponseDTO, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioResponseDTO> autenticarUsuario(@Valid @RequestBody LoginRequestDTO loginRequestDTO, HttpSession session) {
        Usuario usuario = usuarioService.autenticarUsuario(loginRequestDTO);
        session.setAttribute("usuario", usuario);
        UsuarioResponseDTO usuarioResponseDTO = usuarioMapper.toResponseDTO(usuario);
        return new ResponseEntity<>(usuarioResponseDTO, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> cerrarSesion(HttpSession session) {
        session.invalidate();
        return new ResponseEntity<>("Sesión cerrada exitosamente", HttpStatus.OK);
    }
}
