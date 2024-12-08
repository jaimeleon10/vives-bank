package org.example.vivesbankproject.rest.cuenta.exceptions.cuenta;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Clase base para manejar excepciones relacionadas con operaciones de cuenta en la aplicación.
 * Extiende RuntimeException para capturar excepciones no verificadas (unchecked exceptions).
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
public abstract class CuentaException extends RuntimeException {
    public CuentaException(String message) {
        super(message);
    }
}