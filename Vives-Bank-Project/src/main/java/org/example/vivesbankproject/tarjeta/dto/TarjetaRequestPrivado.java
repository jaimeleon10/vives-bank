package org.example.vivesbankproject.tarjeta.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TarjetaRequestPrivado {
    @NotBlank(message = "El usuario no puede estar vacío")
    private String username;

    @NotBlank(message = "La contraseña no puede estar vacía")
    private String userPass;
}
