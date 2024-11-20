package org.example.vivesbankproject.tarjeta.exceptions;

import java.util.UUID;

public class TarjetaNotFound extends TarjetaException {
    public TarjetaNotFound(String message) {super(message);}

    public TarjetaNotFound(UUID id) {
        super("Tarjeta no encontrada con ID: " + id);
    }

}
