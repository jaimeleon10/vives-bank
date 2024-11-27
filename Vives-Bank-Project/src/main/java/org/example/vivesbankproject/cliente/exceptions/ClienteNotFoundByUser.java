package org.example.vivesbankproject.cliente.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ClienteNotFoundByUser extends ClienteExceptions {
    public ClienteNotFoundByUser(String id) {
        super("No existe ningún cliente asignado al usuario con id: " + id);
    }
}
