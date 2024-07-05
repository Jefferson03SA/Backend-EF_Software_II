package com.paygrid.dockerized.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UsuarioRegistroRequestDTO {
    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 5, max = 15, message = "El nombre de usuario debe tener entre 5 y 15 caracteres")
    private String nombreUsuario;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo debe ser válido")
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, max = 15, message = "La contraseña debe tener entre 6 y 15 caracteres")
    private String contraseña;
}
