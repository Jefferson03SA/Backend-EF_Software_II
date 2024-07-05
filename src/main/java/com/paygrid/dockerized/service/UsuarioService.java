package com.paygrid.dockerized.service;

import java.util.Collections;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.paygrid.dockerized.exception.UsuarioYaExisteException;
import com.paygrid.dockerized.mapper.UsuarioMapper;
import com.paygrid.dockerized.model.dto.LoginRequestDTO;
import com.paygrid.dockerized.model.dto.UsuarioRegistroRequestDTO;
import com.paygrid.dockerized.model.dto.UsuarioResponseDTO;
import com.paygrid.dockerized.model.entity.Usuario;
import com.paygrid.dockerized.repository.UsuarioRepository;

@Service
@Transactional
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.usuarioMapper = usuarioMapper;
    }

    public UsuarioResponseDTO registrarUsuario(UsuarioRegistroRequestDTO usuarioRegistroRequestDTO) {
        if (usuarioRepository.existsByCorreo(usuarioRegistroRequestDTO.getCorreo())) {
            throw new UsuarioYaExisteException("El correo ya está registrado");
        }

        Usuario usuario = usuarioMapper.toEntity(usuarioRegistroRequestDTO);
        usuario.setContraseña(passwordEncoder.encode(usuario.getContraseña()));

        usuarioRepository.save(usuario);

        return usuarioMapper.toResponseDTO(usuario);
    }

    public Usuario autenticarUsuario(LoginRequestDTO loginRequestDTO) {
        Usuario usuario = usuarioRepository.findByCorreo(loginRequestDTO.getCorreo())
                .orElseThrow(() -> new UsernameNotFoundException("Correo o contraseña incorrectos"));

        if (!passwordEncoder.matches(loginRequestDTO.getContraseña(), usuario.getContraseña())) {
            throw new UsernameNotFoundException("Correo o contraseña incorrectos");
        }

        return usuario;
    }

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con correo: " + correo));
        
        return new org.springframework.security.core.userdetails.User(
            usuario.getCorreo(), 
            usuario.getContraseña(), 
            Collections.emptyList() 
        );
    }

    public Usuario findByCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con correo: " + correo));
    }
}