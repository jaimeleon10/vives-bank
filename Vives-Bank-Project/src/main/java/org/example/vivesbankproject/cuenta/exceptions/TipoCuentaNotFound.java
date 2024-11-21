package org.example.vivesbankproject.cuenta.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TipoCuentaNotFound extends TipoCuentaException {
    public TipoCuentaNotFound(String id) {
        super("Tipo de cuenta con id " + id + " no encontrado");
    }
}
