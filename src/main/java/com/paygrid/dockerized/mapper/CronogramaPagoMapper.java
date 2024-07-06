package com.paygrid.dockerized.mapper;

import com.paygrid.dockerized.model.dto.CronogramaPagoDTO;
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
}
