package org.example.vivesbankproject.tarjeta.exceptions;

/**
 * Clase base abstracta para excepciones relacionadas con tarjetas.
 * Extiende de RuntimeException para permitir excepciones no verificadas.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
public abstract class TarjetaException extends RuntimeException {
    /**
     * Constructor que permite crear una excepción con un mensaje personalizado.
     *
     * @param message Mensaje descriptivo de la excepción
     */
    public TarjetaException(String message) {
        super(message);
    }
}