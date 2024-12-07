package org.example.vivesbankproject.security.auth.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción base para manejar errores de autenticación en la aplicación.
 * <p>
 * Esta clase es la clase padre de todas las excepciones relacionadas con autenticación,
 * como fallos en el inicio de sesión, registro, permisos insuficientes, etc.
 * </p>
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public abstract class AuthException extends RuntimeException {

    /**
     * Constructor de la excepción AuthException.
     *
     * @param message Mensaje detallado que describe el error.
     */
    public AuthException(String message) {
        super(message);
    }
}