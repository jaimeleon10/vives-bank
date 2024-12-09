package org.example.vivesbankproject.rest.users.exceptions;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción que indica que el nombre de usuario ya existe en el sistema.
 * Esta excepción es lanzada cuando un intento de crear un usuario con un nombre de usuario ya existente se realiza.
 *
 * @author Jaime León, Natalia González, Germán Fernández, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserExists extends UserException {

    /**
     * Constructor para crear una excepción con un mensaje informativo indicando que el nombre de usuario ya existe.
     *
     * @param username Nombre de usuario que ya existe en el sistema.
     */
    public UserExists(String username) {
        super("El nombre de usuario '" + username + "' ya existe");
    }
}