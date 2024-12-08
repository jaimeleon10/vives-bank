package org.example.vivesbankproject.rest.tarjeta.exceptions;

public abstract class TarjetaException extends RuntimeException{
    public TarjetaException(String message) {
        super(message);
    }
}
