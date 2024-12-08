package org.example.vivesbankproject.rest.movimientos.models;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Enum que representa las opciones de periodicidad para una domiciliación o movimiento recurrente.
 * Contempla las opciones más comunes para definir el intervalo de ejecución de una operación.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Schema(description = "Enum para representar las opciones de periodicidad en operaciones financieras")
public enum Periodicidad {

    /**
     * Representa la ejecución diaria.
     */
    @Schema(description = "Ejecutar de manera diaria")
    DIARIA,

    /**
     * Representa la ejecución mensual.
     */
    @Schema(description = "Ejecutar de manera mensual")
    MENSUAL,

    /**
     * Representa la ejecución semanal.
     */
    @Schema(description = "Ejecutar de manera semanal")
    SEMANAL,

    /**
     * Representa la ejecución anual.
     */
    @Schema(description = "Ejecutar de manera anual")
    ANUAL
}