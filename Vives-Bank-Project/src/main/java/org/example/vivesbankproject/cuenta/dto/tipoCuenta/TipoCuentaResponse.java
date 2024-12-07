package org.example.vivesbankproject.cuenta.dto.tipoCuenta;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
/**
 * Clase de respuesta que representa la información de un tipo de cuenta devuelta por la API.
 * Contiene los atributos clave relacionados con un tipo de cuenta.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize
public class TipoCuentaResponse implements Serializable {

    @Schema(description = "Identificador único de la respuesta", example = "12345", required = true)
    private String guid;

    @Schema(description = "Nombre del tipo de cuenta", example = "Cuenta Ahorro", required = true)
    private String nombre;

    @Schema(description = "Porcentaje de interés para el tipo de cuenta", example = "3.25", required = true)
    private String interes;

    @Schema(description = "Fecha de creación del tipo de cuenta en formato ISO 8601", example = "2022-01-15T12:00:00Z")
    private String createdAt;

    @Schema(description = "Fecha de última actualización del tipo de cuenta en formato ISO 8601", example = "2023-05-01T12:00:00Z")
    private String updatedAt;

    @Schema(description = "Indica si el tipo de cuenta está marcado como borrado lógico", example = "false")
    private Boolean isDeleted;
}