package org.example.vivesbankproject.cuenta.exceptions.tipoCuenta;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción lanzada cuando se intenta crear o registrar un tipo de cuenta que ya existe en el sistema.
 *
 * <p>Esta excepción incluye un mensaje descriptivo que identifica el nombre del tipo de cuenta que causó el error,
 * permitiendo una rápida identificación y resolución del conflicto.</p>
 *
 * <p>La anotación {@code @ResponseStatus(HttpStatus.BAD_REQUEST)} indica que esta excepción
 * debe devolver un código de estado HTTP 400 (Bad Request) cuando se lanza.</p>
 *
 * @version 1.0-SNAPSHOT
 * @see TipoCuentaException
 * @see RuntimeException
 * @author Jaime León, Natalia González, German Fernandez,
 *         Alba García, Mario de Domingo, Alvaro Herrero
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TipoCuentaExists extends TipoCuentaException {

    /**
     * Constructor para crear una excepción {@code TipoCuentaExists} con un mensaje que incluye el nombre del tipo de cuenta existente.
     *
     * @param nombre Nombre del tipo de cuenta que ya existe
     */
    public TipoCuentaExists(String nombre) {
        super("Cuenta con nombre " + nombre + " ya existente");
    }
}