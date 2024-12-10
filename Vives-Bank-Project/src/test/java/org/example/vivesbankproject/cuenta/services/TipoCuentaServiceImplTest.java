package org.example.vivesbankproject.cuenta.services;

import org.example.vivesbankproject.rest.cuenta.dto.tipoCuenta.TipoCuentaRequest;
import org.example.vivesbankproject.rest.cuenta.dto.tipoCuenta.TipoCuentaResponse;
import org.example.vivesbankproject.rest.cuenta.exceptions.tipoCuenta.TipoCuentaExists;
import org.example.vivesbankproject.rest.cuenta.exceptions.tipoCuenta.TipoCuentaNotFound;
import org.example.vivesbankproject.rest.cuenta.mappers.TipoCuentaMapper;
import org.example.vivesbankproject.rest.cuenta.models.TipoCuenta;
import org.example.vivesbankproject.rest.cuenta.repositories.TipoCuentaRepository;
import org.example.vivesbankproject.rest.cuenta.services.TipoCuentaServiceImpl;
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
public class TipoCuentaServiceImplTest {

    @Mock
    private TipoCuentaRepository tipoCuentaRepository;

    @Mock
    private TipoCuentaMapper tipoCuentaMapper;

    @InjectMocks
    private TipoCuentaServiceImpl tipoCuentaService;

    @Test
    void getAll() {
        TipoCuenta tipoCuenta1 = new TipoCuenta();
        tipoCuenta1.setNombre("Cuenta A");
        TipoCuenta tipoCuenta2 = new TipoCuenta();
        tipoCuenta2.setNombre("Cuenta B");

        List<TipoCuenta> tipoCuentas = List.of(tipoCuenta1, tipoCuenta2);
        Pageable pageable = mock(Pageable.class);
        Page<TipoCuenta> page = new PageImpl<>(tipoCuentas);

        when(tipoCuentaRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(tipoCuentaMapper.toTipoCuentaResponse(any())).thenReturn(new TipoCuentaResponse());

        Page<TipoCuentaResponse> result = tipoCuentaService.getAll(Optional.empty(), Optional.empty(),Optional.empty(), pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(tipoCuentaRepository).findAll(any(Specification.class), eq(pageable)); // Verificar la llamada al repositorio
    }
    @Test
    void getById() {
        String tipoCuentaId = "123";
        TipoCuenta tipoCuenta = new TipoCuenta();
        tipoCuenta.setGuid(tipoCuentaId);

        when(tipoCuentaRepository.findByGuid(tipoCuentaId)).thenReturn(Optional.of(tipoCuenta));
        when(tipoCuentaMapper.toTipoCuentaResponse(tipoCuenta)).thenReturn(new TipoCuentaResponse());

        TipoCuentaResponse result = tipoCuentaService.getById(tipoCuentaId);

        assertNotNull(result);
        verify(tipoCuentaRepository).findByGuid(tipoCuentaId);
    }

    @Test
    void getById_notFound() {
        String tipoCuentaId = "123";

        when(tipoCuentaRepository.findByGuid(tipoCuentaId)).thenReturn(Optional.empty());

        assertThrows(TipoCuentaNotFound.class, () -> tipoCuentaService.getById(tipoCuentaId));
    }

    @Test
    void save() {
        TipoCuentaRequest tipoCuentaRequest = new TipoCuentaRequest();
        tipoCuentaRequest.setNombre("Cuenta A");

        TipoCuenta tipoCuenta = new TipoCuenta();
        tipoCuenta.setNombre("Cuenta A");

        TipoCuentaResponse tipoCuentaResponse = new TipoCuentaResponse();

        when(tipoCuentaRepository.findByNombre(tipoCuentaRequest.getNombre())).thenReturn(Optional.empty());
        when(tipoCuentaMapper.toTipoCuenta(tipoCuentaRequest)).thenReturn(tipoCuenta);
        when(tipoCuentaRepository.save(tipoCuenta)).thenReturn(tipoCuenta);
        when(tipoCuentaMapper.toTipoCuentaResponse(tipoCuenta)).thenReturn(tipoCuentaResponse);

        TipoCuentaResponse result = tipoCuentaService.save(tipoCuentaRequest);

        assertNotNull(result);
        verify(tipoCuentaRepository).save(tipoCuenta);
    }

    @Test
    void update() {
        String tipoCuentaId = "123";
        TipoCuentaRequest tipoCuentaRequest = new TipoCuentaRequest();
        TipoCuenta tipoCuenta = new TipoCuenta();
        tipoCuenta.setGuid(tipoCuentaId);

        TipoCuenta tipoCuentaUpdated = new TipoCuenta();
        tipoCuentaUpdated.setGuid(tipoCuentaId);

        TipoCuentaResponse tipoCuentaResponse = new TipoCuentaResponse();

        when(tipoCuentaRepository.findByGuid(tipoCuentaId)).thenReturn(Optional.of(tipoCuenta));
        when(tipoCuentaMapper.toTipoCuentaUpdate(tipoCuentaRequest, tipoCuenta)).thenReturn(tipoCuentaUpdated);
        when(tipoCuentaRepository.save(tipoCuentaUpdated)).thenReturn(tipoCuentaUpdated);
        when(tipoCuentaMapper.toTipoCuentaResponse(tipoCuentaUpdated)).thenReturn(tipoCuentaResponse);

        TipoCuentaResponse result = tipoCuentaService.update(tipoCuentaId, tipoCuentaRequest);

        assertNotNull(result);
        verify(tipoCuentaRepository).save(tipoCuentaUpdated);
    }

    @Test
    void update_notFound() {
        String tipoCuentaId = "123";
        TipoCuentaRequest tipoCuentaRequest = new TipoCuentaRequest();

        when(tipoCuentaRepository.findByGuid(tipoCuentaId)).thenReturn(Optional.empty());

        assertThrows(TipoCuentaNotFound.class, () -> tipoCuentaService.update(tipoCuentaId, tipoCuentaRequest));
    }

    @Test
    void deleteById() {
        String tipoCuentaId = "123";
        TipoCuenta tipoCuenta = new TipoCuenta();
        tipoCuenta.setGuid(tipoCuentaId);

        when(tipoCuentaRepository.findByGuid(tipoCuentaId)).thenReturn(Optional.of(tipoCuenta));

        tipoCuentaService.deleteById(tipoCuentaId);

        verify(tipoCuentaRepository).save(tipoCuenta);
    }

    @Test
    void deleteById_notFound() {
        String tipoCuentaId = "123";

        when(tipoCuentaRepository.findByGuid(tipoCuentaId)).thenReturn(Optional.empty());

        assertThrows(TipoCuentaNotFound.class, () -> tipoCuentaService.deleteById(tipoCuentaId));
    }

    @Test
    void save_tipoCuentaExistente() {
        TipoCuentaRequest tipoCuentaRequest = new TipoCuentaRequest();
        tipoCuentaRequest.setNombre("Existing Account");

        when(tipoCuentaRepository.findByNombre(tipoCuentaRequest.getNombre()))
                .thenReturn(Optional.of(new TipoCuenta()));

        assertThrows(TipoCuentaExists.class, () -> {
            tipoCuentaService.save(tipoCuentaRequest);
        });

        verify(tipoCuentaRepository, never()).save(any());
    }

    @Test
    void getAll_conFiltrado() {
        Optional<String> nombre = Optional.of("Test");
        Optional<BigDecimal> interesMax = Optional.of(BigDecimal.valueOf(5.0));
        Optional<BigDecimal> interesMin = Optional.of(BigDecimal.valueOf(1.0));
        Pageable pageable = PageRequest.of(0, 10);

        TipoCuenta tipoCuenta = new TipoCuenta();
        Page<TipoCuenta> page = new PageImpl<>(java.util.List.of(tipoCuenta));

        when(tipoCuentaRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);

        when(tipoCuentaMapper.toTipoCuentaResponse(any(TipoCuenta.class)))
                .thenReturn(new TipoCuentaResponse());

        Page<TipoCuentaResponse> result = tipoCuentaService.getAll(
                nombre, interesMax, interesMin, pageable
        );

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(tipoCuentaRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void save_masCampos() {
        TipoCuentaRequest tipoCuentaRequest = new TipoCuentaRequest();
        tipoCuentaRequest.setNombre("New Account");
        tipoCuentaRequest.setInteres(BigDecimal.valueOf(3.5));

        TipoCuenta tipoCuenta = new TipoCuenta();
        tipoCuenta.setNombre(tipoCuentaRequest.getNombre());
        tipoCuenta.setInteres(tipoCuentaRequest.getInteres());

        TipoCuentaResponse expectedResponse = new TipoCuentaResponse();

        when(tipoCuentaRepository.findByNombre(tipoCuentaRequest.getNombre()))
                .thenReturn(Optional.empty());

        when(tipoCuentaMapper.toTipoCuenta(tipoCuentaRequest)).thenReturn(tipoCuenta);
        when(tipoCuentaRepository.save(tipoCuenta)).thenReturn(tipoCuenta);
        when(tipoCuentaMapper.toTipoCuentaResponse(tipoCuenta)).thenReturn(expectedResponse);

        TipoCuentaResponse result = tipoCuentaService.save(tipoCuentaRequest);

        assertNotNull(result);
        verify(tipoCuentaRepository).save(tipoCuenta);
    }

    @Test
    void deleteById_verificandoLogica() {
        String tipoCuentaId = "123";
        TipoCuenta tipoCuenta = new TipoCuenta();
        tipoCuenta.setGuid(tipoCuentaId);
        tipoCuenta.setIsDeleted(false);

        TipoCuentaResponse expectedResponse = new TipoCuentaResponse();

        when(tipoCuentaRepository.findByGuid(tipoCuentaId))
                .thenReturn(Optional.of(tipoCuenta));

        when(tipoCuentaRepository.save(tipoCuenta)).thenReturn(tipoCuenta);

        when(tipoCuentaMapper.toTipoCuentaResponse(tipoCuenta)).thenReturn(expectedResponse);

        TipoCuentaResponse result = tipoCuentaService.deleteById(tipoCuentaId);

        assertTrue(tipoCuenta.getIsDeleted());
        assertNotNull(result);
        verify(tipoCuentaRepository).save(tipoCuenta);
    }

    @Test
    void update_minimosCampos() {
        String tipoCuentaId = "123";
        TipoCuentaRequest tipoCuentaRequest = new TipoCuentaRequest();
        tipoCuentaRequest.setNombre("Updated Account");

        TipoCuenta existingTipoCuenta = new TipoCuenta();
        existingTipoCuenta.setGuid(tipoCuentaId);

        TipoCuenta updatedTipoCuenta = new TipoCuenta();
        updatedTipoCuenta.setGuid(tipoCuentaId);
        updatedTipoCuenta.setNombre(tipoCuentaRequest.getNombre());

        TipoCuentaResponse expectedResponse = new TipoCuentaResponse();

        when(tipoCuentaRepository.findByGuid(tipoCuentaId))
                .thenReturn(Optional.of(existingTipoCuenta));

        when(tipoCuentaMapper.toTipoCuentaUpdate(tipoCuentaRequest, existingTipoCuenta))
                .thenReturn(updatedTipoCuenta);
        when(tipoCuentaRepository.save(updatedTipoCuenta)).thenReturn(updatedTipoCuenta);
        when(tipoCuentaMapper.toTipoCuentaResponse(updatedTipoCuenta)).thenReturn(expectedResponse);

        TipoCuentaResponse result = tipoCuentaService.update(tipoCuentaId, tipoCuentaRequest);

        assertNotNull(result);
        verify(tipoCuentaRepository).save(updatedTipoCuenta);
    }
}