package org.example.vivesbankproject.cuenta.exceptions.cuenta;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.NOT_FOUND)
public class CuentaNotFoundByNumTarjeta extends CuentaException {
    public CuentaNotFoundByNumTarjeta(String numTarjeta) {
        super("Cuenta correspondiente a la tarjeta " + numTarjeta + " no encontrada");
    }
}