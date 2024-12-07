package org.example.vivesbankproject.cuenta.exceptions.tipoCuenta;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción lanzada cuando un tipo de cuenta no puede ser encontrado en el sistema por su identificador.
 *
 * <p>Esta excepción proporciona un mensaje descriptivo que incluye el identificador del tipo de cuenta
 * que no pudo ser localizado, ayudando en el diagnóstico de errores.</p>
 *
 * <p>La anotación {@code @ResponseStatus(HttpStatus.NOT_FOUND)} indica que esta excepción
 * debe devolver un código de estado HTTP 404 (Not Found) cuando se lanza.</p>
 *
 * @version 1.0-SNAPSHOT
 * @see TipoCuentaException
 * @see RuntimeException
 * @see ResponseStatus
 * @see HttpStatus
 * @author Jaime León, Natalia González, German Fernandez,
 *         Alba García, Mario de Domingo, Alvaro Herrero
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class TipoCuentaNotFound extends TipoCuentaException {

    /**
     * Constructor para crear una excepción {@code TipoCuentaNotFound} con un mensaje que incluye el identificador no encontrado.
     *
     * @param id Identificador del tipo de cuenta que no pudo ser localizado
     */
    public TipoCuentaNotFound(String id) {
        super("Tipo de cuenta con id " + id + " no encontrado");
    }
}