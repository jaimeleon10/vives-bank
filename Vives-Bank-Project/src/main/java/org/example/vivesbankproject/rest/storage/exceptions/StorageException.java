package org.example.vivesbankproject.rest.storage.exceptions;

import java.io.Serial;

/**
 * Clase base para todas las excepciones relacionadas con operaciones de almacenamiento.
 * Esta clase extiende {@link RuntimeException} y se utiliza como clase base para excepciones
 * específicas que se generen durante operaciones de almacenamiento.
 *
 * @author Jaime León, Natalia González, Germán Fernández, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
public abstract class StorageException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 43876691117560211L;

    /**
     * Constructor que recibe un mensaje como parámetro para describir el error.
     *
     * @param mensaje Mensaje que proporciona información sobre el error.
     */
    public StorageException(String mensaje) {
        super(mensaje);
    }
}