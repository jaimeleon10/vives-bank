package org.example.vivesbankproject.utils.generators;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class IdGeneratorTest {

    @Test
    void generarId_longitudCorrecta() {
        String id = IdGenerator.generarId();
        assertEquals(11, id.length(), "El ID debe tener 11 caracteres");
    }

    @Test
    void generarId_soloAlfanumericos() {
        String id = IdGenerator.generarId();
        assertTrue(id.matches("[a-zA-Z0-9]+"), "El ID debe contener solo caracteres alfanuméricos");
    }

    @Test
    void generarId_aleatoriedad() {
        Set<String> ids = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            ids.add(IdGenerator.generarId());
        }
        assertEquals(1000, ids.size(), "Se han generado IDs duplicados");
    }

    @Test
    void generarId_semillaAfecta() {
        // Simular un paso de tiempo significativo
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String id1 = IdGenerator.generarId();
        String id2 = IdGenerator.generarId();
        assertNotEquals(id1, id2, "Los IDs generados con diferentes semillas deben ser diferentes");
    }
}