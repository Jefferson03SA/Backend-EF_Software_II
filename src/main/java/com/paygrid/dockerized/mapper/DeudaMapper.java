package com.paygrid.dockerized.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.paygrid.dockerized.model.dto.DeudaRequestDTO;
import com.paygrid.dockerized.model.dto.DeudaResponseDTO;
import com.paygrid.dockerized.model.entity.Deuda;

@Component
public class DeudaMapper {

    @Autowired
    private ModelMapper modelMapper;

    public DeudaResponseDTO toResponseDTO(Deuda deuda) {
        return modelMapper.map(deuda, DeudaResponseDTO.class);
    }

    public Deuda toEntity(DeudaRequestDTO deudaRequestDTO) {
        return modelMapper.map(deudaRequestDTO, Deuda.class);
    }
}
