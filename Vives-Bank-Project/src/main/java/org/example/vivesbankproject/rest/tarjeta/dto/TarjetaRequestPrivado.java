package org.example.vivesbankproject.rest.tarjeta.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TarjetaRequestPrivado {
    @NotBlank(message = "El usuario no puede estar vacio")
    private String username;

    @NotBlank(message = "La contraseña no puede estar vacia")
    private String userPass;
}
