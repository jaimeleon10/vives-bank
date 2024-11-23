package org.example.vivesbankproject.cuenta.services;

import org.example.vivesbankproject.cuenta.dto.tipoCuenta.TipoCuentaRequest;
import org.example.vivesbankproject.cuenta.dto.tipoCuenta.TipoCuentaResponse;
import org.example.vivesbankproject.cuenta.exceptions.TipoCuentaNotFound;
import org.example.vivesbankproject.cuenta.mappers.TipoCuentaMapper;
import org.example.vivesbankproject.cuenta.models.TipoCuenta;
import org.example.vivesbankproject.cuenta.repositories.TipoCuentaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TipoCuentaServiceImplTest {
    @Mock
    private TipoCuentaRepository tipoCuentaRepository;

    @Mock
    private TipoCuentaMapper tipoCuentaMapper;

    @InjectMocks
    private TipoCuentaServiceImpl tipoCuentaService;

    private TipoCuenta tipoCuentaTest;

    @BeforeEach
    void setUp() {
        tipoCuentaTest = new TipoCuenta();
        tipoCuentaTest.setId(1L);
        tipoCuentaTest.setGuid("hola");
        tipoCuentaTest.setNombre("normal");
        tipoCuentaTest.setInteres(BigDecimal.valueOf(2.0));
    }

    @Test
    void getAll() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("nombre").ascending());

        TipoCuentaResponse tipoCuentaResponseTest = new TipoCuentaResponse();
        tipoCuentaResponseTest.setGuid(tipoCuentaTest.getGuid());
        tipoCuentaResponseTest.setNombre(tipoCuentaTest.getNombre());
        tipoCuentaResponseTest.setInteres(tipoCuentaTest.getInteres());

        Page<TipoCuenta> cuentaPage = new PageImpl<>(List.of(tipoCuentaTest), pageable, 1);
        when(tipoCuentaRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(cuentaPage);

        when(tipoCuentaMapper.toTipoCuentaResponse(tipoCuentaTest)).thenReturn(tipoCuentaResponseTest);

        var result = tipoCuentaService.getAll(
                Optional.of(tipoCuentaTest.getNombre()),
                Optional.of(tipoCuentaTest.getInteres()),
                pageable
        );

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1, result.getContent().size()),
                () -> assertEquals("normal", result.getContent().get(0).getNombre()),
                () -> assertEquals(BigDecimal.valueOf(2.0), result.getContent().get(0).getInteres())
        );

        verify(tipoCuentaRepository, times(1)).findAll(any(Specification.class), eq(pageable));
        verify(tipoCuentaMapper, times(1)).toTipoCuentaResponse(tipoCuentaTest);
    }


    @Test
    void getById() {
        String idTipoCuenta = "test";

        TipoCuentaResponse tipoCuentaResponse = new TipoCuentaResponse();
        tipoCuentaResponse.setGuid(idTipoCuenta);
        tipoCuentaResponse.setNombre("normal");
        tipoCuentaResponse.setInteres(BigDecimal.valueOf(2.0));

        when(tipoCuentaRepository.findByGuid(idTipoCuenta)).thenReturn(Optional.of(tipoCuentaTest));
        when(tipoCuentaMapper.toTipoCuentaResponse(tipoCuentaTest)).thenReturn(tipoCuentaResponse);

        TipoCuentaResponse resultTipoCuenta = tipoCuentaService.getById(idTipoCuenta);

        assertEquals(tipoCuentaResponse, resultTipoCuenta);

        verify(tipoCuentaRepository, times(1)).findByGuid(idTipoCuenta);
        verify(tipoCuentaMapper, times(1)).toTipoCuentaResponse(tipoCuentaTest);
    }

    @Test
    void getByIdNotFound() {
        String idTipoCuenta = "test2";

        when(tipoCuentaRepository.findByGuid("test2")).thenReturn(Optional.empty());

        assertThrows(TipoCuentaNotFound.class, () -> tipoCuentaService.getById(idTipoCuenta));

        verify(tipoCuentaRepository).findByGuid(idTipoCuenta);
    }

    @Test
    void save() {
        TipoCuenta tipoCuenta = new TipoCuenta();
        tipoCuenta.setGuid("hola");
        tipoCuenta.setNombre("normal");
        tipoCuenta.setInteres(BigDecimal.valueOf(2.0));

        TipoCuentaRequest tipoCuentaRequest = new TipoCuentaRequest();
        tipoCuentaRequest.setNombre("normal");
        tipoCuentaRequest.setInteres(BigDecimal.valueOf(2.0));

        TipoCuentaResponse tipoCuentaResponse = new TipoCuentaResponse();
        tipoCuentaResponse.setGuid(tipoCuenta.getGuid());
        tipoCuentaResponse.setNombre("normal");
        tipoCuentaResponse.setInteres(BigDecimal.valueOf(2.0));

        when(tipoCuentaRepository.findByNombre(tipoCuentaRequest.getNombre())).thenReturn(Optional.empty());
        when(tipoCuentaRepository.save(any(TipoCuenta.class))).thenReturn(tipoCuenta);
        when(tipoCuentaMapper.toTipoCuenta(tipoCuentaRequest)).thenReturn(tipoCuenta);
        when(tipoCuentaMapper.toTipoCuentaResponse(tipoCuenta)).thenReturn(tipoCuentaResponse);

        var result = tipoCuentaService.save(tipoCuentaRequest);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(tipoCuenta.getGuid(), result.getGuid()),
                () -> assertEquals(tipoCuenta.getNombre(), result.getNombre()),
                () -> assertEquals(tipoCuenta.getInteres(), result.getInteres())
        );

        verify(tipoCuentaRepository, times(1)).findByNombre(tipoCuentaRequest.getNombre());
        verify(tipoCuentaRepository, times(1)).save(any(TipoCuenta.class));
        verify(tipoCuentaMapper, times(1)).toTipoCuenta(tipoCuentaRequest);
        verify(tipoCuentaMapper, times(1)).toTipoCuentaResponse(tipoCuenta);
    }

    @Test
    void update() {
        String idTipoCuenta = "6c257ab6-e588-4cef-a479-c2f8fcd7379a";

        TipoCuenta tipoCuenta = new TipoCuenta();
        tipoCuenta.setGuid(idTipoCuenta);
        tipoCuenta.setNombre("normal");
        tipoCuenta.setInteres(BigDecimal.valueOf(2.0));

        TipoCuentaRequest tipoCuentaRequest = new TipoCuentaRequest();
        tipoCuenta.setGuid(idTipoCuenta);
        tipoCuenta.setNombre(tipoCuenta.getNombre());
        tipoCuenta.setInteres(BigDecimal.valueOf(3.0));

        TipoCuentaResponse tipoCuentaResponse = new TipoCuentaResponse();
        tipoCuenta.setNombre(tipoCuenta.getNombre());
        tipoCuenta.setInteres(tipoCuenta.getInteres());

        when(tipoCuentaRepository.findByGuid(idTipoCuenta)).thenReturn(Optional.of(tipoCuenta));
        when(tipoCuentaRepository.save(tipoCuenta)).thenReturn(tipoCuenta);
        when(tipoCuentaMapper.toTipoCuentaUpdate(tipoCuentaRequest, tipoCuenta)).thenReturn(tipoCuenta);
        when(tipoCuentaMapper.toTipoCuentaResponse(tipoCuenta)).thenReturn(tipoCuentaResponse);

        TipoCuentaResponse result = tipoCuentaService.update(idTipoCuenta, tipoCuentaRequest);

        assertEquals(tipoCuentaResponse, result);

        verify(tipoCuentaRepository, times(1)).findByGuid(idTipoCuenta);
        verify(tipoCuentaRepository, times(1)).save(tipoCuenta);
        verify(tipoCuentaMapper, times(1)).toTipoCuentaUpdate(tipoCuentaRequest, tipoCuenta);
        verify(tipoCuentaMapper, times(1)).toTipoCuentaResponse(tipoCuenta);
    }

    @Test
    void updateNotFound() {
        String idTipoCuenta = "4182d617-ec89-4fbc-be95-85e461778700";
        TipoCuenta tipoCuenta = new TipoCuenta();

        TipoCuentaRequest tipoCuentaRequest = new TipoCuentaRequest();
        tipoCuenta.setGuid(idTipoCuenta);
        tipoCuenta.setNombre(tipoCuenta.getNombre());
        tipoCuenta.setInteres(BigDecimal.valueOf(3.0));

        when(tipoCuentaRepository.findByGuid(idTipoCuenta)).thenReturn(Optional.empty());

        assertThrows(TipoCuentaNotFound.class, () -> tipoCuentaService.update(idTipoCuenta, tipoCuentaRequest));

        verify(tipoCuentaRepository).findByGuid(idTipoCuenta);
        verify(tipoCuentaRepository, never()).save(tipoCuenta);
    }

    @Test
    void deleteById() {
        String idCuenta = "hola";

        TipoCuenta tipoCuenta = new TipoCuenta();
        tipoCuenta.setGuid(idCuenta);
        tipoCuenta.setNombre("ahorro");
        tipoCuenta.setInteres(BigDecimal.valueOf(3.0));

        when(tipoCuentaRepository.findByGuid(idCuenta)).thenReturn(Optional.of(tipoCuenta));

        tipoCuentaService.deleteById(idCuenta);

        verify(tipoCuentaRepository, times(1)).findByGuid(idCuenta);
    }
}