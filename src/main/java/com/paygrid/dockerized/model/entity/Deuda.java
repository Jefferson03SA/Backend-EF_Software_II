package com.paygrid.dockerized.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.paygrid.dockerized.model.enums.Estado;

@Entity
@Data
public class Deuda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El número de documento es obligatorio.")
    @Column(unique = true)
    private String numeroDocumento;

    @NotBlank(message = "La empresa es obligatoria.")
    private String empresa;

    @NotNull(message = "El monto es obligatorio.")
    private BigDecimal monto;

    @NotNull(message = "La fecha de vencimiento es obligatoria.")
    private LocalDate fechaVencimiento;

    @Enumerated(EnumType.STRING)
    private Estado estado;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // Método para establecer el usuario
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}

