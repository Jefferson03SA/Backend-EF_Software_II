package com.paygrid.dockerized.model.dto;

import java.util.List;

import lombok.Data;

@Data
public class PrestamoDetalleDTO {
    private PrestamoResponseDTO prestamo;
    private List<CronogramaPagoDTO> cronograma;
}

