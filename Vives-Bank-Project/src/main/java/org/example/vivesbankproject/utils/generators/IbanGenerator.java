package org.example.vivesbankproject.utils.generators;

import lombok.experimental.UtilityClass;

import java.math.BigInteger;

/**
 * Utility class para la generación de códigos IBAN.
 *
 * <p>Esta clase ofrece un método estático para generar un IBAN válido
 * conforme al formato español.</p>
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@UtilityClass
public class IbanGenerator {

    /**
     * Genera un código IBAN aleatorio siguiendo el formato español.
     *
     * <p>El IBAN generado incluye:
     * <ul>
     *   <li>Código de país: "ES"</li>
     *   <li>Código de banco</li>
     *   <li>Código de sucursal</li>
     *   <li>Dígitos de control</li>
     *   <li>Número de cuenta</li>
     * </ul>
     * Además, calcula los dígitos de verificación utilizando el método módulo 97.
     * </p>
     *
     * @return Un código IBAN válido y aleatorio.
     * @operationId generateIban
     * @summary Generar IBAN
     * @description Genera un IBAN aleatorio válido para el formato español, incluyendo la validación del checksum.
     */
    public String generateIban() {
        String countryCode = "ES";
        String bankCode = "1234";
        String branchCode = "1234";
        String controlDigits = String.format("%02d", (int)(Math.random() * 100));
        String accountNumber = String.format("%010d", (int)(Math.random() * 1_000_000_0000L));

        // Construimos un IBAN temporal para calcular los dígitos de verificación
        String tempIban = bankCode + branchCode + controlDigits + accountNumber + "142800";

        // Convertimos el IBAN temporal a su representación numérica
        String numericIban = tempIban.chars()
                .mapToObj(c -> Character.isDigit(c) ? String.valueOf((char) c) : String.valueOf(c - 'A' + 10))
                .reduce("", String::concat);

        // Calculamos el checksum según el método módulo 97
        int checksum = 98 - (new BigInteger(numericIban).mod(BigInteger.valueOf(97)).intValue());

        // Devolvemos el IBAN final
        return countryCode + String.format("%02d", checksum) + bankCode + branchCode + controlDigits + accountNumber;
    }
}
