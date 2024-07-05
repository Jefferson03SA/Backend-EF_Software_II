package com.paygrid.dockerized.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import com.paygrid.dockerized.model.dto.LoginRequestDTO;
import com.paygrid.dockerized.model.dto.UsuarioRegistroRequestDTO;
import com.paygrid.dockerized.model.dto.UsuarioResponseDTO;
import com.paygrid.dockerized.model.entity.Usuario;
import com.paygrid.dockerized.service.UsuarioService;
import com.paygrid.dockerized.mapper.UsuarioMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;
    private final AuthenticationManager authenticationManager;

    public UsuarioController(UsuarioService usuarioService, UsuarioMapper usuarioMapper, AuthenticationManager authenticationManager) {
        this.usuarioService = usuarioService;
        this.usuarioMapper = usuarioMapper;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<UsuarioResponseDTO> registrarUsuario(@Valid @RequestBody UsuarioRegistroRequestDTO usuarioRegistroRequestDTO) {
        UsuarioResponseDTO usuarioResponseDTO = usuarioService.registrarUsuario(usuarioRegistroRequestDTO);
        return new ResponseEntity<>(usuarioResponseDTO, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioResponseDTO> autenticarUsuario(@Valid @RequestBody LoginRequestDTO loginRequestDTO, HttpServletRequest request, HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getCorreo(), loginRequestDTO.getContraseña())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            Usuario usuario = usuarioService.findByCorreo(loginRequestDTO.getCorreo());
            UsuarioResponseDTO usuarioResponseDTO = usuarioMapper.toResponseDTO(usuario);
            
            request.getSession(true);
            
            return new ResponseEntity<>(usuarioResponseDTO, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> cerrarSesion(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return new ResponseEntity<>("No hay sesión activa", HttpStatus.UNAUTHORIZED);
        }
        
        SecurityContextHolder.clearContext();
        request.getSession().invalidate();
        return new ResponseEntity<>("Sesión cerrada exitosamente", HttpStatus.OK);
    }
}
