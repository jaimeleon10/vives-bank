package org.example.vivesbankproject.rest.auth.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción personalizada para manejar errores de contraseñas diferentes durante el proceso de registro.
 * <p>
 * Esta excepción se lanza cuando las contraseñas ingresadas por el usuario no coinciden en el proceso de validación.
 * </p>
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserDiferentePasswords extends RuntimeException {

    /**
     * Constructor para la excepción de contraseñas diferentes.
     *
     * @param message Mensaje detallado que indica la razón por la que las contraseñas no coinciden.
     */
    public UserDiferentePasswords(String message) {
        super(message);
    }
}