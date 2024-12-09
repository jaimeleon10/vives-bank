package org.example.vivesbankproject.rest.auth.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción personalizada para manejar errores en caso de credenciales de inicio de sesión inválidas.
 * <p>
 * Esta excepción se lanza cuando un usuario intenta iniciar sesión con credenciales incorrectas
 * o inválidas en la aplicación.
 * </p>
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class AuthSingInInvalid extends AuthException {

    /**
     * Constructor de la excepción para credenciales de inicio de sesión inválidas.
     *
     * @param message Mensaje detallado que describe la razón por la que el inicio de sesión es inválido.
     */
    public AuthSingInInvalid(String message) {
        super(message);
    }
}