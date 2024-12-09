package org.example.vivesbankproject.websocket.notifications.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.math.BigDecimal;

/**
 * Respuesta para la operación de transferencia.
 *
 * <p>
 * Esta clase DTO se utiliza para representar la información devuelta después de realizar una transferencia.
 * Contiene detalles sobre el origen, destino, cantidad y el nombre del beneficiario.
 * </p>
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Tag(name = "TransferenciaResponse", description = "Clase DTO para la respuesta de una transferencia bancaria")
public record TransferenciaResponse(

        /** IBAN de la cuenta de origen de la transferencia */
        @Schema(description = "IBAN de la cuenta de origen de la transferencia", example = "ES9121000418450200051331")
        String ibanOrigen,

        /** IBAN de la cuenta de destino de la transferencia */
        @Schema(description = "IBAN de la cuenta de destino de la transferencia", example = "ES9121000418450200051332")
        String ibanDestino,

        /** Cantidad transferida en la operación */
        @Schema(description = "Cantidad transferida en la operación", example = "2500.75")
        BigDecimal cantidad,

        /** Nombre del beneficiario de la transferencia */
        @Schema(description = "Nombre del beneficiario de la transferencia", example = "Juan Pérez")
        String nombreBeneficiario
) {}