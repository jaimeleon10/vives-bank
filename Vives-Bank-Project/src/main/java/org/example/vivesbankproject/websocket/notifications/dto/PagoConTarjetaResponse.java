package org.example.vivesbankproject.websocket.notifications.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Respuesta para el procesamiento de pagos con tarjeta.
 *
 * <p>
 * Esta clase DTO se utiliza para representar la información devuelta después de realizar un pago
 * con tarjeta. Contiene detalles sobre el número de tarjeta, la cantidad pagada y el nombre del comercio.
 * </p>
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Tag(name = "PagoConTarjetaResponse", description = "Clase DTO para la respuesta de pagos con tarjeta")
public record PagoConTarjetaResponse(

        /** Número de tarjeta utilizado en la transacción */
        @Schema(description = "Número de tarjeta utilizada en la transacción", example = "1234 5678 9012 3456")
        String numeroTarjeta,

        /** Cantidad pagada en la transacción con tarjeta */
        @Schema(description = "Cantidad pagada en la transacción con tarjeta", example = "150.00")
        Double cantidad,

        /** Nombre del comercio que procesó el pago */
        @Schema(description = "Nombre del comercio asociado al pago", example = "Supermercado ABC")
        String nombreComercio
) {}