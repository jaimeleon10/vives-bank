package org.example.vivesbankproject.tarjeta.repositories;

import org.example.vivesbankproject.tarjeta.models.Tarjeta;
import org.example.vivesbankproject.tarjeta.models.TipoTarjeta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TarjetaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TarjetaRepository tarjetaRepository;

    private Tarjeta tarjetaMock;
    private final LocalDateTime NOW = LocalDateTime.now();
    private final LocalDate CADUCIDAD = LocalDate.now().plusYears(10);

    @BeforeEach
    void setUp() {
        tarjetaMock = Tarjeta.builder()
                .guid("isTest")
                .numeroTarjeta("1234567890123456")
                .fechaCaducidad(CADUCIDAD)
                .cvv(123)
                .pin("123")
                .limiteDiario(new BigDecimal("1000.00"))
                .limiteSemanal(new BigDecimal("5000.00"))
                .limiteMensual(new BigDecimal("20000.00"))
                .tipoTarjeta(TipoTarjeta.DEBITO)
                .createdAt(NOW)
                .updatedAt(NOW)
                .isDeleted(false)
                .build();
    }

    @Test
    void findByGuid() {
        entityManager.persist(tarjetaMock);
        entityManager.flush();

        Optional<Tarjeta> found = tarjetaRepository.findByGuid("isTest");

        assertTrue(found.isPresent());
        assertEquals("isTest", found.get().getGuid());
        assertEquals(tarjetaMock.getNumeroTarjeta(), found.get().getNumeroTarjeta());
    }

    @Test
    void findByGuidTarjetaNotFound() {
        Optional<Tarjeta> found = tarjetaRepository.findByGuid("NoExisteId");

        assertTrue(found.isEmpty());
    }
}