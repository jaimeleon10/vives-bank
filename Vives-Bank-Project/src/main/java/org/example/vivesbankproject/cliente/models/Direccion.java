package org.example.vivesbankproject.cliente.models;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class Direccion {
    @NotBlank(message = "La calle no puede estar vacia")
    private String calle;

    @NotBlank(message = "El número no puede estar vacio")
    private String numero;

    @NotBlank(message = "El código postal no puede estar vacio")
    @Pattern(regexp = "^\\d{5}$", message = "El codigo postal debe tener 5 numeros")
    private String codigoPostal;

    @NotBlank(message = "El piso no puede estar vacio")
    private String piso;

    @NotBlank(message = "La letra no puede estar vacia")
    private String letra;
}
