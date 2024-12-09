package org.example.vivesbankproject.websocket.notifications.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Clase que representa la respuesta para una operación de ingreso de nómina.
 *
 * <p>
 * Esta clase contiene la información relevante generada al procesar un ingreso de nómina,
 * incluyendo detalles como los IBAN de origen y destino, el monto, el nombre de la empresa y el CIF.
 * </p>
 *   @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 *   @version 1.0-SNAPSHOT
 */
@Tag(name = "IngresoNominaResponse", description = "Clase de respuesta para operaciones de ingreso de nómina")
public record IngresoNominaResponse(

        /** Código IBAN del banco origen en el ingreso de nómina */
        @Schema(description = "Código IBAN de origen en el ingreso de nómina", example = "ES91210004184502000513393")
        String ibanOrigen,

        /** Código IBAN del banco destino en el ingreso de nómina */
        @Schema(description = "Código IBAN de destino en el ingreso de nómina", example = "DE89370400440532013000")
        String ibanDestino,

        /** Cantidad monetaria de la nómina ingresada */
        @Schema(description = "Cantidad monetaria correspondiente al ingreso de nómina", example = "2500.75")
        Double cantidad,

        /** Nombre de la empresa que ejecuta el pago de la nómina */
        @Schema(description = "Nombre de la empresa que realiza el ingreso", example = "Tech Solutions S.A.")
        String nombreEmpresa,

        /** Código de identificación fiscal (CIF) de la empresa */
        @Schema(description = "Código de identificación fiscal de la empresa", example = "A12345678")
        String cifEmpresa
) {}
