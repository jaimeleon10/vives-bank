package org.example.vivesbankproject.utils.validators;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.experimental.UtilityClass;

/**
 * Clase de utilidad para validar códigos IBAN.
 * <p>
 * Esta clase proporciona un método para validar un IBAN completo utilizando las reglas de validación estándar,
 * incluyendo el cálculo mediante el método módulo 97 después de reorganizar el IBAN.
 * </p>
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@UtilityClass
@Tag(name = "ValidarIban", description = "Clase para validar el formato y lógica de un IBAN")
public class ValidarIban {

    /**
     * Valida un código IBAN siguiendo la normativa estándar.
     *
     * <p>
     * Este método verifica el formato y realiza un cálculo lógico utilizando el método módulo 97 para determinar
     * la validez del IBAN. Se realiza la reorganización estándar para el cálculo: se mueven los primeros 4 caracteres
     * al final y se convierten en valores numéricos.
     * </p>
     *
     * @param iban El código IBAN a validar.
     * @return {@code true} si el IBAN es válido, de lo contrario {@code false}.
     */
    @Operation(summary = "Valida el formato y lógica de un IBAN", description = "Verifica el formato y realiza el cálculo lógico de un IBAN utilizando el método módulo 97")
    public boolean validateIban(
            @Schema(description = "Código IBAN para validar", example = "ES9121000418450200051336206") String iban) {

        // Comprueba que el IBAN no sea null, que tenga una longitud válida y que coincida con el patrón alfanumérico
        if (iban == null || iban.length() < 15 || iban.length() > 34 || !iban.matches("[A-Z0-9]+")) {
            return false; // El IBAN no es válido
        }

        // Reorganiza el IBAN al mover los primeros 4 caracteres al final
        String reorganizedIban = iban.substring(4) + iban.substring(0, 4);

        // Convierte el IBAN reorganizado a una representación numérica según las reglas estándar
        StringBuilder numericIban = new StringBuilder();
        for (char c : reorganizedIban.toCharArray()) {
            if (Character.isDigit(c)) {
                numericIban.append(c);
            } else {
                numericIban.append(c - 'A' + 10);
            }
        }

        // Realiza el cálculo módulo 97 para validar el IBAN
        return modulo97(numericIban.toString()) == 1;
    }

    /**
     * Calcula el residuo de un número dado utilizando el método módulo 97.
     *
     * <p>
     * Este método es fundamental para validar la lógica de un IBAN, verificando su integridad numérica mediante cálculos
     * con el módulo 97.
     * </p>
     *
     * @param number El número convertido en representación numérica que se validará.
     * @return El residuo resultante de la operación módulo 97.
     */
    @Schema(description = "Realiza el cálculo módulo 97 para validar el IBAN")
    private int modulo97(String number) {
        int remainder = 0;

        // Calcula el residuo paso a paso, aplicando la operación módulo 97 para cada dígito del número
        for (int i = 0; i < number.length(); i++) {
            char digit = number.charAt(i);
            remainder = (remainder * 10 + (digit - '0')) % 97;
        }

        return remainder;
    }
}