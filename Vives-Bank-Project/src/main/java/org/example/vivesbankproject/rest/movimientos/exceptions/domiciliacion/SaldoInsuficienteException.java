package org.example.vivesbankproject.rest.movimientos.exceptions.domiciliacion;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.math.BigDecimal;


/**
 * Excepción para manejar casos en los que el saldo de una cuenta es insuficiente para realizar una domiciliación.
 * Devuelve un error HTTP 400 (BAD REQUEST) al ser lanzada.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SaldoInsuficienteException extends DomiciliacionException {

    /**
     * Constructor para la excepción de saldo insuficiente.
     *
     * @param cantidad La cantidad actual en la cuenta que es insuficiente.
     */
    public SaldoInsuficienteException(String Iban, BigDecimal cantidad) {
        super("El saldo de la cuenta " + Iban + " es insuficiente para realizar la domiciliación. Saldo actual: " + cantidad);
    }
}