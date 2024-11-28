package org.example.vivesbankproject.utils.validators;

import lombok.experimental.UtilityClass;

@UtilityClass
public class validarIban {

    public boolean validateIban(String iban) {
        if (iban == null || iban.length() < 15 || iban.length() > 34 || !iban.matches("[A-Z0-9]+")) {
            return false; // Validar formato y longitud general
        }

        String reorganizedIban = iban.substring(4) + iban.substring(0, 4);

        StringBuilder numericIban = new StringBuilder();
        for (char c : reorganizedIban.toCharArray()) {
            if (Character.isDigit(c)) {
                numericIban.append(c);
            } else {
                numericIban.append(c - 'A' + 10);
            }
        }

        return modulo97(numericIban.toString()) == 1;
    }

    private int modulo97(String number) {
        int remainder = 0;

        for (int i = 0; i < number.length(); i++) {
            char digit = number.charAt(i);
            remainder = (remainder * 10 + (digit - '0')) % 97;
        }

        return remainder;
    }
}

