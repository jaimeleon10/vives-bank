package org.example.vivesbankproject.cuenta.exceptions.tipoCuenta;

/**
 * Clase abstracta base para manejar excepciones relacionadas con operaciones de tipo de cuenta en la aplicación.
 *
 * <p>Esta clase sirve como base para todas las excepciones específicas relacionadas con los tipos de cuenta,
 * permitiendo un manejo centralizado y consistente de los errores relacionados con esta entidad.</p>
 *
 * <p>Extiende {@link RuntimeException}, lo que permite capturar excepciones no verificadas (unchecked exceptions)
 * en tiempo de ejecución.</p>
 *
 * @version 1.0-SNAPSHOT
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 */
public abstract class TipoCuentaException extends RuntimeException {

    /**
     * Constructor para crear una instancia de {@code TipoCuentaException} con un mensaje descriptivo de la excepción.
     *
     * @param message Mensaje de error que describe la excepción relacionada con el tipo de cuenta
     */
    public TipoCuentaException(String message) {
        super(message);
    }
}