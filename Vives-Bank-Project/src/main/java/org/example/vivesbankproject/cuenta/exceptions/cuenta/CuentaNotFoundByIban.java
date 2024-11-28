package org.example.vivesbankproject.cuenta.exceptions.cuenta;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CuentaNotFoundByIban extends CuentaException {
    public CuentaNotFoundByIban(String iban) {
        super("Cuenta con iban " + iban + " no encontrada");
    }
}