package org.example.vivesbankproject.movimientos.exceptions.domiciliacion;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DuplicatedDomiciliacionException extends DomiciliacionException{
    public DuplicatedDomiciliacionException(String guid) {
        super("Domiciliación con guid " + guid + " ya existente");
    }
}
