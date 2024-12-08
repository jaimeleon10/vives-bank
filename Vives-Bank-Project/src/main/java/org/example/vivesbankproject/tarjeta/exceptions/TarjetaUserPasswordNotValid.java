package org.example.vivesbankproject.tarjeta.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción lanzada cuando la contraseña proporcionada no es válida.
 * Devuelve un estado HTTP 404 (Not Found) cuando se produce.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class TarjetaUserPasswordNotValid extends TarjetaException {
    /**
     * Constructor que crea una excepción con un mensaje estándar de contraseña incorrecta.
     */
    public TarjetaUserPasswordNotValid() {
        super("La contraseña introducida no es correcta");
    }
}