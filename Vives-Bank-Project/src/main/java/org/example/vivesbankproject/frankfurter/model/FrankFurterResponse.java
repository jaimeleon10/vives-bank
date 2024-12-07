package org.example.vivesbankproject.frankfurter.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

/**
 * Representa la respuesta obtenida del servicio Frankfurter con información sobre las tasas de cambio.
 * Contiene la cantidad, moneda base, fecha y el mapa de tasas de cambio por cada moneda objetivo.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Data
public class FrankFurterResponse {

    /**
     * Cantidad para la que se calculó la tasa de cambio.
     */
    @Schema(description = "Cantidad para la que se calculó la tasa de cambio", example = "1")
    private String amount;

    /**
     * Moneda base utilizada en la conversión.
     */
    @Schema(description = "Moneda base utilizada en la conversión", example = "EUR")
    private String base;

    /**
     * Fecha en la que se realizó la consulta de tasas de cambio.
     */
    @Schema(description = "Fecha en la que se consultaron las tasas de cambio", example = "2023-12-07")
    private String date;

    /**
     * Mapa que contiene las tasas de cambio por cada moneda objetivo.
     * Claves son los códigos de moneda y valores son las tasas de cambio.
     */
    @JsonProperty("rates")
    @Schema(description = "Mapa de tasas de cambio por moneda", example = "{ \"USD\": 1.1, \"GBP\": 0.85 }")
    private Map<String, Double> exchangeRates;

    // Getters y Setters generados automáticamente por Lombok con @Data
}