package org.example.vivesbankproject.cliente.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción personalizada para manejar el caso en el que un cliente ya existe en el sistema

 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ClienteExistsByEmail extends ClienteExceptions {

    /**
     * Constructor para crear una instancia de ClienteExistsByEmail con un mensaje específico.
     *
     * @param email El correo electrónico que ya existe en el sistema
     */
    public ClienteExistsByEmail(String email) {
        super("Cliente con email '" + email + "' ya existente");
    }
}
