package org.example.vivesbankproject.utils.generators;

import lombok.experimental.UtilityClass;

import java.util.Random;

@UtilityClass
public class TarjetaGenerator {

    public String generarTarjeta() {
        StringBuilder numTarjeta = new StringBuilder();

        numTarjeta.append("4");

        Random random = new Random();
        for (int i = 0; i < 13; i++) {
            numTarjeta.append(random.nextInt(10));
        }

        int digitoDeControl = calculoLuhn(numTarjeta.toString());
        numTarjeta.append(digitoDeControl);

        return numTarjeta.toString();
    }

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
