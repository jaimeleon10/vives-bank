package org.example.vivesbankproject.rest.movimientos.exceptions.domiciliacion;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DuplicatedDomiciliacionException extends DomiciliacionException{
    public DuplicatedDomiciliacionException(String iban) {
        super("Domiciliación al IBAN " + iban + " ya existente");
    }
}
