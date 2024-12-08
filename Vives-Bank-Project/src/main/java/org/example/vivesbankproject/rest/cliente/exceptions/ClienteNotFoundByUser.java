package org.example.vivesbankproject.rest.cliente.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción personalizada para manejar el caso en el que un cliente no está asignado a un usuario específico.
 * Extiende la clase base ClienteExceptions.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ClienteNotFoundByUser extends ClienteExceptions {

    /**
     * Constructor para crear una instancia de ClienteNotFoundByUser con un mensaje específico.
     *
     * @param id El identificador del usuario que no tiene asignado un cliente
     */
    public ClienteNotFoundByUser(String id) {
        super("No existe ningún cliente asignado al usuario con id: " + id);
    }
}