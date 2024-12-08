package org.example.vivesbankproject.tarjeta.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.vivesbankproject.tarjeta.models.TipoTarjeta;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Representa la respuesta de una tarjeta bancaria con sus detalles principales.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize
@Schema(description = "Detalles de respuesta de una tarjeta bancaria")
public class TarjetaResponse implements Serializable {

    /**
     * Identificador único global de la tarjeta.
     */
    @Schema(description = "Identificador único global de la tarjeta", example = "123e4567-e89b-12d3-a456-426614174000")
    private String guid;

    /**
     * Número de la tarjeta.
     */
    @Schema(description = "Número de la tarjeta", example = "4111111111111111")
    private String numeroTarjeta;

    /**
     * Fecha de caducidad de la tarjeta.
     */
    @Schema(description = "Fecha de caducidad de la tarjeta", example = "12/25")
    private String fechaCaducidad;

    /**
     * Límite diario de la tarjeta.
     */
    @Schema(description = "Límite de gasto diario", example = "1000.00")
    private String limiteDiario;

    /**
     * Límite semanal de la tarjeta.
     */
    @Schema(description = "Límite de gasto semanal", example = "5000.00")
    private String limiteSemanal;

    /**
     * Límite mensual de la tarjeta.
     */
    @Schema(description = "Límite de gasto mensual", example = "20000.00")
    private String limiteMensual;

    /**
     * Tipo de tarjeta.
     */
    @Schema(description = "Tipo de tarjeta")
    private TipoTarjeta tipoTarjeta;

    /**
     * Fecha de creación de la tarjeta.
     */
    @Schema(description = "Fecha de creación de la tarjeta", example = "2024-01-15T10:30:00")
    private String createdAt;

    /**
     * Fecha de última actualización de la tarjeta.
     */
    @Schema(description = "Fecha de última actualización de la tarjeta", example = "2024-01-15T10:30:00")
    private String updatedAt;

    /**
     * Indica si la tarjeta ha sido eliminada.
     */
    @Schema(description = "Indica si la tarjeta ha sido eliminada", example = "false")
    public Boolean isDeleted;
}