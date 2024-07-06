package com.paygrid.dockerized.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DeudaResponseDTO {

    private Long id;
    private String numeroDocumento;
    private String empresa;
    private BigDecimal monto;
    private LocalDate fechaVencimiento;
    private String estado;
}

