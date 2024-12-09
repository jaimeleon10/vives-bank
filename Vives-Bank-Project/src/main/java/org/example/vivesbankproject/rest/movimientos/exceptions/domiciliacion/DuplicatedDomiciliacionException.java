package org.example.vivesbankproject.rest.movimientos.exceptions.domiciliacion;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción para manejar casos en los que una domiciliación con el mismo IBAN ya existe en el sistema.
 * Devuelve un error HTTP 400 (BAD REQUEST) al ser lanzada.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DuplicatedDomiciliacionException extends DomiciliacionException {

    /**
     * Constructor para la excepción de domiciliación duplicada.
     *
     * @param iban El código IBAN que ya existe en el sistema.
     */
    public DuplicatedDomiciliacionException(String iban) {
        super("Domiciliación al IBAN " + iban + " ya existente");
    }
}