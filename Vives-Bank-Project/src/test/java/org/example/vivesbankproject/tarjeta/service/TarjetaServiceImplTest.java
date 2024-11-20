package org.example.vivesbankproject.tarjeta.service;

import org.example.vivesbankproject.tarjeta.exceptions.TarjetaNotFound;
import org.example.vivesbankproject.tarjeta.models.Tarjeta;
import org.example.vivesbankproject.tarjeta.models.Tipo;
import org.example.vivesbankproject.tarjeta.models.TipoTarjeta;
import org.example.vivesbankproject.tarjeta.repositories.TarjetaRepository;
import org.example.vivesbankproject.tarjeta.repositories.TipoTarjetaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TarjetaServiceImplTest {

    @Mock
    private TarjetaRepository tarjetaRepository;

    @Mock
    private TipoTarjetaRepository tipoTarjetaRepository;

    @InjectMocks
    private TarjetaServiceImpl tarjetaService;

    private Tarjeta tarjetaTest;
    private TipoTarjeta tipoTarjetaTest;

    @BeforeEach
    void setUp() {
        tipoTarjetaTest = TipoTarjeta.builder()
                .id(UUID.randomUUID())
                .nombre(Tipo.DEBITO)
                .build();

        tarjetaTest = Tarjeta.builder()
                .id(UUID.fromString("921f6b86-695d-4361-8905-365d97691024"))
                .numeroTarjeta("4242424242424242")
                .fechaCaducidad(LocalDate.parse("2025-12-31"))
                .cvv(123)
                .pin("1234")
                .limiteDiario(100.0)
                .limiteSemanal(200.0)
                .limiteMensual(500.0)
                .tipoTarjeta(tipoTarjetaTest)
                .build();
    }

    @Test
    void getAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Tarjeta> tarjetaPage = new PageImpl<>(List.of(tarjetaTest), pageable, 1);

        when(tarjetaRepository.findAll(pageable)).thenReturn(tarjetaPage);

        Page<Tarjeta> result = tarjetaService.getAll(pageable);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1, result.getTotalElements()),
                () -> assertTrue(result.getContent().contains(tarjetaTest))
        );

        verify(tarjetaRepository).findAll(pageable);
    }

    @Test
    void getAllPaginaVacia() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Tarjeta> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(tarjetaRepository.findAll(pageable)).thenReturn(emptyPage);

        Page<Tarjeta> result = tarjetaService.getAll(pageable);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(0, result.getTotalElements()),
                () -> assertTrue(result.getContent().isEmpty())
        );

        verify(tarjetaRepository).findAll(pageable);
    }

    @Test
    void findById() {
        UUID id = tarjetaTest.getId();
        when(tarjetaRepository.findById(id)).thenReturn(Optional.of(tarjetaTest));

        Optional<Tarjeta> result = tarjetaService.getById(id);

        assertTrue(result.isPresent());
        assertEquals(tarjetaTest, result.get());

        verify(tarjetaRepository).findById(id);
    }

    @Test
    void findByIdNotFound() {
        UUID id = UUID.randomUUID();
        when(tarjetaRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(TarjetaNotFound.class, () -> tarjetaService.getById(id).orElseThrow(() -> new TarjetaNotFound(id)));

        verify(tarjetaRepository).findById(id);
    }

    @Test
    void save() {
        when(tarjetaRepository.save(tarjetaTest)).thenReturn(tarjetaTest);

        Tarjeta result = tarjetaService.save(tarjetaTest);

        assertEquals(tarjetaTest, result);

        verify(tarjetaRepository).save(tarjetaTest);
    }

    @Test
    void saveExcepcion() {
        when(tarjetaRepository.save(any(Tarjeta.class))).thenThrow(new RuntimeException("Error guardando la tarjeta"));

        assertThrows(RuntimeException.class, () -> tarjetaService.save(tarjetaTest));

        verify(tarjetaRepository).save(any(Tarjeta.class));
    }

    @Test
    void update() {
        UUID id = tarjetaTest.getId();
        Tarjeta tarjetaActualizada = Tarjeta.builder()
                .pin("5678")
                .limiteDiario(150.0)
                .build();

        when(tarjetaRepository.findById(id)).thenReturn(Optional.of(tarjetaTest));
        when(tarjetaRepository.save(any(Tarjeta.class))).thenReturn(tarjetaActualizada);

        Tarjeta result = tarjetaService.update(id, tarjetaActualizada);

        assertAll(
                () -> assertEquals("5678", result.getPin()),
                () -> assertEquals(150.0, result.getLimiteDiario())
        );

        verify(tarjetaRepository).findById(id);
        verify(tarjetaRepository).save(any(Tarjeta.class));
    }

    @Test
    void UpdateNotFound() {
        UUID id = UUID.randomUUID();
        Tarjeta tarjetaActualizada = Tarjeta.builder().build();

        when(tarjetaRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(TarjetaNotFound.class, () -> tarjetaService.update(id, tarjetaActualizada));

        verify(tarjetaRepository).findById(id);
        verify(tarjetaRepository, never()).save(any(Tarjeta.class));
    }

    @Test
    void UpdateConValoresNull() {
        UUID id = tarjetaTest.getId();
        Tarjeta tarjetaActualizada = Tarjeta.builder()
                .pin(null)
                .limiteDiario(null)
                .build();

        when(tarjetaRepository.findById(id)).thenReturn(Optional.of(tarjetaTest));
        when(tarjetaRepository.save(any(Tarjeta.class))).thenReturn(tarjetaActualizada);

        Tarjeta result = tarjetaService.update(id, tarjetaActualizada);

        assertAll(
                () -> assertNull(result.getPin()),
                () -> assertNull(result.getLimiteDiario())
        );

        verify(tarjetaRepository).findById(id);
        verify(tarjetaRepository).save(any(Tarjeta.class));
    }

    @Test
    void deleteById() {
        UUID id = tarjetaTest.getId();
        when(tarjetaRepository.findById(id)).thenReturn(Optional.of(tarjetaTest));

        Tarjeta result = tarjetaService.deleteById(id);

        assertEquals(tarjetaTest, result);

        verify(tarjetaRepository).findById(id);
        verify(tarjetaRepository).delete(tarjetaTest);
    }

    @Test
    void deleteIdNotFound() {
        UUID id = UUID.randomUUID();

        when(tarjetaRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(TarjetaNotFound.class, () -> tarjetaService.deleteById(id));

        verify(tarjetaRepository).findById(id);
        verify(tarjetaRepository, never()).delete(any());
    }

    @Test
    void getTipoTarjetaByNombre() {
        Tipo tipo = Tipo.DEBITO;
        when(tipoTarjetaRepository.findByNombre(tipo)).thenReturn(Optional.of(tipoTarjetaTest));

        TipoTarjeta result = tarjetaService.getTipoTarjetaByNombre(tipo);

        assertEquals(tipoTarjetaTest, result);

        verify(tipoTarjetaRepository).findByNombre(tipo);
    }

    @Test
    void getTipoTarjetaPorNombreNotFound() {
        Tipo tipo = Tipo.CREDITO;
        when(tipoTarjetaRepository.findByNombre(tipo)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> tarjetaService.getTipoTarjetaByNombre(tipo));

        verify(tipoTarjetaRepository).findByNombre(tipo);
    }

    @Test
    void getAllMultiplesTarjetas() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Tarjeta> tarjetas = List.of(tarjetaTest, tarjetaTest);
        Page<Tarjeta> tarjetaPage = new PageImpl<>(tarjetas, pageable, tarjetas.size());

        when(tarjetaRepository.findAll(pageable)).thenReturn(tarjetaPage);

        Page<Tarjeta> result = tarjetaService.getAll(pageable);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(2, result.getTotalElements()),
                () -> assertTrue(result.getContent().containsAll(tarjetas))
        );

        verify(tarjetaRepository).findAll(pageable);
    }

}
