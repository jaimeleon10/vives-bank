package org.example.vivesbankproject.tarjeta.service;

import org.example.vivesbankproject.tarjeta.dto.TarjetaRequest;
import org.example.vivesbankproject.tarjeta.dto.TarjetaResponse;
import org.example.vivesbankproject.tarjeta.exceptions.TarjetaNotFound;
import org.example.vivesbankproject.tarjeta.mappers.TarjetaMapper;
import org.example.vivesbankproject.tarjeta.models.Tarjeta;
import org.example.vivesbankproject.tarjeta.models.Tipo;
import org.example.vivesbankproject.tarjeta.models.TipoTarjeta;
import org.example.vivesbankproject.tarjeta.repositories.TarjetaRepository;
import org.example.vivesbankproject.tarjeta.repositories.TipoTarjetaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
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

    @Mock
    private TarjetaMapper tarjetaMapper;

    @InjectMocks
    private TarjetaServiceImpl tarjetaService;

    private Tarjeta tarjeta;
    private TarjetaRequest tarjetaRequest;
    private TarjetaResponse tarjetaResponse;
    private UUID tarjetaId;

    @BeforeEach
    void setUp() {
        tarjetaId = UUID.randomUUID();
        tarjeta = new Tarjeta();
        tarjeta.setId(tarjetaId);
        tarjeta.setNumeroTarjeta("1234567890123456");

        tarjetaRequest = TarjetaRequest.builder()
                .numeroTarjeta("1234567890123456")
                .cvv(123)
                .pin("1234")
                .fechaCaducidad(LocalDate.now().plusYears(3))
                .limiteDiario(BigDecimal.valueOf(1000))
                .limiteSemanal(BigDecimal.valueOf(5000))
                .limiteMensual(BigDecimal.valueOf(20000))
                .tipoTarjeta("CREDITO")
                .cuentaId(UUID.randomUUID())
                .build();

        tarjetaResponse = TarjetaResponse.builder()
                .id(tarjetaId)
                .numeroTarjeta("1234567890123456")
                .tipoTarjeta("CREDITO")
                .build();
    }

    @Test
    void testSaveTarjeta() {
        when(tarjetaMapper.toTarjeta(tarjetaRequest)).thenReturn(tarjeta);
        when(tarjetaRepository.save(tarjeta)).thenReturn(tarjeta);
        when(tarjetaMapper.toTarjetaResponse(tarjeta)).thenReturn(tarjetaResponse);

        TarjetaResponse resultado = tarjetaService.save(tarjetaRequest);

        assertNotNull(resultado);
        assertEquals(tarjetaId, resultado.getId());
        assertEquals("1234567890123456", resultado.getNumeroTarjeta());

        verify(tarjetaRepository).save(tarjeta);
    }

    @Test
    void testGetById() {
        when(tarjetaRepository.findById(tarjetaId)).thenReturn(Optional.of(tarjeta));
        when(tarjetaMapper.toTarjetaResponse(tarjeta)).thenReturn(tarjetaResponse);

        Optional<TarjetaResponse> resultado = tarjetaService.getById(tarjetaId);

        assertTrue(resultado.isPresent());
        assertEquals(tarjetaResponse, resultado.get());
    }

    @Test
    void testGetById_NotFound() {
        when(tarjetaRepository.findById(tarjetaId)).thenReturn(Optional.empty());

        Optional<TarjetaResponse> resultado = tarjetaService.getById(tarjetaId);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void testUpdateTarjeta() {
        when(tarjetaRepository.findById(tarjetaId)).thenReturn(Optional.of(tarjeta));
        when(tarjetaMapper.toTarjeta(tarjetaRequest)).thenReturn(tarjeta);
        when(tarjetaRepository.save(tarjeta)).thenReturn(tarjeta);
        when(tarjetaMapper.toTarjetaResponse(tarjeta)).thenReturn(tarjetaResponse);

        TarjetaResponse resultado = tarjetaService.update(tarjetaId, tarjetaRequest);

        assertNotNull(resultado);
        assertEquals(tarjetaId, resultado.getId());
        verify(tarjetaRepository).save(tarjeta);
    }

    @Test
    void testUpdateTarjeta_NotFound() {
        when(tarjetaRepository.findById(tarjetaId)).thenReturn(Optional.empty());

        assertThrows(TarjetaNotFound.class, () ->
                tarjetaService.update(tarjetaId, tarjetaRequest)
        );
    }

    @Test
    void testDeleteTarjeta() {
        when(tarjetaRepository.findById(tarjetaId)).thenReturn(Optional.of(tarjeta));
        when(tarjetaMapper.toTarjetaResponse(tarjeta)).thenReturn(tarjetaResponse);

        TarjetaResponse resultado = tarjetaService.deleteById(tarjetaId);

        assertNotNull(resultado);
        verify(tarjetaRepository).delete(tarjeta);
    }

    @Test
    void testDeleteTarjeta_NotFound() {
        when(tarjetaRepository.findById(tarjetaId)).thenReturn(Optional.empty());

        assertThrows(TarjetaNotFound.class, () ->
                tarjetaService.deleteById(tarjetaId)
        );
    }

    @Test
    void testGetTipoTarjetaByNombre() {
        TipoTarjeta tipoTarjeta = new TipoTarjeta();
        tipoTarjeta.setNombre(Tipo.CREDITO);

        when(tipoTarjetaRepository.findByNombre(Tipo.CREDITO))
                .thenReturn(Optional.of(tipoTarjeta));

        TipoTarjeta resultado = tarjetaService.getTipoTarjetaByNombre(Tipo.CREDITO);

        assertNotNull(resultado);
        assertEquals(Tipo.CREDITO, resultado.getNombre());
    }

    @Test
    void testGetTipoTarjetaByNombre_NotFound() {
        when(tipoTarjetaRepository.findByNombre(Tipo.CREDITO))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                tarjetaService.getTipoTarjetaByNombre(Tipo.CREDITO)
        );
    }
}