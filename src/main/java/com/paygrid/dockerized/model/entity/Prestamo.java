package com.paygrid.dockerized.model.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal monto;
    private BigDecimal interes;
    private LocalDate fechaDesembolso;
    private Integer plazo;
    private String entidad;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "prestamo", cascade = CascadeType.ALL)
    private List<CronogramaPago> cronogramaPagos;
}
