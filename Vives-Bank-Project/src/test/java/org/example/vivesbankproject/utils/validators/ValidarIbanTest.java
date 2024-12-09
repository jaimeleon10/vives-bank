package org.example.vivesbankproject.utils.validators;

import org.example.vivesbankproject.utils.generators.IbanGenerator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidarIbanTest {

    @Test
    void testValidateIban_ValidIban() {
        String iban = IbanGenerator.generateIban();
        // IBAN válido (pasa el cálculo módulo 97)
        String ibanValido = iban;
        assertTrue(ValidarIban.validateIban(ibanValido), "El IBAN debería ser válido.");
    }

    @Test
    void testValidateIban_InvalidIban_Modulo97Fails() {
        // IBAN que falla el cálculo módulo 97
        String ibanInvalidoModulo = "ES9121000418450200051336207";
        assertFalse(ValidarIban.validateIban(ibanInvalidoModulo), "El IBAN debería ser inválido debido al cálculo módulo 97.");
    }

    @Test
    void testValidateIban_InvalidIban_IncorrectLength() {
        // IBAN con longitud incorrecta (menor a 15 caracteres)
        String ibanCorto = "ES91";
        assertFalse(ValidarIban.validateIban(ibanCorto), "Un IBAN demasiado corto debería ser inválido.");

        // IBAN con longitud incorrecta (mayor a 34 caracteres)
        String ibanLargo = "ES912100041845020005133620612345678901234";
        assertFalse(ValidarIban.validateIban(ibanLargo), "Un IBAN demasiado largo debería ser inválido.");
    }

    @Test
    void testValidateIban_InvalidIban_InvalidCharacters() {
        // IBAN con caracteres no permitidos
        String ibanConLetras = "ES91$10004184@5000051336206";
        assertFalse(ValidarIban.validateIban(ibanConLetras), "El IBAN con caracteres inválidos debería ser inválido.");

        // IBAN con caracteres en minúsculas
        String ibanMinusculas = "es9121000418450200051336206";
        assertFalse(ValidarIban.validateIban(ibanMinusculas), "El IBAN en minúsculas debería ser inválido.");
    }

    @Test
    void testValidateIban_NullOrEmpty() {
        // IBAN nulo
        String ibanNulo = null;
        assertFalse(ValidarIban.validateIban(ibanNulo), "Un IBAN nulo debería ser inválido.");

        // IBAN vacío
        String ibanVacio = "";
        assertFalse(ValidarIban.validateIban(ibanVacio), "Un IBAN vacío debería ser inválido.");
    }

    @Test
    void testValidateIban_ValidEdgeCases() {
        // IBAN válido pero al límite de longitud mínima (15 caracteres)
        String ibanMinimoValido = "AD1200012030200359100100"; // Andorra, IBAN válido
        assertTrue(ValidarIban.validateIban(ibanMinimoValido), "El IBAN más corto válido debería ser reconocido como válido.");

        // IBAN válido pero al límite de longitud máxima (34 caracteres)
        String ibanMaximoValido = "GB29NWBK60161331926819"; // Reino Unido, IBAN válido
        assertTrue(ValidarIban.validateIban(ibanMaximoValido), "El IBAN más largo válido debería ser reconocido como válido.");
    }
}
