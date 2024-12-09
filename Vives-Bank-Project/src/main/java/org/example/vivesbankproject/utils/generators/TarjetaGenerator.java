package org.example.vivesbankproject.utils.generators;

import lombok.experimental.UtilityClass;

import java.util.Random;

/**
 * Utility class para la generación de números de tarjetas.
 *
 * <p>Proporciona un método para generar un número de tarjeta válido utilizando
 * el algoritmo de Luhn para el cálculo del dígito de control.</p>
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@UtilityClass
public class TarjetaGenerator {

    /**
     * Genera un número de tarjeta de crédito válido.
     *
     * <p>El número de tarjeta generado comienza con el dígito "4" y consta de 16 dígitos en total.
     * El último dígito se calcula utilizando el algoritmo de Luhn para garantizar la validez del número.</p>
     *
     * @return Un número de tarjeta de crédito válido.
     */
    public String generarTarjeta() {
        StringBuilder numTarjeta = new StringBuilder();

        // Comienza con el prefijo "4"
        numTarjeta.append("4");

        Random random = new Random();
        // Genera los primeros 15 dígitos
        for (int i = 0; i < 13; i++) {
            numTarjeta.append(random.nextInt(10));
        }

        // Calcula el dígito de control usando el algoritmo de Luhn
        int digitoDeControl = calculoLuhn(numTarjeta.toString());
        numTarjeta.append(digitoDeControl);

        return numTarjeta.toString();
    }

    /**
     * Aplica el algoritmo de Luhn para calcular el dígito de control de un número de tarjeta.
     *
     * <p>Este método calcula y retorna el dígito necesario para que el número de tarjeta
     * cumpla con el algoritmo de Luhn, lo que garantiza su validez.</p>
     *
     * @param cardNumber Número de tarjeta sin el dígito de control.
     * @return El dígito de control calculado según el algoritmo de Luhn.
     */
    private int calculoLuhn(String cardNumber) {
        int suma = 0;
        boolean duplicar = false;

        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int digit = cardNumber.charAt(i) - '0';

            if (duplicar) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }

            suma += digit;
            duplicar = !duplicar;
        }

        return (10 - (suma % 10)) % 10;
    }
}
