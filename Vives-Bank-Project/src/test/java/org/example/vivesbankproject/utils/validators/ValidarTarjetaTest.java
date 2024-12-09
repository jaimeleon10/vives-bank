package org.example.vivesbankproject.utils.validators;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidarTarjetaTest {

    @Test
    void testValidarTarjeta_ValidNumber() {
        // Número de tarjeta válido (pasa el algoritmo Luhn)
        String tarjetaValida = "4111111111111111";
        assertTrue(ValidarTarjeta.validateTarjeta(tarjetaValida), "El número de tarjeta debería ser válido.");
    }

    @Test
    void testValidarTarjeta_InvalidNumber_LuhnFails() {
        // Número de tarjeta que no pasa el algoritmo Luhn
        String tarjetaInvalidaLuhn = "4111111111111112";
        assertFalse(ValidarTarjeta.validateTarjeta(tarjetaInvalidaLuhn), "El número de tarjeta no debería pasar el algoritmo Luhn.");
    }

    @Test
    void testValidarTarjeta_InvalidNumber_InvalidLength() {
        // Número de tarjeta con longitud incorrecta (menos de 16 dígitos)
        String tarjetaCorta = "411111111111";
        assertFalse(ValidarTarjeta.validateTarjeta(tarjetaCorta), "El número de tarjeta con longitud incorrecta debería ser inválido.");

        // Número de tarjeta con longitud incorrecta (más de 16 dígitos)
        String tarjetaLarga = "41111111111111112222";
        assertFalse(ValidarTarjeta.validateTarjeta(tarjetaLarga), "El número de tarjeta con longitud incorrecta debería ser inválido.");
    }

    @Test
    void testValidarTarjeta_InvalidNumber_NonNumericCharacters() {
        // Número de tarjeta con caracteres no numéricos
        String tarjetaConLetras = "4111abcd11111111";
        assertFalse(ValidarTarjeta.validateTarjeta(tarjetaConLetras), "El número de tarjeta con caracteres no numéricos debería ser inválido.");

        String tarjetaConSimbolos = "4111-1111-1111-1111";
        assertFalse(ValidarTarjeta.validateTarjeta(tarjetaConSimbolos), "El número de tarjeta con símbolos debería ser inválido.");
    }

    @Test
    void testValidarTarjeta_EmptyOrNull() {
        // Cadena vacía
        String tarjetaVacia = "";
        assertFalse(ValidarTarjeta.validateTarjeta(tarjetaVacia), "Una tarjeta vacía debería ser inválida.");

        // Cadena nula
        String tarjetaNula = null;
        assertFalse(ValidarTarjeta.validateTarjeta(tarjetaNula), "Una tarjeta nula debería ser inválida.");
    }
}
