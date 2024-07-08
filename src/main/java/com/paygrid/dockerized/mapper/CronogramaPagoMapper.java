package com.paygrid.dockerized.mapper;

import com.paygrid.dockerized.model.dto.CronogramaPagoDTO;
import com.paygrid.dockerized.model.dto.CronogramaPagoEmailDTO;
import com.paygrid.dockerized.model.entity.CronogramaPago;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CronogramaPagoMapper {

    @Autowired
    private ModelMapper modelMapper;

    public CronogramaPagoDTO toDTO(CronogramaPago cronogramaPago) {
        return modelMapper.map(cronogramaPago, CronogramaPagoDTO.class);
    }

    public CronogramaPagoEmailDTO toEmailDTO(CronogramaPago cronogramaPago) {
        CronogramaPagoEmailDTO dto = modelMapper.map(cronogramaPago, CronogramaPagoEmailDTO.class);
        dto.setEntidad(cronogramaPago.getPrestamo().getEntidad()); // Asegurarse de mapear la entidad
        return dto;
    }

    public CronogramaPago toEntity(CronogramaPagoDTO cronogramaPagoDTO) {
        return modelMapper.map(cronogramaPagoDTO, CronogramaPago.class);
    }
}
