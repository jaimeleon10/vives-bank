package org.example.vivesbankproject.cuenta.dto.cuenta;

import io.swagger.v3.oas.annotations.media.Schema;
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

/**
 * Clase de solicitud para la actualización de una cuenta.
 * Contiene validaciones y documentación para cada campo.
 * Se utiliza en operaciones de actualización de datos en la base de datos.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CuentaRequestUpdate {

    @Schema(description = "Saldo de la cuenta (número válido con hasta dos decimales)", example = "1000.50", required = false)
    @Digits(integer = 8, fraction = 2, message = "El saldo debe ser un número válido con hasta dos decimales")
    @PositiveOrZero(message = "El saldo no puede ser negativo")
    private BigDecimal saldo;

    @Schema(description = "Identificador del tipo de cuenta", example = "123456789", required = true)
    @NotNull(message = "El campo del tipo de cuenta debe contener un id de tipo de cuenta")
    @Builder.Default
    private String tipoCuentaId = "";

    @Schema(description = "Identificador de la tarjeta asociada", example = "987654321", required = true)
    @NotNull(message = "El campo tarjeta debe contener un id de tarjeta")
    @Builder.Default
    private String tarjetaId = "";

    @Schema(description = "Identificador del cliente al que pertenece la cuenta", example = "abc12345", required = true)
    @NotNull(message = "El campo cliente debe contener un id de cliente")
    @Builder.Default
    private String clienteId = "";

    @Schema(description = "Indica si la cuenta está marcada para borrado lógico", example = "false", required = true)
    @NotNull(message = "El campo de borrado lógico no puede ser nulo")
    @Builder.Default
    private Boolean isDeleted = false;
}