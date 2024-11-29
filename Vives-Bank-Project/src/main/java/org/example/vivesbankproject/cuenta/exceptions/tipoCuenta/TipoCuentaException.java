package org.example.vivesbankproject.cuenta.exceptions.tipoCuenta;

public abstract class TipoCuentaException extends RuntimeException {
    public TipoCuentaException(String message) {
        super(message);
    }
}
