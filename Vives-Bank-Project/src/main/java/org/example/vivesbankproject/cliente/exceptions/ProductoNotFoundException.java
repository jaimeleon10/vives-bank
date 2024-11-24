package org.example.vivesbankproject.cliente.exceptions;

import org.example.vivesbankproject.cuenta.exceptions.CuentaException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProductoNotFoundException extends ClienteExceptions {
    public ProductoNotFoundException(String id) {
        super("Producto con id " + id + " no encontrado");
    }
}
