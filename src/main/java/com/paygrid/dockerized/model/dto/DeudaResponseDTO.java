package com.paygrid.dockerized.model.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class DeudaResponseDTO {

    private Long id;
    private String numeroDocumento;
    private String empresa;
    private Double monto;
    private LocalDate fechaVencimiento;
    private boolean pagada;

}

