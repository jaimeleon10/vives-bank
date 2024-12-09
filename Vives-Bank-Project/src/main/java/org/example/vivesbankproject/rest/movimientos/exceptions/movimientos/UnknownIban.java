package org.example.vivesbankproject.rest.movimientos.exceptions.movimientos;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
/**
 * Excepción lanzada cuando el IBAN proporcionado no es válido como IBAN de origen.
 * Responde con un error HTTP 404 (NOT FOUND).
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UnknownIban extends MovimientosException {

    /**
     * Constructor para crear una excepción UnknownIban con un mensaje específico que indique el motivo del error.
     *
     * @param iban El IBAN que no es válido como IBAN de origen.
     */
    public UnknownIban(String iban) {
        super("El iban " + iban + " no es válido como Iban de origen");
    }
}