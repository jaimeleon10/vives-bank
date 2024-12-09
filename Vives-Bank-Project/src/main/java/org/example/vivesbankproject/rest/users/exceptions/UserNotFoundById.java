package org.example.vivesbankproject.rest.users.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción que indica que el usuario con el identificador proporcionado no fue encontrado en el sistema.
 * Esta excepción es lanzada cuando un intento de buscar un usuario con un identificador inexistente se realiza.
 *
 * @author Jaime León, Natalia González, Germán Fernández, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundById extends UserException {

    /**
     * Constructor para crear una excepción con un mensaje informativo indicando que el usuario no fue encontrado.
     *
     * @param id Identificador del usuario que no fue encontrado.
     */
    public UserNotFoundById(String id) {
        super("Usuario con id '" + id + "' no encontrado");
    }
}