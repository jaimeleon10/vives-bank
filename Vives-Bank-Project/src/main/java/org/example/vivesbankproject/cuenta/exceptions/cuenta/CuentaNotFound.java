package org.example.vivesbankproject.cuenta.exceptions.cuenta;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CuentaNotFound extends CuentaException {
    public CuentaNotFound(String id) {
        super("Cuenta con id " + id + " no encontrada");
    }
}