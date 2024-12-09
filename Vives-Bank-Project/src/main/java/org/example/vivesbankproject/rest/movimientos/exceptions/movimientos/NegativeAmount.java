package org.example.vivesbankproject.rest.movimientos.exceptions.movimientos;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.math.BigDecimal;

/**
 * Excepción que representa un error cuando la cantidad del movimiento es negativa.
 * Devuelve un error HTTP 400 (BAD REQUEST) al ser lanzada.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NegativeAmount extends MovimientosException {

    /**
     * Constructor para crear una excepción NegativeAmount con un mensaje detallado que indica el monto negativo.
     *
     * @param message La cantidad negativa que causó la excepción.
     */
    public NegativeAmount(BigDecimal message) {
        super("La cantidad del movimiento no puede ser negativa: " + message);
    }
}