package com.paygrid.dockerized.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email(message = "El correo electrónico debe ser válido.")
    @NotBlank(message = "El correo electrónico es obligatorio.")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "El nombre de usuario es obligatorio.")
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank(message = "La contraseña es obligatoria.")
    private String password;
}
