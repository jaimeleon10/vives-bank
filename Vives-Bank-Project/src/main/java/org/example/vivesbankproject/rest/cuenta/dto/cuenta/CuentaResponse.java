package org.example.vivesbankproject.rest.cuenta.dto.cuenta;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Clase de respuesta para la entidad Cuenta.
 * Representa la información que se devuelve en las respuestas de la API al consultar una cuenta.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize
public class CuentaResponse implements Serializable {

    @Schema(description = "Identificador único de la cuenta", example = "123e4567-e89b-12d3-a456-426614174000", required = true)
    private String guid;

    @Schema(description = "IBAN de la cuenta", example = "ES9121000418450200051332", required = true)
    private String iban;

    @Schema(description = "Saldo de la cuenta", example = "1000.50", required = true)
    private String saldo;

    @Schema(description = "Identificador del tipo de cuenta", example = "123456789", required = true)
    private String tipoCuentaId;

    @Schema(description = "Identificador de la tarjeta asociada", example = "987654321", required = true)
    private String tarjetaId;

    @Schema(description = "Identificador del cliente al que pertenece la cuenta", example = "abc12345", required = true)
    private String clienteId;

    @Schema(description = "Fecha de creación de la cuenta", example = "2022-01-01T12:00:00Z", required = true)
    private String createdAt;

    @Schema(description = "Fecha de última actualización de la cuenta", example = "2023-12-01T15:30:00Z", required = true)
    private String updatedAt;

    @Schema(description = "Indica si la cuenta está marcada para borrado lógico", example = "false", required = true)
    private Boolean isDeleted;
}