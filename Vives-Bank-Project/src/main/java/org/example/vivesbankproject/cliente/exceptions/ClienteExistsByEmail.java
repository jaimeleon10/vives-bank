package org.example.vivesbankproject.cliente.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ClienteExistsByEmail extends ClienteExceptions {
    public ClienteExistsByEmail(String email) {
        super("Cliente con email '" + email + "' ya existente");
    }
}

