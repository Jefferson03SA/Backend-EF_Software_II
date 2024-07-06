package com.paygrid.dockerized.mapper;

import com.paygrid.dockerized.model.dto.DeudaRequestDTO;
import com.paygrid.dockerized.model.dto.DeudaResponseDTO;
import com.paygrid.dockerized.model.entity.Deuda;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeudaMapper {

    @Autowired
    private ModelMapper modelMapper;

    public Deuda toEntity(DeudaRequestDTO deudaRequestDTO) {
        return modelMapper.map(deudaRequestDTO, Deuda.class);
    }

    public DeudaResponseDTO toDTO(Deuda deuda) {
        return modelMapper.map(deuda, DeudaResponseDTO.class);
    }
}
