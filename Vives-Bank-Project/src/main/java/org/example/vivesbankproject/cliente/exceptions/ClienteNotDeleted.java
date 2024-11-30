package org.example.vivesbankproject.cliente.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ClienteNotDeleted extends ClienteExceptions {
    public ClienteNotDeleted(String id) {
        super("Cliente con guid '" + id + "' no se pudo borrar");
    }
}