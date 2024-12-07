package org.example.vivesbankproject.movimientos.exceptions.movimientos;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TransferenciaNoRevocableException extends MovimientosException  {
    public TransferenciaNoRevocableException(String guid) {
        super("La transferencia con guid '" + guid + "' no puede ser revocada, han pasado más de 24 horas");
    }
}
