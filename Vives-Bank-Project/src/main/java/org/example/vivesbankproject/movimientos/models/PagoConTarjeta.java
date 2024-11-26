package org.example.vivesbankproject.movimientos.models;

import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PagoConTarjeta {
    @Pattern(regexp = "^\\d{16}$", message = "El número de tarjeta debe tener 16 dígitos")
    private String numeroTarjeta;

    @Size(max = 100, message = "El nombre del comercio no puede tener más de 100 caracteres")
    private String nombreComercio;

    @Pattern(regexp = "^\\d{3}$", message = "El CVV debe tener 3 dígitos")
    private String cvv;
}