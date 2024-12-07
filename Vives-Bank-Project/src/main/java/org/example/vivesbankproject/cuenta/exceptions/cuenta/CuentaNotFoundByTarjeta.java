package org.example.vivesbankproject.cuenta.exceptions.cuenta;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CuentaNotFoundByTarjeta extends CuentaException {
    public CuentaNotFoundByTarjeta(Long id) {
        super("Cuenta con id de tarjeta " + id + " no encontrada");
    }
}
