package com.paygrid.dockerized.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.paygrid.dockerized.model.dto.UsuarioRequestDTO;
import com.paygrid.dockerized.model.dto.UsuarioResponseDTO;
import com.paygrid.dockerized.model.entity.Usuario;

@Component
public class UsuarioMapper {

    @Autowired
    private ModelMapper modelMapper;

    public UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        return modelMapper.map(usuario, UsuarioResponseDTO.class);
    }

    public Usuario toEntity(UsuarioRequestDTO usuarioRequestDTO) {
        return modelMapper.map(usuarioRequestDTO, Usuario.class);
    }
}

