package com.paygrid.dockerized.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.paygrid.dockerized.model.enums.Estado;

@Data
@Entity
public class CronogramaPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer numero;
    private LocalDate fechaVencimiento;
    private BigDecimal saldo;
    private BigDecimal capital;
    private BigDecimal interes;
    private BigDecimal cuota;

    @Enumerated(EnumType.STRING)
    private Estado estado;

    @ManyToOne
    @JoinColumn(name = "prestamo_id", nullable = false)
    private Prestamo prestamo;
}

