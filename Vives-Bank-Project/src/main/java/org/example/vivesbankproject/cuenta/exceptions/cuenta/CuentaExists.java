package org.example.vivesbankproject.cuenta.exceptions.cuenta;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción personalizada que se lanza cuando se intenta crear o registrar una cuenta que ya existe.
 * Extiende la clase base {@link CuentaException} para manejo centralizado de excepciones relacionadas con cuentas.
 *
 * <p>Esta excepción se asocia con el estado HTTP 400 (BAD_REQUEST), indicando que la solicitud
 * realizada por el cliente contiene un error lógico o de validación.</p>
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CuentaExists extends CuentaException {

    /**
     * Constructor para crear una instancia de {@code CuentaExists} con un mensaje que indica
     * que ya existe una cuenta asociada al IBAN proporcionado.
     *
     * @param iban El IBAN de la cuenta que ya existe
     */
    public CuentaExists(String iban) {
        super("Cuenta con IBAN " + iban + " ya existente");
    }
}