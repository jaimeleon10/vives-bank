package org.example.vivesbankproject.cuenta.exceptions.cuenta;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción personalizada que se lanza cuando no se encuentra una cuenta asociada al IBAN proporcionado.
 * Extiende la clase base {@link CuentaException} para manejo centralizado de excepciones relacionadas con cuentas.
 *
 * <p>Esta excepción se asocia con el estado HTTP 404 (NOT_FOUND), indicando que el recurso solicitado
 * no está disponible en el servidor.</p>
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class CuentaNotFoundByIban extends CuentaException {

    /**
     * Constructor para crear una instancia de {@code CuentaNotFoundByIban} con un mensaje que indica
     * que no se encontró una cuenta asociada al IBAN proporcionado.
     *
     * @param iban El IBAN de la cuenta que no se encontró
     */
    public CuentaNotFoundByIban(String iban) {
        super("Cuenta con IBAN " + iban + " no encontrada");
    }
}