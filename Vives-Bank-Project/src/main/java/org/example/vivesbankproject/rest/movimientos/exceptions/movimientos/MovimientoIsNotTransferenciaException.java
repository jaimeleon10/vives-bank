package org.example.vivesbankproject.rest.movimientos.exceptions.movimientos;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción para manejar el caso en el que un movimiento no es una transferencia válida.
 * Devuelve un error HTTP 400 (BAD REQUEST) al ser lanzada.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MovimientoIsNotTransferenciaException extends MovimientosException {

    /**
     * Constructor para la excepción MovimientoIsNotTransferenciaException.
     *
     * @param guid El identificador único del movimiento que no es una transferencia.
     */
    public MovimientoIsNotTransferenciaException(String guid) {
        super("El movimiento con guid '" + guid + "' no es una transferencia");
    }
}