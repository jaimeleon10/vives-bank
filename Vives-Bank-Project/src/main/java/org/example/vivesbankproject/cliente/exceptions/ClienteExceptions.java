package org.example.vivesbankproject.cliente.exceptions;

/**
 * Clase abstracta base para manejar excepciones relacionadas con operaciones de cliente.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
public abstract class ClienteExceptions extends RuntimeException {

    /**
     * Constructor para crear una instancia de ClienteExceptions con un mensaje de error.
     *
     * @param message Mensaje de error que describe la excepción
     */
    public ClienteExceptions(String message) {
        super(message);
    }
}