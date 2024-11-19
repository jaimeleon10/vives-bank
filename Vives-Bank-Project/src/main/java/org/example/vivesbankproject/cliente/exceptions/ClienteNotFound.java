package org.example.vivesbankproject.cliente.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ClienteNotFound extends ClienteExceptions {
    public ClienteNotFound(String message) {
        super(message);
    }

    public ClienteNotFound(Long id) {
        super("Cliente con id " + id + " no encontrado");
    }
}
