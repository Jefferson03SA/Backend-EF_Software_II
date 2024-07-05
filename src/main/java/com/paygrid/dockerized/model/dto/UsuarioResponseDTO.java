package com.paygrid.dockerized.model.dto;

import lombok.Data;

@Data
public class UsuarioResponseDTO {
    private Long id;
    private String nombreUsuario;
    private String correo;
}