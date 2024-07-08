package com.paygrid.dockerized.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class CronogramaPagoEmailDTO{

    private Integer numero;
    private LocalDate fechaVencimiento;
    private BigDecimal saldo;
    private BigDecimal capital;
    private BigDecimal interes;
    private BigDecimal cuota;
    private String estado;
    private String entidad;
}
