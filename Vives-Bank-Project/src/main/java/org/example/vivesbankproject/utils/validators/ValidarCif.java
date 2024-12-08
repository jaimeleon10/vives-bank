package org.example.vivesbankproject.utils.validators;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.experimental.UtilityClass;

/**
 * Clase de utilidad para validar el formato y lógica de un Código de Identificación Fiscal (CIF).
 * <p>
 * Esta clase contiene métodos para verificar la validez de un CIF español, comprobando tanto su formato
 * como la validez lógica interna de su dígito de control según la normativa vigente.
 * </p>
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@UtilityClass
@Tag(name = "ValidarCif", description = "Clase para validar el CIF con lógica y formato")
public class ValidarCif {

    /**
     * Valida un Código de Identificación Fiscal (CIF) de acuerdo con las reglas oficiales.
     *
     * <p>
     * El método verifica el formato y la lógica del dígito de control. Comprueba la validez según el tipo de letra inicial:
     * <ul>
     *     <li>Empresas con dígito de control numérico.</li>
     *     <li>Entidades con letra de control.</li>
     *     <li>Casos generales con condiciones mixtas de dígitos y letras.</li>
     * </ul>
     *
     * @param cif El CIF a validar.
     * @return {@code true} si el CIF es válido, de lo contrario {@code false}.
     */
    @Operation(summary = "Valida el formato y lógica de un CIF español", description = "Verifica el formato y la lógica interna de un CIF español")
    public boolean validateCif(
            @Schema(description = "Código de Identificación Fiscal (CIF) para validar", example = "B12345678") String cif) {

        // Comprueba que el CIF no es null, que tiene el formato adecuado y su longitud es válida
        if (cif == null || !cif.matches("[A-HJ-NP-SUVW][0-9]{7}[0-9A-J]") || cif.length() != 9) {
            return false; // CIF no válido en formato o longitud
        }

        char letraInicial = cif.charAt(0); // Primera letra del CIF
        String numeros = cif.substring(1, 8); // Extrae los números del CIF
        char digitoControl = cif.charAt(8); // Obtiene el dígito de control al final

        // Suma los dígitos ubicados en posiciones pares
        int sumaPares = 0;
        for (int i = 1; i < numeros.length(); i += 2) {
            sumaPares += Character.getNumericValue(numeros.charAt(i));
        }

        // Suma los dígitos ubicados en posiciones impares, multiplicándolos por 2 y calculando la suma de los dígitos
        int sumaImpares = 0;
        for (int i = 0; i < numeros.length(); i += 2) {
            int doble = Character.getNumericValue(numeros.charAt(i)) * 2;
            sumaImpares += doble / 10 + doble % 10;
        }

        int sumaTotal = sumaPares + sumaImpares; // Calcula la suma total
        int unidadControl = (10 - (sumaTotal % 10)) % 10; // Obtiene el dígito de control esperado

        // Validación lógica dependiendo de la letra inicial
        if ("ABEH".indexOf(letraInicial) != -1) {
            // Validación para empresas con dígito de control numérico
            return Character.getNumericValue(digitoControl) == unidadControl;
        } else if ("KPQRSNW".indexOf(letraInicial) != -1) {
            // Validación para entidades con letra de control
            char letraControl = "JABCDEFGHI".charAt(unidadControl);
            return digitoControl == letraControl;
        } else {
            // Validación general para otros casos mixtos
            return Character.getNumericValue(digitoControl) == unidadControl ||
                    digitoControl == "JABCDEFGHI".charAt(unidadControl);
        }
    }
}