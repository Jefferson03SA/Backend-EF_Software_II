package com.paygrid.dockerized.model.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DeudaRequestDTO {

    @NotNull
    private String numeroDocumento;

    @NotNull
    private String empresa;

    @NotNull
    @Positive(message = "El monto debe ser mayor que cero")
    private Double monto;

    @NotNull
    @FutureOrPresent(message = "La fecha de vencimiento debe ser hoy o en el futuro")
    private LocalDate fechaVencimiento;

}
