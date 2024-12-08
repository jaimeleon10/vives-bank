package org.example.vivesbankproject.rest.cuenta.exceptions.cuenta;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción personalizada que se lanza cuando no se encuentra una cuenta asociada al ID de tarjeta proporcionado.
 * Extiende la clase base {@link CuentaException} para manejo centralizado de excepciones relacionadas con cuentas.
 *
 * <p>Esta excepción está asociada con el estado HTTP 404 (NOT_FOUND), lo que indica que el recurso solicitado
 * no está disponible en el servidor.</p>
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class CuentaNotFoundByTarjeta extends CuentaException {

    /**
     * Constructor para crear una instancia de {@code CuentaNotFoundByTarjeta} con un mensaje que indica
     * que no se encontró una cuenta asociada al ID de tarjeta proporcionado.
     *
     * @param id El ID de la tarjeta asociada a la cuenta que no se encontró
     */
    public CuentaNotFoundByTarjeta(Long id) {
        super("Cuenta con ID de tarjeta " + id + " no encontrada");
    }
}