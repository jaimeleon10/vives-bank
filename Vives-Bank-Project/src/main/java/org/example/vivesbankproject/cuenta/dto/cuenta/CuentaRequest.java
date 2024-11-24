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
    @NotBlank(message = "El campo tipo de cuenta no puede estar vacío")
    private String tipoCuentaId;

    @NotBlank(message = "El campo tarjeta no puede estar vacío")
    private String tarjetaId;

    @NotBlank(message = "El campo cliente no puede estar vacío")
    private String clienteId;
}