package org.example.vivesbankproject.cuenta.exceptions;

public abstract class TipoCuentaException extends RuntimeException {
    public TipoCuentaException(String message) {
        super(message);
    }
}
