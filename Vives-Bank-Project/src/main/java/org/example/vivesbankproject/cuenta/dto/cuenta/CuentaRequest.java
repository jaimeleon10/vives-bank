package org.example.vivesbankproject.cuenta.dto.cuenta;

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
    @NotNull(message = "El campo del tipo de cuenta no puede estar vacio")
    private TipoCuenta tipoCuenta;

    @NotNull(message = "El campo tarjeta no puede estar vacio")
    private Tarjeta tarjeta;
}