package org.example.vivesbankproject.rest.movimientos.exceptions.domiciliacion;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.math.BigDecimal;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SaldoInsuficienteException  extends  DomiciliacionException{
    public SaldoInsuficienteException(String Iban, BigDecimal cantidad) {
        super("El saldo de la cuenta " + Iban + " es insuficiente para realizar la domiciliación. Saldo actual: " + cantidad);
    }
}
