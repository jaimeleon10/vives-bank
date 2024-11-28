package org.example.vivesbankproject.utils.validators;

import lombok.experimental.UtilityClass;

@UtilityClass
public class validarTarjeta {
    public boolean validateTarjeta(String tarjeta) {
        if (!tarjeta.matches("\\d+") || tarjeta.length() == 16) {
            return false;
        }

        return validateLuhn(tarjeta);
    }

    private boolean validateLuhn(String tarjeta) {
        int suma = 0;
        boolean duplicar = false;

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

        return suma % 10 == 0;
    }
}
