package org.example.vivesbankproject.rest.tarjeta.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import org.example.vivesbankproject.rest.tarjeta.models.TipoTarjeta;

import java.math.BigDecimal;

/**
 * Clase DTO para solicitar el guardado de una nueva tarjeta.
 * Contiene la información necesaria para crear una tarjeta bancaria.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Data
@Builder
@Schema(description = "Solicitud de creación de una nueva tarjeta bancaria")
public class TarjetaRequestSave {

    /**
     * PIN de la tarjeta.
     * Debe ser un número de exactamente 4 dígitos.
     */
    @NotBlank(message = "El PIN debe ser un numero de 4 digitos")
    @Pattern(regexp = "^[0-9]{4}$", message = "El PIN debe ser un numero de 4 digitos")
    @Schema(description = "PIN de la tarjeta bancaria", example = "1234", pattern = "^[0-9]{4}$", required = true)
    private String pin;

    /**
     * Límite diario de gastos para la tarjeta.
     * Debe ser un número positivo.
     */
    @Positive(message = "El limite diario debe ser un numero positivo")
    @Schema(description = "Límite diario de gastos", example = "500.00", minimum = "0")
    private BigDecimal limiteDiario;

    /**
     * Límite semanal de gastos para la tarjeta.
     * Debe ser un número positivo.
     */
    @Positive(message = "El limite semanal debe ser un numero positivo")
    @Schema(description = "Límite semanal de gastos", example = "2000.00", minimum = "0" )
    private BigDecimal limiteSemanal;

    /**
     * Límite mensual de gastos para la tarjeta.
     * Debe ser un número positivo.
     */
    @Positive(message = "El limite mensual debe ser un numero positivo")
    @Schema(description = "Límite mensual de gastos", example = "5000.00", minimum = "0")
    private BigDecimal limiteMensual;

    /**
     * Tipo de tarjeta.
     * No puede ser nulo y debe ser un valor del enum TipoTarjeta.
     */
    @NotNull(message = "El tipo de tarjeta no puede ser un campo nulo")
    @Schema(description = "Tipo de tarjeta bancaria", example = "CREDITO", required = true)
    private TipoTarjeta tipoTarjeta;
}
