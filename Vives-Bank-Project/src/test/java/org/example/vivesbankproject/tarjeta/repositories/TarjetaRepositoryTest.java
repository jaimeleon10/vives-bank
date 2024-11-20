package org.example.vivesbankproject.tarjeta.repositories;

import org.example.vivesbankproject.tarjeta.models.Tarjeta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DataJpaTest
public class TarjetaRepositoryTest {
/*
    @Mock
    private TarjetaRepository tarjetaRepository;

    @InjectMocks
    private Tarjeta tarjeta;

    private UUID tarjetaId;

    @BeforeEach
    public void setUp() {
        tarjetaId = UUID.randomUUID();
        tarjeta = new Tarjeta();
        tarjeta.setId(tarjetaId);
        tarjeta.setNumeroTarjeta("1234567890123456");
        tarjeta.setFechaCaducidad(java.time.LocalDate.of(2025, 12, 31));
        tarjeta.setCvv(123);
        tarjeta.setPin("1234");
        tarjeta.setLimiteDiario(5000.0);
        tarjeta.setLimiteSemanal(20000.0);
        tarjeta.setLimiteMensual(50000.0);
        tarjeta.setTipoTarjeta(new org.example.vivesbankproject.tarjeta.models.TipoTarjeta());
    }

    @Test
    public void testFindById() {
        when(tarjetaRepository.findById(tarjetaId)).thenReturn(Optional.of(tarjeta));

        Optional<Tarjeta> foundTarjeta = tarjetaRepository.findById(tarjetaId);

        assertTrue(foundTarjeta.isPresent());
        assertEquals(tarjetaId, foundTarjeta.get().getId());
    }

    @Test
    public void testFindByIdNotFound() {
        when(tarjetaRepository.findById(tarjetaId)).thenReturn(Optional.empty());

        Optional<Tarjeta> foundTarjeta = tarjetaRepository.findById(tarjetaId);

        assertFalse(foundTarjeta.isPresent());
    }

    @Test
    public void testSave() {
        when(tarjetaRepository.save(tarjeta)).thenReturn(tarjeta);

        Tarjeta savedTarjeta = tarjetaRepository.save(tarjeta);

        assertNotNull(savedTarjeta);
        assertEquals(tarjeta.getId(), savedTarjeta.getId());
    }

    @Test
    public void testDelete() {
        doNothing().when(tarjetaRepository).deleteById(tarjetaId);

        tarjetaRepository.deleteById(tarjetaId);

        verify(tarjetaRepository, times(1)).deleteById(tarjetaId);
    }

    @Test
    public void testDeleteNotFound() {
        doThrow(EmptyResultDataAccessException.class).when(tarjetaRepository).deleteById(tarjetaId);

        assertThrows(EmptyResultDataAccessException.class, () -> {
            tarjetaRepository.deleteById(tarjetaId);
        });
    }*/
}
