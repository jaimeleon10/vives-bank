package org.example.vivesbankproject.rest.movimientos.exceptions.movimientos;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ClienteHasNoMovements extends MovimientosException{
    public ClienteHasNoMovements(String clienteId) {
        super("El cliente con Id" + clienteId + " no tiene movimientos");
    }
}
