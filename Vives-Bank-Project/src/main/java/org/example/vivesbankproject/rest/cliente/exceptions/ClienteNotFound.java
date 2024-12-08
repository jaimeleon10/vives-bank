package org.example.vivesbankproject.rest.cliente.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción personalizada para manejar el caso en el que un cliente no se encuentra en la base de datos.
 * Extiende la clase base ClienteExceptions.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ClienteNotFound extends ClienteExceptions {

    /**
     * Constructor para crear una instancia de ClienteNotFound con un mensaje específico.
     *
     * @param id El identificador único del cliente que no se encontró
     */
    public ClienteNotFound(String id) {
        super("Cliente con id '" + id + "' no encontrado");
    }
}