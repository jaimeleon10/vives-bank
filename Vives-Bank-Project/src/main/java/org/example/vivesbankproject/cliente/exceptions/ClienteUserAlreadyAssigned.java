package org.example.vivesbankproject.cliente.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ClienteUserAlreadyAssigned extends ClienteExceptions {
    public ClienteUserAlreadyAssigned(String id) {
        super("El usuario con el id " + id + " ya está asignado a otro cliente");
    }
}
