package org.example.vivesbankproject.cuenta.repositories;

import org.example.vivesbankproject.cuenta.models.TipoCuenta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TipoCuentaRepositoryTest {

    @Autowired
    private TipoCuentaRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private TipoCuenta tipoCuentaTest = new TipoCuenta();

    @BeforeEach
    void setUp() {
        tipoCuentaTest.setGuid("hola");
        tipoCuentaTest.setNombre("normal");
        tipoCuentaTest.setInteres(BigDecimal.valueOf(2.0));

        tipoCuentaTest = entityManager.merge(tipoCuentaTest);
        entityManager.flush();
    }

    @Test
    void findByNombre() {
        var result = repository.findByNombre("normal");

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(tipoCuentaTest.getGuid(), result.get().getGuid()),
                () -> assertEquals(tipoCuentaTest.getNombre(), result.get().getNombre()),
                () -> assertEquals(tipoCuentaTest.getInteres(), result.get().getInteres())
        );
    }

    @Test
    void findByNombreNotFound() {
        var result = repository.findByNombre("ahorro");

        assertNull(result.orElse(null));
    }
}