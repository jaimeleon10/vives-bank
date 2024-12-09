package org.example.vivesbankproject.utils.validators;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.experimental.UtilityClass;

/**
 * Clase de utilidad para validar números de tarjetas de crédito.
 * <p>
 * Esta clase proporciona un método para validar números de tarjetas utilizando la lógica de validación Luhn
 * y comprobando el formato básico de una tarjeta de 16 dígitos.
 * </p>
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@UtilityClass
@Tag(name = "ValidarTarjeta", description = "Clase para validar tarjetas de crédito")
public class ValidarTarjeta {

    /**
     * Valida el número de una tarjeta de crédito.
     *
     * <p>
     * Este método verifica si el número de la tarjeta es válido en términos de formato numérico y longitud,
     * además de realizar la validación mediante el algoritmo Luhn.
     * </p>
     *
     * @param tarjeta El número de la tarjeta a validar.
     * @return {@code true} si la tarjeta es válida, de lo contrario {@code false}.
     */
    @Operation(summary = "Valida el número de una tarjeta de crédito", description = "Verifica el número de tarjeta para determinar su validez utilizando el método Luhn")
    public boolean validateTarjeta(
            @Schema(description = "Número de la tarjeta de crédito", example = "4111111111111111") String tarjeta) {

        // Comprueba si la tarjeta es nula o si no contiene solo números con una longitud válida
        if (tarjeta == null || !tarjeta.matches("\\d+") || tarjeta.length() != 16) {
            return false;
        }

        // Realiza la validación Luhn
        return validateLuhn(tarjeta);
    }

    /**
     * Aplica el algoritmo Luhn para validar el número de la tarjeta.
     *
     * <p>
     * El algoritmo Luhn es un método utilizado para validar números de tarjetas de crédito.
     * Este método verifica la integridad numérica de un número de tarjeta aplicando un patrón de suma alternada.
     * </p>
     *
     * @param tarjeta El número de tarjeta que se va a validar.
     * @return {@code true} si el número pasa la validación Luhn, de lo contrario {@code false}.
     */
    @Schema(description = "Aplica el algoritmo Luhn para validar la tarjeta")
    private boolean validateLuhn(String tarjeta) {
        int suma = 0;
        boolean duplicar = false;

        // Recorre la tarjeta de derecha a izquierda y aplica la lógica Luhn
        for (int i = tarjeta.length() - 1; i >= 0; i--) {
            int digit = tarjeta.charAt(i) - '0';

            if (duplicar) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }

            suma += digit;
            duplicar = !duplicar;
        }

        // Devuelve si la suma es múltiplo de 10
        return suma % 10 == 0;
    }
}