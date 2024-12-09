package org.example.vivesbankproject.rest.movimientos.exceptions.movimientos;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Clase base para todas las excepciones relacionadas con los movimientos.
 * Devuelve un error HTTP 400 (BAD REQUEST) al ser lanzada.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public abstract class MovimientosException extends RuntimeException {

    /**
     * Constructor para crear una excepción MovimientosException con un mensaje específico.
     *
     * @param message El mensaje detallado de la excepción.
     */
    public MovimientosException(String message) {
        super(message);
    }
}