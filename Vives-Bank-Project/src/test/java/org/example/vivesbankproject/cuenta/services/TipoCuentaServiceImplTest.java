package org.example.vivesbankproject.cuenta.services;

import org.example.vivesbankproject.cuenta.exceptions.TipoCuentaNotFound;
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

    @InjectMocks
    private TipoCuentaServiceImpl tipoCuentaService;

    private TipoCuenta tipoCuentaTest;

    @BeforeEach
    void setUp() {
        tipoCuentaTest = new TipoCuenta();
        tipoCuentaTest.setId(1L);
        tipoCuentaTest.setNombre("normal");
        tipoCuentaTest.setInteres(BigDecimal.valueOf(2.0));
    }

    @Test
    void getAll() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("nombre").ascending());

        Page<TipoCuenta> cuentaPage = new PageImpl<>(List.of(tipoCuentaTest), pageable, 1);

        when(tipoCuentaRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(cuentaPage);

        var result = tipoCuentaService.getAll(Optional.of(tipoCuentaTest.getNombre()), Optional.of(tipoCuentaTest.getInteres()), pageable);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1, result.getContent().size()),
                () -> assertTrue(result.getContent().contains(tipoCuentaTest)),
                () -> assertEquals("normal", result.getContent().getFirst().getNombre()),
                () -> assertEquals(BigDecimal.valueOf(2.0), result.getContent().getFirst().getInteres())
        );

        verify(tipoCuentaRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getById() {
        String idTipoCuenta = "test";

        when(tipoCuentaRepository.findById(idTipoCuenta)).thenReturn(Optional.of(tipoCuentaTest));

        TipoCuenta resultTipoCuenta = tipoCuentaService.getById(idTipoCuenta);

        assertEquals(tipoCuentaTest, resultTipoCuenta);

        verify(tipoCuentaRepository, times(1)).findById(idTipoCuenta);
    }

    @Test
    void getByIdNotFound() {
        String idTipoCuenta = "test2";

        when(tipoCuentaRepository.findById("test2")).thenReturn(Optional.empty());

        assertThrows(TipoCuentaNotFound.class, () -> tipoCuentaService.getById(idTipoCuenta));

        verify(tipoCuentaRepository).findById(idTipoCuenta);
    }

    @Test
    void save() {
        TipoCuenta tipoCuenta = new TipoCuenta();
        tipoCuenta.setNombre("normal");
        tipoCuenta.setInteres(BigDecimal.valueOf(2.0));

        when(tipoCuentaRepository.findByNombre(tipoCuenta.getNombre())).thenReturn(Optional.empty());
        when(tipoCuentaRepository.save(tipoCuenta)).thenReturn(tipoCuenta);

        var result = tipoCuentaService.save(tipoCuenta);

        assertAll(
                () -> assertEquals(tipoCuenta.getId(), result.getId()),
                () -> assertEquals(tipoCuenta.getGuid(), result.getGuid()),
                () -> assertEquals(tipoCuenta.getNombre(), result.getNombre()),
                () -> assertEquals(tipoCuenta.getInteres(), result.getInteres())
        );

        verify(tipoCuentaRepository, times(1)).findByNombre(tipoCuenta.getNombre());
        verify(tipoCuentaRepository, times(1)).save(tipoCuenta);
    }

    @Test
    void update() {
        String idTipoCuenta = "6c257ab6-e588-4cef-a479-c2f8fcd7379a";

        TipoCuenta tipoCuenta = new TipoCuenta();
        tipoCuenta.setGuid(idTipoCuenta);
        tipoCuenta.setNombre("normal");
        tipoCuenta.setInteres(BigDecimal.valueOf(2.0));

        when(tipoCuentaRepository.findById(idTipoCuenta)).thenReturn(Optional.of(tipoCuenta));
        when(tipoCuentaRepository.save(tipoCuenta)).thenReturn(tipoCuenta);

        TipoCuenta result = tipoCuentaService.update(idTipoCuenta, tipoCuenta);

        assertEquals(tipoCuenta, result);

        verify(tipoCuentaRepository, times(1)).findById(idTipoCuenta);
        verify(tipoCuentaRepository, times(1)).save(tipoCuenta);
    }

    @Test
    void updateNotFound() {
        String idCuenta = "4182d617-ec89-4fbc-be95-85e461778700";
        TipoCuenta tipoCuenta = new TipoCuenta();

        when(tipoCuentaRepository.findById(idCuenta)).thenReturn(Optional.empty());

        assertThrows(TipoCuentaNotFound.class, () -> tipoCuentaService.update(idCuenta, tipoCuenta));

        verify(tipoCuentaRepository).findById(idCuenta);
        verify(tipoCuentaRepository, never()).save(tipoCuenta);
    }

    @Test
    void deleteById() {
        String idCuenta = "hola";

        TipoCuenta tipoCuenta = new TipoCuenta();
        tipoCuenta.setGuid(idCuenta);
        tipoCuenta.setNombre("ahorro");
        tipoCuenta.setInteres(BigDecimal.valueOf(3.0));

        when(tipoCuentaRepository.findById(idCuenta)).thenReturn(Optional.of(tipoCuenta));

        tipoCuentaService.deleteById(idCuenta);

        verify(tipoCuentaRepository, times(1)).findById(idCuenta);
    }
}