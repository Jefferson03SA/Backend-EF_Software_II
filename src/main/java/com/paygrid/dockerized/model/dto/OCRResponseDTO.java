package com.paygrid.dockerized.model.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OCRResponseDTO {
    private String empresa;
    private BigDecimal monto;
    private LocalDate fechaVencimiento;
    private String numeroDocumento;
    private String textoExtraido;
    private boolean exito;
    private String mensajeError;
} 