package org.example.vivesbankproject.rest.cliente.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción personalizada para manejar el caso en el que un cliente no se pudo eliminar.

 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ClienteNotDeleted extends ClienteExceptions {

    /**
     * Constructor para crear una instancia de ClienteNotDeleted con un mensaje específico.
     *
     * @param id El identificador único del cliente que no se pudo borrar
     */
    public ClienteNotDeleted(String id) {
        super("Cliente con guid '" + id + "' no se pudo borrar");
    }
}