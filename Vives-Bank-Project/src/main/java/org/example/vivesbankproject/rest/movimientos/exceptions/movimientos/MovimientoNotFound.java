package org.example.vivesbankproject.rest.movimientos.exceptions.movimientos;

import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * Excepción para manejar el caso en el que un movimiento no existe en la base de datos.
 * Devuelve un error HTTP 404 (NOT FOUND) al ser lanzada.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class MovimientoNotFound extends MovimientosException {

    /**
     * Constructor para la excepción MovimientoNotFound utilizando un identificador de tipo String.
     *
     * @param id El identificador único del movimiento que no existe.
     */
    public MovimientoNotFound(String id) {
        super("El movimiento con id " + id + " no existe");
    }

    /**
     * Constructor para la excepción MovimientoNotFound utilizando un identificador de tipo ObjectId.
     *
     * @param id El identificador único del movimiento que no existe.
     */
    public MovimientoNotFound(ObjectId id) {
        super("El movimiento con id " + id + " no existe");
    }
}