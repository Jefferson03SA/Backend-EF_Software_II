package com.paygrid.dockerized.service;

import com.paygrid.dockerized.exception.UsuarioNoEncontradoException;
import com.paygrid.dockerized.model.dto.UsuarioRequestDTO;
import com.paygrid.dockerized.model.dto.UsuarioResponseDTO;
import com.paygrid.dockerized.model.entity.Usuario;
import com.paygrid.dockerized.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public UsuarioResponseDTO registrarUsuario(UsuarioRequestDTO usuarioRequestDTO) {
        Usuario usuario = modelMapper.map(usuarioRequestDTO, Usuario.class);
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        return modelMapper.map(usuarioGuardado, UsuarioResponseDTO.class);
    }

    public UsuarioResponseDTO obtenerUsuarioPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado con email: " + email));
        return modelMapper.map(usuario, UsuarioResponseDTO.class);
    }

    public UsuarioResponseDTO obtenerUsuarioPorUsername(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado con username: " + username));
        return modelMapper.map(usuario, UsuarioResponseDTO.class);
    }

    public Usuario obtenerUsuarioEntidadPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado con email: " + email));
    }

    public Usuario obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado con id: " + id));
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));
        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getPassword())
                .roles("USER") // Asigna roles según tu necesidad
                .build();
    }
}
