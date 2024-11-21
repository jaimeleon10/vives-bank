package org.example.vivesbankproject.tarjeta.service;

import org.example.vivesbankproject.tarjeta.dto.TarjetaRequestSave;
import org.example.vivesbankproject.tarjeta.dto.TarjetaResponse;
import org.example.vivesbankproject.tarjeta.exceptions.TarjetaNotFound;
import org.example.vivesbankproject.tarjeta.mappers.TarjetaMapper;
import org.example.vivesbankproject.tarjeta.models.Tarjeta;
import org.example.vivesbankproject.tarjeta.models.TipoTarjeta;
import org.example.vivesbankproject.tarjeta.repositories.TarjetaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    private TarjetaMapper tarjetaMapper;

    @InjectMocks
    private TarjetaServiceImpl tarjetaService;

    private Tarjeta tarjeta;
    private TarjetaRequestSave tarjetaRequestSave;
    private TarjetaResponse tarjetaResponse;
    private UUID tarjetaId;

    @BeforeEach
    void setUp() {
        tarjetaId = UUID.randomUUID();
        tarjeta = new Tarjeta();
        tarjeta.setId(tarjetaId);
        tarjeta.setNumeroTarjeta("1234567890123456");

        tarjetaRequestSave = TarjetaRequestSave.builder()
                .numeroTarjeta("1234567890123456")
                .cvv(123)
                .pin("1234")
                .fechaCaducidad(LocalDate.now().plusYears(3))
                .limiteDiario(BigDecimal.valueOf(1000))
                .limiteSemanal(BigDecimal.valueOf(5000))
                .limiteMensual(BigDecimal.valueOf(20000))
                .tipoTarjeta(TipoTarjeta.CREDITO)
                .build();

        tarjetaResponse = TarjetaResponse.builder()
                .id(tarjetaId)
                .numeroTarjeta("1234567890123456")
                .tipoTarjeta(TipoTarjeta.CREDITO)
                .build();
    }

    @Test
    void SaveTarjeta() {
        when(tarjetaMapper.toTarjeta(tarjetaRequestSave)).thenReturn(tarjeta);
        when(tarjetaRepository.save(tarjeta)).thenReturn(tarjeta);
        when(tarjetaMapper.toTarjetaResponse(tarjeta)).thenReturn(tarjetaResponse);

        TarjetaResponse resultado = tarjetaService.save(tarjetaRequestSave);

        assertNotNull(resultado);
        assertEquals(tarjetaId, resultado.getId());
        assertEquals("1234567890123456", resultado.getNumeroTarjeta());

        verify(tarjetaRepository).save(tarjeta);
    }

    @Test
    void GetById() {
        when(tarjetaRepository.findById(tarjetaId)).thenReturn(Optional.of(tarjeta));
        when(tarjetaMapper.toTarjetaResponse(tarjeta)).thenReturn(tarjetaResponse);

        Optional<TarjetaResponse> resultado = Optional.ofNullable(tarjetaService.getById(tarjetaId));

        assertTrue(resultado.isPresent());
        assertEquals(tarjetaResponse, resultado.get());
    }

    @Test
    void GetById_NotFound() {
        when(tarjetaRepository.findById(tarjetaId)).thenReturn(Optional.empty());

        Optional<TarjetaResponse> resultado = Optional.ofNullable(tarjetaService.getById(tarjetaId));

        assertTrue(resultado.isEmpty());
    }

    @Test
    void UpdateTarjeta() {
        when(tarjetaRepository.findById(tarjetaId)).thenReturn(Optional.of(tarjeta));
        when(tarjetaMapper.toTarjeta(tarjetaRequestSave)).thenReturn(tarjeta);
        when(tarjetaRepository.save(tarjeta)).thenReturn(tarjeta);
        when(tarjetaMapper.toTarjetaResponse(tarjeta)).thenReturn(tarjetaResponse);

        TarjetaResponse resultado = tarjetaService.update(tarjetaId, tarjetaRequestSave);

        assertNotNull(resultado);
        assertEquals(tarjetaId, resultado.getId());
        verify(tarjetaRepository).save(tarjeta);
    }

    @Test
    void UpdateTarjeta_NotFound() {
        when(tarjetaRepository.findById(tarjetaId)).thenReturn(Optional.empty());

        assertThrows(TarjetaNotFound.class, () ->
                tarjetaService.update(tarjetaId, tarjetaRequestSave)
        );
    }

    @Test
    void DeleteTarjeta() {
        when(tarjetaRepository.findById(tarjetaId)).thenReturn(Optional.of(tarjeta));
        when(tarjetaMapper.toTarjetaResponse(tarjeta)).thenReturn(tarjetaResponse);

        TarjetaResponse resultado = tarjetaService.deleteById(tarjetaId);

        assertNotNull(resultado);
        verify(tarjetaRepository).delete(tarjeta);
    }

    @Test
    void DeleteTarjeta_NotFound() {
        when(tarjetaRepository.findById(tarjetaId)).thenReturn(Optional.empty());

        assertThrows(TarjetaNotFound.class, () ->
                tarjetaService.deleteById(tarjetaId)
        );
    }
}