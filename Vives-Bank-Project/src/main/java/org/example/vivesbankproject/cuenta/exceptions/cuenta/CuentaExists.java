package org.example.vivesbankproject.cuenta.exceptions.cuenta;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CuentaExists extends CuentaException {
    public CuentaExists(String iban) {
        super("Cuenta con iban " + iban + " ya existente");
    }
}