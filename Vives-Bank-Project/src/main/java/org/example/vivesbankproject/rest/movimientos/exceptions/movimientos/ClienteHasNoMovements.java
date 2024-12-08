package org.example.vivesbankproject.rest.movimientos.exceptions.movimientos;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción para manejar el caso en el que un cliente no tiene movimientos asociados.
 * Devuelve un error HTTP 404 (NOT FOUND) al ser lanzada.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ClienteHasNoMovements extends MovimientosException {

    /**
     * Constructor para la excepción ClienteHasNoMovements.
     *
     * @param clienteId El identificador del cliente que no tiene movimientos.
     */
    public ClienteHasNoMovements(String clienteId) {
        super("El cliente con Id " + clienteId + " no tiene movimientos");
    }
}