package org.example.vivesbankproject.cuenta.dto.cuenta;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.vivesbankproject.cuenta.models.TipoCuenta;
import org.example.vivesbankproject.tarjeta.models.Tarjeta;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CuentaRequest {
    @NotBlank(message = "El campo del tipo de cuenta no puede estar vacio")
    private String tipoCuentaId;

    @NotBlank(message = "El campo tarjeta no puede estar vacio")
    private String tarjetaId;
}