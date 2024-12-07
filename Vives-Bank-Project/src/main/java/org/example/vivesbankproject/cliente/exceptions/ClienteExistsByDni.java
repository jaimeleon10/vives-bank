package org.example.vivesbankproject.cliente.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción personalizada para manejar el caso en el que un cliente ya existe en el sistema
 
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ClienteExistsByDni extends ClienteExceptions {

    /**
     * Constructor para crear una instancia de ClienteExistsByDni con un mensaje específico.
     *
     * @param dni El documento de identidad (DNI) que ya existe en el sistema
     */
    public ClienteExistsByDni(String dni) {
        super("Cliente con dni '" + dni + "' ya existente");
    }
}
