package org.example.vivesbankproject.rest.tarjeta.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

/**
 * Representa la carga útil de solicitud para actualizar los detalles de una tarjeta.
 *
 * Esta clase contiene restricciones de validación para actualizar información
 * relacionada con la tarjeta, como PIN, límites diarios, semanales y mensuales,
 * y estado de borrado lógico.
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Data
@Builder
@Schema(description = "Carga útil de solicitud para actualizar detalles de tarjeta")
public class TarjetaRequestUpdate {

    /**
     * Número de Identificación Personal (PIN) de la tarjeta.
     *
     * @apiNote Debe ser exactamente 4 dígitos
     */
    @NotBlank(message = "El PIN no puede estar vacío")
    @Pattern(regexp = "^[0-9]{4}$", message = "El PIN debe ser un número de 4 dígitos")
    @Schema(description = "PIN de la tarjeta", example = "1234", pattern = "^[0-9]{4}$", required = true)
    private String pin;

    /**
     * Límite de transacción diario para la tarjeta.
     *
     * @apiNote Debe ser un número positivo
     */
    @Positive(message = "El límite diario debe ser un número positivo")
    @Schema(description = "Límite de transacción diario", example = "1000.00", minimum = "0")
    private BigDecimal limiteDiario;

    /**
     * Límite de transacción semanal para la tarjeta.
     *
     * @apiNote Debe ser un número positivo
     */
    @Positive(message = "El límite semanal debe ser un número positivo")
    @Schema(description = "Límite de transacción semanal", example = "5000.00", minimum = "0")
    private BigDecimal limiteSemanal;

    /**
     * Límite de transacción mensual para la tarjeta.
     *
     * @apiNote Debe ser un número positivo
     */
    @Positive(message = "El límite mensual debe ser un número positivo")
    @Schema(description = "Límite de transacción mensual", example = "20000.00", minimum = "0")
    private BigDecimal limiteMensual;

    /**
     * Estado de borrado lógico de la tarjeta.
     *
     * @apiNote Por defecto es falso si no se especifica
     */
    @NotNull(message = "El campo de borrado lógico no puede ser nulo")
    @Builder.Default
    @Schema(description = "Estado de borrado lógico", example = "false", defaultValue = "false")
    private Boolean isDeleted = false;
}