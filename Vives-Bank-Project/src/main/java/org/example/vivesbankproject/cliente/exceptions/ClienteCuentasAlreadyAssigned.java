package org.example.vivesbankproject.cliente.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ClienteCuentasAlreadyAssigned extends ClienteExceptions {
    public ClienteCuentasAlreadyAssigned(String cuentasAsignadas) {
        super("Las siguientes cuentas ya están asignadas: " + cuentasAsignadas);
    }
}
