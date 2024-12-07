package org.example.vivesbankproject.movimientos.exceptions.movimientos;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UnknownIban extends MovimientosException{
    public UnknownIban(String iban) {
        super("El iban " + iban + " no es válido como Iban de origen");
    }
}