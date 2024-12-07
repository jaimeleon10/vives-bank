package org.example.vivesbankproject.movimientos.exceptions.movimientos;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MovimientoIsNotTransferenciaException extends MovimientosException  {
    public MovimientoIsNotTransferenciaException(String guid) {
        super("El movimiento con guid '" + guid + "' no es una transferencia");
    }
}
