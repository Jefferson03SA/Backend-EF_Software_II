package com.paygrid.dockerized.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class PrestamoResponseDTO {

    private Long id;
    private BigDecimal monto;
    private BigDecimal interes;
    private LocalDate fechaDesembolso;
    private Integer plazo;
    private String entidad;
}

