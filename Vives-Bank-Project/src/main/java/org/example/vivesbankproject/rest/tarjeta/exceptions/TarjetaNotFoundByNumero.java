package org.example.vivesbankproject.rest.tarjeta.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TarjetaNotFoundByNumero extends TarjetaException {
    public TarjetaNotFoundByNumero(String numeroTarjeta) {
        super("Tarjeta no encontrada con el numero de tarjeta: " + numeroTarjeta);
    }
}
