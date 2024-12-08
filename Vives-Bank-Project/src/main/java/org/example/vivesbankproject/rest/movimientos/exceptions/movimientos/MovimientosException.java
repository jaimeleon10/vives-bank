package org.example.vivesbankproject.rest.movimientos.exceptions.movimientos;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public abstract class MovimientosException extends RuntimeException{
    public MovimientosException(String message) {
        super(message);
    }
}