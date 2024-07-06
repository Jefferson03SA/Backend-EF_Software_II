package com.paygrid.dockerized.mapper;

import com.paygrid.dockerized.model.dto.PrestamoRequestDTO;
import com.paygrid.dockerized.model.dto.PrestamoResponseDTO;
import com.paygrid.dockerized.model.entity.Prestamo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PrestamoMapper {

    @Autowired
    private ModelMapper modelMapper;

    public Prestamo toEntity(PrestamoRequestDTO prestamoRequestDTO) {
        return modelMapper.map(prestamoRequestDTO, Prestamo.class);
    }

    public PrestamoResponseDTO toDTO(Prestamo prestamo) {
        return modelMapper.map(prestamo, PrestamoResponseDTO.class);
    }
}
