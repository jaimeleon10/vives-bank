package org.example.vivesbankproject.cuenta.exceptions.cuenta;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CuentaNotFoundByClienteGuid extends CuentaException {
    public CuentaNotFoundByClienteGuid(String guid) {
        super("Cuenta con guid de cliente " + guid + " no encontrada");
    }
}