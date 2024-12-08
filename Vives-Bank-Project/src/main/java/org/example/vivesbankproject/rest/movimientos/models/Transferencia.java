package org.example.vivesbankproject.rest.movimientos.models;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * Clase que representa una transferencia financiera.
 * Contiene información esencial para realizar una transferencia entre cuentas.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Clase que representa la información para una transferencia financiera")
public class Transferencia {

    /**
     * IBAN de la cuenta de origen desde donde se envía la transferencia.
     */
    @NotBlank
    @Schema(description = "IBAN de la cuenta de origen de la transferencia", example = "ES9121000418450200051330930")
    private String iban_Origen;

    /**
     * IBAN de la cuenta destino donde se recibirá la transferencia.
     */
    @NotBlank
    @Schema(description = "IBAN de la cuenta destino de la transferencia", example = "ES9121000418450200051330947")
    private String iban_Destino;

    /**
     * Cantidad a transferir, debe estar en un rango entre 1 y 10,000.
     */
    @Min(value = 1, message = "La cantidad debe ser mayor a 1")
    @Max(value = 10000, message = "La cantidad debe ser menor a 10000")
    @Schema(description = "Cantidad a transferir en la operación", example = "2500")
    private BigDecimal cantidad;

    /**
     * Nombre del beneficiario que recibe la transferencia, limitado a 100 caracteres.
     */
    @NotBlank
    @Size(max = 100, message = "El nombre del beneficiario no puede tener más de 100 caracteres")
    @Schema(description = "Nombre del beneficiario que recibe la transferencia", example = "Juan Pérez")
    private String nombreBeneficiario;

    /**
     * Información adicional sobre el movimiento destino, opcional.
     */
    @Schema(description = "Información adicional sobre el movimiento destino", example = "Pago de servicios")
    private String movimientoDestino;
}