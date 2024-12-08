package org.example.vivesbankproject.rest.cuenta.dto.tipoCuenta;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Clase de solicitud para la creación y actualización de un tipo de cuenta.
 * Representa los datos necesarios para crear un nuevo tipo de cuenta en la API.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TipoCuentaRequest {

    @Schema(description = "Nombre del tipo de cuenta", example = "Cuenta Ahorro", required = true)
    @NotBlank(message = "El nombre del tipo de cuenta no puede estar vacío")
    private String nombre;

    @Schema(description = "Porcentaje de interés para el tipo de cuenta", example = "3.25", required = true)
    @Digits(integer = 3, fraction = 2, message = "El interés debe ser un número válido")
    @PositiveOrZero(message = "El interés no puede ser negativo")
    private BigDecimal interes;
}