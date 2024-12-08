package org.example.vivesbankproject.rest.users.exceptions;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Clase base para todas las excepciones relacionadas con el usuario en el sistema.
 * Proporciona un mensaje de error personalizado para situaciones excepcionales.
 *
 * @author Jaime León, Natalia González, Germán Fernández, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
public abstract class UserException extends RuntimeException {

    /**
     * Constructor para crear una excepción de usuario con un mensaje específico.
     *
     * @param message Mensaje informativo sobre la excepción.
     */
    public UserException(String message) {
        super(message);
    }
}