package org.example.vivesbankproject.cliente.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ClienteNotFoundByDni extends ClienteExceptions {
    public ClienteNotFoundByDni(String dni) {
        super("Cliente con dni '" + dni + "' no encontrado");
    }
}