package org.example.vivesbankproject.tarjeta.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TarjetaNotFound extends TarjetaException {
    public TarjetaNotFound(String id) {
        super("Tarjeta no encontrada con guid: " + id);
    }
}
