package org.example.vivesbankproject.rest.cliente.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción personalizada para manejar el caso en el que un usuario ya esté asignado a otro cliente.
 * Extiende la clase base ClienteExceptions.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ClienteUserAlreadyAssigned extends ClienteExceptions {

    /**
     * Constructor para crear una instancia de ClienteUserAlreadyAssigned con un mensaje específico.
     *
     * @param id El identificador del usuario que ya está asignado a otro cliente
     */
    public ClienteUserAlreadyAssigned(String id) {
        super("El usuario con el id " + id + " ya está asignado a otro cliente");
    }
}