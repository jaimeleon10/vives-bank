package org.example.vivesbankproject.cliente.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ClienteExistsByTelefono extends ClienteExceptions {
    public ClienteExistsByTelefono(String telefono) {
        super("Cliente con telefono '" + telefono + "' ya existente");
    }
}

