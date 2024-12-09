package org.example.vivesbankproject.rest.auth.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción personalizada para manejar errores cuando el nombre de usuario o el correo electrónico ya existen.
 * <p>
 * Esta excepción se lanza cuando un intento de registro falla porque el nombre de usuario o el correo electrónico ya están en uso.
 * </p>
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserAuthNameOrEmailExisten extends AuthException {

    /**
     * Constructor para la excepción de nombre de usuario o correo electrónico existentes.
     *
     * @param message Mensaje detallado que indica la razón por la que el nombre de usuario o correo electrónico no es válido para el registro.
     */
    public UserAuthNameOrEmailExisten(String message) {
        super(message);
    }
}
