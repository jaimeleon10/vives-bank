package org.example.vivesbankproject.rest.movimientos.exceptions.domiciliacion;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Clase base para manejar excepciones relacionadas con las operaciones de domiciliación.
 * Se utiliza para lanzar excepciones específicas en caso de error durante la lógica de negocio.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public abstract class DomiciliacionException extends RuntimeException {

    /**
     * Constructor para la excepción de domiciliación.
     *
     * @param message Mensaje de error detallado que se incluirá en la excepción.
     */
    public DomiciliacionException(String message) {
        super(message);
    }
}