package org.example.vivesbankproject.cuenta.dto.tipoCuenta;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Clase de respuesta para el catálogo de tipos de cuentas devueltos por la API.
 * Contiene información relevante sobre el nombre y el interés de los tipos de cuenta.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize
public class TipoCuentaResponseCatalogo implements Serializable {

    @Schema(description = "Nombre del tipo de cuenta", example = "Cuenta Corriente", required = true)
    private String nombre;

    @Schema(description = "Porcentaje de interés asociado al tipo de cuenta", example = "2.5", required = true)
    private String interes;
}