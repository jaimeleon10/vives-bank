package org.example.vivesbankproject.utils.generators;

import org.example.vivesbankproject.utils.validators.ValidarIban;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

class IbanGeneratorTest {

    @Test
    void generateIban_length() {
        String iban = IbanGenerator.generateIban();
        assertEquals(24, iban.length(), "La longitud del IBAN debe ser 24 caracteres");
    }

    @Test
    void generateIban_startsWithES() {
        String iban = IbanGenerator.generateIban();
        assertEquals("ES", iban.substring(0, 2), "El IBAN debe comenzar con 'ES'");
    }

    @Test
    void generateIban_differentIbans() {
        String iban1 = IbanGenerator.generateIban();
        String iban2 = IbanGenerator.generateIban();
        assertNotEquals(iban1, iban2, "Los IBANs generados deben ser diferentes");
    }

    // Prueba adicional para validar el IBAN completo (si tienes una librería de validación de IBAN)
    @Test
    void generateIban_isValid() {
        String iban = IbanGenerator.generateIban();
        // Aquí utilizarías una librería de validación de IBAN para verificar si el IBAN generado es válido
        assertTrue(ValidarIban.validateIban(iban), "El IBAN generado debe ser válido");
    }

}