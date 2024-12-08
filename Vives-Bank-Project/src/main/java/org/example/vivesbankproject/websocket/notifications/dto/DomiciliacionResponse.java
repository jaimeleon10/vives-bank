package org.example.vivesbankproject.websocket.notifications.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.math.BigDecimal;


/**
 * Clase que representa la respuesta de una operación de domiciliación bancaria.
 *
 * <p>
 * Esta clase se utiliza para estructurar la información de la respuesta generada al procesar una
 * solicitud relacionada con una domiciliación bancaria, incluyendo detalles como el IBAN de origen y destino,
 * la cantidad de la transacción, la información del acreedor y detalles de la última ejecución.
 * </p>
 *   @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 *   @version 1.0-SNAPSHOT
 */
@Tag(name = "DomiciliacionResponse", description = "Clase de respuesta para la información de domiciliaciones bancarias")
public record DomiciliacionResponse(

        /** Identificador único de la operación de domiciliación */
        @Schema(description = "Identificador único para la operación de domiciliación", example = "123e4567-e89b-12d3-a456-426614174000")
        String guid,

        /** Código IBAN del banco origen en la transacción */
        @Schema(description = "Código IBAN de origen", example = "ES91210004184502000513393")
        String ibanOrigen,

        /** Código IBAN del banco destino en la transacción */
        @Schema(description = "Código IBAN de destino", example = "DE89370400440532013000")
        String ibanDestino,

        /** Cantidad monetaria implicada en la transacción */
        @Schema(description = "Cantidad monetaria de la transacción", example = "1500.50")
        BigDecimal cantidad,

        /** Nombre del acreedor involucrado en la operación */
        @Schema(description = "Nombre del acreedor en la transacción", example = "Banco Nacional")
        String nombreAcreedor,

        /** Fecha de inicio de la operación de domiciliación */
        @Schema(description = "Fecha de inicio de la domiciliación", example = "2023-01-01")
        String fechaInicio,

        /** Periodicidad de la domiciliación (e.g., mensual, anual) */
        @Schema(description = "Periodicidad de la domiciliación bancaria", example = "Mensual")
        String periodicidad,

        /** Estado actual de la domiciliación (activa/inactiva) */
        @Schema(description = "Indica si la domiciliación está activa", example = "true")
        boolean activa,

        /** Fecha de la última ejecución realizada */
        @Schema(description = "Fecha de la última ejecución de la domiciliación", example = "2023-12-01")
        String ultimaEjecucion
) {}