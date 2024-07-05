package com.paygrid.dockerized.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.paygrid.dockerized.model.dto.UsuarioRegistroRequestDTO;
import com.paygrid.dockerized.model.dto.UsuarioResponseDTO;
import com.paygrid.dockerized.model.entity.Usuario;

@Component
public class UsuarioMapper {
    private final ModelMapper modelMapper;

    public UsuarioMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        return modelMapper.map(usuario, UsuarioResponseDTO.class);
    }

    public Usuario toEntity(UsuarioRegistroRequestDTO usuarioRegistroRequestDTO) {
        return modelMapper.map(usuarioRegistroRequestDTO, Usuario.class);
    }
}
