package org.example.vivesbankproject.rest.movimientos.exceptions.movimientos;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.math.BigDecimal;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NegativeAmount extends MovimientosException{
    public NegativeAmount(BigDecimal message) {
        super("La cantidad del movimiento no puede ser negativa: " + message);
    }
}
