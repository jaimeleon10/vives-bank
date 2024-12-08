package org.example.vivesbankproject.rest.cliente.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción personalizada para manejar el caso en el que un cliente no se encuentra en la base de datos por su DNI.
 * Extiende la clase base ClienteExceptions.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ClienteNotFoundByDni extends ClienteExceptions {

    /**
     * Constructor para crear una instancia de ClienteNotFoundByDni con un mensaje específico.
     *
     * @param dni El DNI del cliente que no se encontró
     */
    public ClienteNotFoundByDni(String dni) {
        super("Cliente con dni '" + dni + "' no encontrado");
    }
}