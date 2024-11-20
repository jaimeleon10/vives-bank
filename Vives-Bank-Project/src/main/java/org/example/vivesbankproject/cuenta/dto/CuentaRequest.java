package org.example.vivesbankproject.cuenta.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.vivesbankproject.cliente.models.Cliente;
import org.example.vivesbankproject.cuenta.models.TipoCuenta;
import org.example.vivesbankproject.tarjeta.models.Tarjeta;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CuentaRequest {
    @NotBlank(message = "El numero de cuenta (IBAN) no puede estar vacio")
    private String iban;

    @Digits(integer = 8, fraction = 2, message = "El saldo debe ser un numero valido con hasta dos decimales")
    @PositiveOrZero(message = "El saldo no puede ser negativo")
    private Double saldo;

    @NotBlank(message = "El campo del tipo de cuenta no puede estar vacio")
    private TipoCuenta tipoCuenta;

    @NotBlank(message = "El campo tarjeta no puede estar vacio")
    private Tarjeta tarjeta;

    private Boolean isDeleted;
}