package com.paygrid.dockerized.controller;

import jakarta.servlet.http.HttpSession;
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
    private final HttpSession httpSession;

    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponseDTO> registrarUsuario(@Valid @RequestBody UsuarioRequestDTO usuarioRequestDTO) {
        UsuarioResponseDTO usuarioResponseDTO = usuarioService.registrarUsuario(usuarioRequestDTO);
        httpSession.setAttribute("usuario", usuarioResponseDTO);
        return new ResponseEntity<>(usuarioResponseDTO, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> loginUsuario(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        Usuario usuario = usuarioService.obtenerUsuarioEntidadPorEmail(loginRequestDTO.getEmail());
        if (usuario != null && passwordEncoder.matches(loginRequestDTO.getPassword(), usuario.getPassword())) {
            LoginResponseDTO loginResponseDTO = new LoginResponseDTO(usuario.getEmail(), usuario.getUsername());
            httpSession.setAttribute("usuario", loginResponseDTO);
            return new ResponseEntity<>(loginResponseDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logoutUsuario() {
        LoginResponseDTO loginResponseDTO = (LoginResponseDTO) httpSession.getAttribute("usuario");
        if (loginResponseDTO != null) {
            httpSession.invalidate();
            return new ResponseEntity<>("Has cerrado sesión exitosamente.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("No hay una sesión activa.", HttpStatus.BAD_REQUEST);
        }
    }
}
