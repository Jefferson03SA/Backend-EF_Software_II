package com.paygrid.dockerized.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.paygrid.dockerized.model.dto.UsuarioRequestDTO;
import com.paygrid.dockerized.model.dto.UsuarioResponseDTO;
import com.paygrid.dockerized.model.dto.LoginRequestDTO;
import com.paygrid.dockerized.model.dto.LoginResponseDTO;
import com.paygrid.dockerized.model.entity.Usuario;
import com.paygrid.dockerized.service.UsuarioService;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponseDTO> registrarUsuario(@Valid @RequestBody UsuarioRequestDTO usuarioRequestDTO) {
        UsuarioResponseDTO usuarioResponseDTO = usuarioService.registrarUsuario(usuarioRequestDTO);
        return new ResponseEntity<>(usuarioResponseDTO, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> loginUsuario(HttpServletRequest request, @Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        Usuario usuario = usuarioService.obtenerUsuarioEntidadPorEmail(loginRequestDTO.getEmail());
        if (usuario != null && passwordEncoder.matches(loginRequestDTO.getPassword(), usuario.getPassword())) {
            try {
                request.login(loginRequestDTO.getEmail(), loginRequestDTO.getPassword());
                LoginResponseDTO loginResponseDTO = new LoginResponseDTO(usuario.getEmail(), usuario.getUsername());
                return new ResponseEntity<>(loginResponseDTO, HttpStatus.OK);
            } catch (ServletException e) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logoutUsuario(HttpServletRequest request) {
        try {
            request.logout();
            return new ResponseEntity<>("Has cerrado sesión exitosamente.", HttpStatus.OK);
        } catch (ServletException e) {
            return new ResponseEntity<>("Error al cerrar sesión.", HttpStatus.UNAUTHORIZED);
        }
    }
}
