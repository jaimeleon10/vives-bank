package org.example.vivesbankproject.rest.cuenta.dto.cuenta;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase de solicitud para la creación de una nueva cuenta.
 * Contiene validaciones y documentación para los campos.
 * Se utiliza en operaciones de creación de cuentas en la base de datos.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CuentaRequest {

    @Schema(description = "Identificador del tipo de cuenta", example = "123456789", required = true)
    @NotBlank(message = "El campo tipo de cuenta no puede estar vacío")
    private String tipoCuentaId;

    @Schema(description = "Identificador de la tarjeta asociada", example = "987654321", required = true)
    @NotBlank(message = "El campo tarjeta no puede estar vacío")
    private String tarjetaId;

    @Schema(description = "Identificador del cliente al que pertenece la cuenta", example = "abc12345", required = true)
    @NotBlank(message = "El campo cliente no puede estar vacío")
    private String clienteId;
}