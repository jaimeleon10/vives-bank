package org.example.vivesbankproject.cuenta.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CuentaNotFound extends CuentaException {
    public CuentaNotFound(String message) {
        super(message);
    }

    public CuentaNotFound(UUID id) {
        super("Cuenta con id " + id + " no encontrada");
    }
}