package org.example.vivesbankproject.cuenta.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.vivesbankproject.cuenta.models.TipoCuenta;
import org.example.vivesbankproject.tarjeta.models.Tarjeta;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CuentaRequestUpdate {
    @Digits(integer = 8, fraction = 2, message = "El saldo debe ser un numero valido con hasta dos decimales")
    @PositiveOrZero(message = "El saldo no puede ser negativo")
    private BigDecimal saldo;

    @NotBlank(message = "El campo del tipo de cuenta no puede estar vacio")
    private TipoCuenta tipoCuenta;

    @NotBlank(message = "El campo tarjeta no puede estar vacio")
    private Tarjeta tarjeta;

    private Boolean isDeleted;
}