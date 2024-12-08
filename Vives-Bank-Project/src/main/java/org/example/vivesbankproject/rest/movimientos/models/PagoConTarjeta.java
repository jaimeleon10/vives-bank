package org.example.vivesbankproject.rest.movimientos.models;

import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PagoConTarjeta {

    @Pattern(regexp = "^\\d{16}$", message = "El número de tarjeta debe tener 16 dígitos")
    @NotBlank
    private String numeroTarjeta;

    @Min(value = 1, message = "La cantidad debe ser mayor a 1")
    @Max(value = 10000, message = "La cantidad debe ser menor a 10000")
    private Double cantidad;

    @Size(max = 100, message = "El nombre del comercio no puede tener más de 100 caracteres")
    @NotBlank
    private String nombreComercio;

}