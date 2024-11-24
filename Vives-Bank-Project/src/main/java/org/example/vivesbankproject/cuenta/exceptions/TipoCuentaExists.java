package org.example.vivesbankproject.cuenta.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TipoCuentaExists extends TipoCuentaException {
    public TipoCuentaExists(String nombre) {
        super("Cuenta con nombre " + nombre + " ya existente");
    }
}
