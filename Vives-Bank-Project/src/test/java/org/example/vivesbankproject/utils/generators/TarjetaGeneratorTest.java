package org.example.vivesbankproject.utils.generators;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TarjetaGeneratorTest {

    @Test
    void generarTarjeta_longitudCorrecta() {
        String tarjeta = TarjetaGenerator.generarTarjeta();
        assertEquals(15, tarjeta.length(), "El número de tarjeta debe tener 16 dígitos");
    }

    @Test
    void generarTarjeta_comienzaConCuatro() {
        String tarjeta = TarjetaGenerator.generarTarjeta();
        assertEquals('4', tarjeta.charAt(0), "El número de tarjeta debe comenzar con 4");
    }

    @Test
    void generarTarjeta_digitoDeControlCorrecto() {
        String tarjeta = TarjetaGenerator.generarTarjeta();
        String sinDigitoControl = tarjeta.substring(0, tarjeta.length() - 1);
        int digitoCalculado = TarjetaGenerator.calculoLuhn(sinDigitoControl);
        int digitoTarjeta = Character.getNumericValue(tarjeta.charAt(tarjeta.length() - 1));
        assertEquals(digitoCalculado, digitoTarjeta, "El dígito de control no coincide");
    }

    @Test
    void generarTarjeta_numerosDistintos() {
        String tarjeta1 = TarjetaGenerator.generarTarjeta();
        String tarjeta2 = TarjetaGenerator.generarTarjeta();
        assertNotEquals(tarjeta1, tarjeta2, "Los números de tarjeta generados deben ser diferentes");
    }
}