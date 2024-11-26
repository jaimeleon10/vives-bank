package org.example.vivesbankproject.cuenta.repositories;

import org.example.vivesbankproject.cuenta.models.TipoCuenta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TipoCuentaRepositoryTest {

    @Autowired
    private TipoCuentaRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private TipoCuenta tipoCuentaTest;

    @BeforeEach
    void setUp() {

        tipoCuentaTest = new TipoCuenta();
        tipoCuentaTest.setGuid("hola");
        tipoCuentaTest.setNombre("normal");
        tipoCuentaTest.setInteres(BigDecimal.valueOf(2.0));

        // Guardamos el objeto en la base de datos de prueba
        entityManager.persist(tipoCuentaTest);
        entityManager.flush();
    }

    @Test
    void findByNombre() {

        Optional<TipoCuenta> result = repository.findByNombre("normal");


        assertAll(
                () -> assertTrue(result.isPresent(), "El resultado no debe estar vacío"),
                () -> assertEquals(tipoCuentaTest.getGuid(), result.get().getGuid(), "El GUID no coincide"),
                () -> assertEquals(tipoCuentaTest.getNombre(), result.get().getNombre(), "El nombre no coincide"),
                () -> assertEquals(tipoCuentaTest.getInteres(), result.get().getInteres(), "El interés no coincide")
        );
    }

    @Test
    void findByNombreNotFound() {

        Optional<TipoCuenta> result = repository.findByNombre("ahorro");

        // Validamos que no se encontró un resultado
        assertTrue(result.isEmpty(), "El resultado debe estar vacío");
    }

    @Test
    void findByGuid() {
        Optional<TipoCuenta> result = repository.findByGuid("hola");

        assertAll(
                () -> assertTrue(result.isPresent(), "El resultado no debe estar vacío"),
                () -> assertEquals(tipoCuentaTest.getGuid(), result.get().getGuid(), "El GUID no coincide"),
                () -> assertEquals(tipoCuentaTest.getNombre(), result.get().getNombre(), "El nombre no coincide")
        );
    }

    @Test
    void findByGuidNotFound() {
        Optional<TipoCuenta> result = repository.findByGuid("guid-inexistente");

        assertTrue(result.isEmpty(), "El resultado debe estar vacío");
    }
}