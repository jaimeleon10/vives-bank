package org.example.vivesbankproject.cuenta.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.vivesbankproject.cliente.models.Cliente;
import org.example.vivesbankproject.cuenta.models.TipoCuenta;
import org.example.vivesbankproject.tarjeta.models.Tarjeta;

import java.math.BigDecimal;

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