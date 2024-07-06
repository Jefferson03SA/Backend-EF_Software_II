package com.paygrid.dockerized.model.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DeudaRequestDTO {
    @NotBlank(message = "El número de documento es obligatorio.")
    private String numeroDocumento;

    @NotBlank(message = "La empresa es obligatoria.")
    private String empresa;

    @NotNull(message = "El monto es obligatorio.")
    private BigDecimal monto;

    @NotNull(message = "La fecha de vencimiento es obligatoria.")
    private LocalDate fechaVencimiento;
}
