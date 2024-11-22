package org.example.vivesbankproject.cuenta.dto.tipoCuenta;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TipoCuentaRequest {
    @NotBlank(message = "El nombre del tipo de cuenta no puede estar vacío")
    private String nombre;

    @Digits(integer = 3, fraction = 2, message = "El interés debe ser un número válido")
    @PositiveOrZero(message = "El interés no puede ser negativo")
    private BigDecimal interes;
}
