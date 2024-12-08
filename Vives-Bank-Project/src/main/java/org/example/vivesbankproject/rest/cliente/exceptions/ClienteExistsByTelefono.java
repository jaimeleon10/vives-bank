package org.example.vivesbankproject.rest.cliente.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción personalizada para manejar el caso en el que un cliente ya existe en el sistema

 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ClienteExistsByTelefono extends ClienteExceptions {

    /**
     * Constructor para crear una instancia de ClienteExistsByTelefono con un mensaje específico.
     *
     * @param telefono El número de teléfono que ya existe en el sistema
     */
    public ClienteExistsByTelefono(String telefono) {
        super("Cliente con telefono '" + telefono + "' ya existente");
    }
}

