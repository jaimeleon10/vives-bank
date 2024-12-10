package org.example.vivesbankproject.utils.validators;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidarCifTest {

    @Test
    void testCifValidoLetra() {
        String cifValido = "A20608030";
        assertTrue(ValidarCif.validateCif(cifValido), "El CIF con letra de control debería ser válido.");
    }

    @Test
    void testCifInvalidoFormato() {
        String cifInvalido = "12345678B"; // No empieza con letra válida
        assertFalse(ValidarCif.validateCif(cifInvalido), "El CIF con formato inválido debería ser inválido.");
    }

    @Test
    void testCifInvalidoLongitud() {
        String cifInvalido = "B1234567"; // Longitud incorrecta
        assertFalse(ValidarCif.validateCif(cifInvalido), "El CIF con longitud incorrecta debería ser inválido.");
    }

    @Test
    void testCifInvalidoCaracteres() {
        String cifInvalido = "B12345A78"; // Caracteres no permitidos
        assertFalse(ValidarCif.validateCif(cifInvalido), "El CIF con caracteres inválidos debería ser inválido.");
    }

    @Test
    void testCifInvalidoDigitoControl() {
        String cifInvalido = "B12345679"; // Dígito de control erróneo
        assertFalse(ValidarCif.validateCif(cifInvalido), "El CIF con dígito de control incorrecto debería ser inválido.");
    }

    @Test
    void testCifInvalidoLetraControl() {
        String cifInvalido = "K1234567A"; // Letra de control incorrecta
        assertFalse(ValidarCif.validateCif(cifInvalido), "El CIF con letra de control incorrecta debería ser inválido.");
    }

    @Test
    void testCifNuloOVacio() {
        assertFalse(ValidarCif.validateCif(null), "El CIF nulo debería ser inválido.");
        assertFalse(ValidarCif.validateCif(""), "El CIF vacío debería ser inválido.");
    }
}