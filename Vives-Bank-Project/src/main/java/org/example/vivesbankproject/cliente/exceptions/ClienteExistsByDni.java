package org.example.vivesbankproject.cliente.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ClienteExistsByDni extends ClienteExceptions {
    public ClienteExistsByDni(String dni) {
        super("Cliente con dni '" + dni + "' ya existente");
    }
}
