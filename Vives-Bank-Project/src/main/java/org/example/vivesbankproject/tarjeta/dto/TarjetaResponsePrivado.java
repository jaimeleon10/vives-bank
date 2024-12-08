package org.example.vivesbankproject.tarjeta.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Representa los datos privados de una tarjeta bancaria.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos privados de una tarjeta bancaria")
public class TarjetaResponsePrivado implements Serializable {

    /**
     * Identificador único global de la tarjeta.
     */
    @Schema(description = "Identificador único global de la tarjeta", example = "123e4567-e89b-12d3-a456-426614174000")
    private String guid;

    /**
     * Código de verificación de valor (CVV) de la tarjeta.
     */
    @Schema(description = "Código de verificación de valor (CVV)", example = "123")
    private String cvv;

    /**
     * Número de identificación personal (PIN) de la tarjeta.
     */
    @Schema(description = "Número de identificación personal (PIN)", example = "1234")
    private String pin;
}