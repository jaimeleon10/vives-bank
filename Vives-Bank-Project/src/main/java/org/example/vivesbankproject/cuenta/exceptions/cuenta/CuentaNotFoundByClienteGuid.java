package org.example.vivesbankproject.cuenta.exceptions.cuenta;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción personalizada que se lanza cuando no se encuentra una cuenta asociada al GUID del cliente proporcionado.
 * Extiende la clase base {@link CuentaException} para manejo centralizado de excepciones relacionadas con cuentas.
 *
 * <p>Esta excepción se asocia con el estado HTTP 404 (NOT_FOUND), indicando que el recurso solicitado
 * no está disponible en el servidor.</p>
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class CuentaNotFoundByClienteGuid extends CuentaException {

    /**
     * Constructor para crear una instancia de {@code CuentaNotFoundByClienteGuid} con un mensaje que indica
     * que no se encontró una cuenta asociada al GUID del cliente proporcionado.
     *
     * @param guid El GUID del cliente cuya cuenta no se encontró
     */
    public CuentaNotFoundByClienteGuid(String guid) {
        super("Cuenta con GUID de cliente " + guid + " no encontrada");
    }
}