package org.example.vivesbankproject.rest.movimientos.exceptions.movimientos;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción que se lanza cuando se intenta revocar una transferencia que no es revocable.
 * Devuelve un error HTTP 400 (BAD REQUEST) si el intento de revocación ocurre después de las 24 horas.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TransferenciaNoRevocableException extends MovimientosException {

    /**
     * Constructor para crear una excepción TransferenciaNoRevocableException con un mensaje específico
     * que indica el motivo de la excepción (intentó revocar una transferencia fuera del plazo permitido).
     *
     * @param guid Identificador único de la transferencia que intentó ser revocada.
     */
    public TransferenciaNoRevocableException(String guid) {
        super("La transferencia con guid '" + guid + "' no puede ser revocada, han pasado más de 24 horas");
    }
}