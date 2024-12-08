package org.example.vivesbankproject.tarjeta.repositories;

import org.example.vivesbankproject.rest.tarjeta.models.Tarjeta;
import org.example.vivesbankproject.rest.tarjeta.models.TipoTarjeta;
import org.example.vivesbankproject.rest.tarjeta.repositories.TarjetaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TarjetaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TarjetaRepository tarjetaRepository;

    private Tarjeta tarjetaMock;

    @BeforeEach
    void setUp() {
        tarjetaMock = Tarjeta.builder()
                .guid("isTest")
                .numeroTarjeta("1234567890123456")
                .fechaCaducidad(LocalDate.now().plusYears(10))
                .cvv(123)
                .pin("1234")
                .limiteDiario(new BigDecimal("1000.00"))
                .limiteSemanal(new BigDecimal("5000.00"))
                .limiteMensual(new BigDecimal("20000.00"))
                .tipoTarjeta(TipoTarjeta.DEBITO)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
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