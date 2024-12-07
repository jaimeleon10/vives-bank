package org.example.vivesbankproject.utils.validators;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ValidarCif {
    public boolean validateCif(String cif) {
        if (cif == null || !cif.matches("[A-HJ-NP-SUVW][0-9]{7}[0-9A-J]") || cif.length() != 9) {
            return false; // Validar formato y longitud general
        }

        char letraInicial = cif.charAt(0);
        String numeros = cif.substring(1, 8);
        char digitoControl = cif.charAt(8);

        // Sumar los dígitos pares
        int sumaPares = 0;
        for (int i = 1; i < numeros.length(); i += 2) {
            sumaPares += Character.getNumericValue(numeros.charAt(i));
        }

        // Sumar los dígitos impares, multiplicar por 2 y sumar los dígitos del resultado
        int sumaImpares = 0;
        for (int i = 0; i < numeros.length(); i += 2) {
            int doble = Character.getNumericValue(numeros.charAt(i)) * 2;
            sumaImpares += doble / 10 + doble % 10;
        }

        int sumaTotal = sumaPares + sumaImpares;
        int unidadControl = (10 - (sumaTotal % 10)) % 10;

        // Comprobar el dígito de control según la letra inicial
        if ("ABEH".indexOf(letraInicial) != -1) {
            // Empresas con dígito de control numérico
            return Character.getNumericValue(digitoControl) == unidadControl;
        } else if ("KPQRSNW".indexOf(letraInicial) != -1) {
            // Entidades con letra de control
            char letraControl = "JABCDEFGHI".charAt(unidadControl);
            return digitoControl == letraControl;
        } else {
            // Empresas que pueden tener dígito o letra de control
            return Character.getNumericValue(digitoControl) == unidadControl ||
                    digitoControl == "JABCDEFGHI".charAt(unidadControl);
        }
    }
}