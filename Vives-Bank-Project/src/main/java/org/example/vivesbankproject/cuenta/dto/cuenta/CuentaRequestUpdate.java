package org.example.vivesbankproject.cuenta.dto.cuenta;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "El campo del tipo de cuenta debe contener un id de tipo de cuenta")
    @Builder.Default
    private String tipoCuentaId = "";

    @NotNull(message = "El campo tarjeta debe contener un id de tarjeta")
    @Builder.Default
    private String tarjetaId = "";

    @NotNull(message = "El campo cliente debe contener un id de cliente")
    @Builder.Default
    private String clienteId = "";

    @NotNull(message = "El campo de borrado lógico no puede ser nulo")
    @Builder.Default
    private Boolean isDeleted = false;
}