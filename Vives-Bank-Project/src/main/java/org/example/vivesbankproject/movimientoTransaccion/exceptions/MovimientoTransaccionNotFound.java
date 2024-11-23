package org.example.vivesbankproject.movimientoTransaccion.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MovimientoTransaccionNotFound extends MovimientoTransaccionException {
    public MovimientoTransaccionNotFound(String id) {
        super("El movimiento transaccion con id " + id + " no existe");
    }
}
