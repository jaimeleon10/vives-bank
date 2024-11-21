package org.example.vivesbankproject.movimientos.models;

import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@JsonTypeName("PAGO_CON_TARJETA")
@AllArgsConstructor
@NoArgsConstructor
public class PagoConTarjeta extends Transacciones {
    @Pattern(regexp = "^\\d{16}$", message = "El número de tarjeta debe tener 16 dígitos")
    private String numeroTarjeta;

    @Size(max = 100, message = "El nombre del comercio no puede tener más de 100 caracteres")
    private String nombreComercio;

    @Pattern(regexp = "^\\d{3}$", message = "El CVV debe tener 3 dígitos")
    private String cvv;
}