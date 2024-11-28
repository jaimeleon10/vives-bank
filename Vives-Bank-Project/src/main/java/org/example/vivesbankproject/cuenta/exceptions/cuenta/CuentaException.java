package org.example.vivesbankproject.cuenta.exceptions.cuenta;

public abstract class CuentaException extends RuntimeException {
    public CuentaException(String message) {
        super(message);
    }
}