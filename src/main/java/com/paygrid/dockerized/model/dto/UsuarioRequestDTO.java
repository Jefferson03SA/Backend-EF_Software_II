package com.paygrid.dockerized.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UsuarioRequestDTO {

    @Email(message = "El correo electrónico debe ser válido.")
    @NotBlank(message = "El correo electrónico es obligatorio.")
    private String email;

    @NotBlank(message = "El nombre de usuario es obligatorio.")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria.")
    private String password;
}

