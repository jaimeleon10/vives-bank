package org.example.vivesbankproject.rest.users.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción que indica que el usuario con el nombre de usuario proporcionado no fue encontrado en el sistema.
 * Esta excepción es lanzada cuando un intento de buscar un usuario con un nombre de usuario inexistente es realizado.
 *
 * @author Jaime León, Natalia González, Germán Fernández, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends UserException {

    /**
     * Constructor para crear una excepción con un mensaje informativo indicando que el usuario no fue encontrado.
     *
     * @param username Nombre de usuario que no fue encontrado.
     */
    public UserNotFoundException(String username) {
        super("Usuario con username '" + username + "' no encontrado");
    }
}