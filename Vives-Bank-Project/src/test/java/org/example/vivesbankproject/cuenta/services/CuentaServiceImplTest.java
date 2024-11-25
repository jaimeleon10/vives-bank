package org.example.vivesbankproject.cuenta.services;

import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaRequest;
import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaRequestUpdate;
import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaResponse;
import org.example.vivesbankproject.cuenta.dto.tipoCuenta.TipoCuentaResponse;
import org.example.vivesbankproject.cuenta.exceptions.CuentaNotFound;
import org.example.vivesbankproject.cuenta.mappers.CuentaMapper;
import org.example.vivesbankproject.cuenta.mappers.TipoCuentaMapper;
import org.example.vivesbankproject.cuenta.models.Cuenta;
import org.example.vivesbankproject.cuenta.models.TipoCuenta;
import org.example.vivesbankproject.cuenta.repositories.CuentaRepository;
import org.example.vivesbankproject.cuenta.repositories.TipoCuentaRepository;
import org.example.vivesbankproject.tarjeta.dto.TarjetaResponse;
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
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

 /*@ExtendWith(MockitoExtension.class)
class CuentaServiceImplTest {
    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private CuentaMapper cuentaMapper;

    @Mock
    private TipoCuentaMapper tipoCuentaMapper;

    @Mock
    private TarjetaMapper tarjetaMapper;

    @Mock
    private TipoCuentaRepository tipoCuentaRepository;

    @Mock
    private TarjetaRepository tarjetaRepository;

    @InjectMocks
    private CuentaServiceImpl cuentaService;

    private Cuenta cuentaTest;
    private Tarjeta tarjetaTest;
    private TipoCuenta tipoCuentaTest;
    private TarjetaResponse tarjetaResponse;
    private TipoCuentaResponse tipoCuentaResponse;

   @BeforeEach
    void setUp() {
        tarjetaTest = new Tarjeta();
        tarjetaTest.setGuid("hola");
        tarjetaTest.setNumeroTarjeta("4242424242424242");
        tarjetaTest.setFechaCaducidad(LocalDate.parse("2025-12-31"));
        tarjetaTest.setCvv(123);
        tarjetaTest.setPin("1234");
        tarjetaTest.setLimiteDiario(BigDecimal.valueOf(100.0));
        tarjetaTest.setLimiteSemanal(BigDecimal.valueOf(200.0));
        tarjetaTest.setLimiteMensual(BigDecimal.valueOf(500.0));
        tarjetaTest.setTipoTarjeta(TipoTarjeta.valueOf("DEBITO"));

        tipoCuentaTest = new TipoCuenta();
        tipoCuentaTest.setNombre("normal");
        tipoCuentaTest.setInteres(BigDecimal.valueOf(2.0));

        tipoCuentaResponse = TipoCuentaResponse.builder()
                .guid(tipoCuentaTest.getGuid())
                .nombre(tipoCuentaTest.getNombre())
                .interes(tipoCuentaTest.getInteres())
                .build();

        tarjetaResponse = TarjetaResponse.builder()
                .guid(tarjetaTest.getGuid())
                .numeroTarjeta(tarjetaTest.getNumeroTarjeta())
                .fechaCaducidad(tarjetaTest.getFechaCaducidad())
                .limiteDiario(tarjetaTest.getLimiteDiario())
                .limiteSemanal(tarjetaTest.getLimiteSemanal())
                .limiteMensual(tarjetaTest.getLimiteMensual())
                .tipoTarjeta(tarjetaTest.getTipoTarjeta())
                .build();

        cuentaTest = new Cuenta();
        cuentaTest.setId(1L);
        cuentaTest.setGuid("12d45756-3895-49b2-90d3-c4a12d5ee081");
        cuentaTest.setIban("ES9120804243448487618583");
        cuentaTest.setSaldo(BigDecimal.valueOf(1000.0));
        cuentaTest.setTipoCuenta(tipoCuentaTest);
        cuentaTest.setTarjeta(tarjetaTest);
        cuentaTest.setIsDeleted(false);
    }

    @Test
    void getAll() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("nombre").ascending());

        CuentaResponse cuentaResponseTest = new CuentaResponse(
                cuentaTest.getGuid(),
                cuentaTest.getIban(),
                cuentaTest.getSaldo(),
                tipoCuentaResponse,
                tarjetaResponse,
                cuentaTest.getIsDeleted()
        );

        Page<Cuenta> cuentaPage = new PageImpl<>(List.of(cuentaTest), pageable, 1);
        when(cuentaRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(cuentaPage);
        when(tipoCuentaMapper.toTipoCuentaResponse(tipoCuentaTest)).thenReturn(tipoCuentaResponse);
        when(tarjetaMapper.toTarjetaResponse(tarjetaTest)).thenReturn(tarjetaResponse);
        when(cuentaMapper.toCuentaResponse(cuentaTest, tipoCuentaResponse, tarjetaResponse)).thenReturn(cuentaResponseTest);

        var result = cuentaService.getAll(
                Optional.of(cuentaTest.getIban()),
                Optional.of(cuentaTest.getSaldo()),
                Optional.of(cuentaTest.getSaldo()),
                Optional.of(cuentaTest.getTipoCuenta().getNombre()),
                pageable
        );

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1, result.getContent().size()),
                () -> {
                    CuentaResponse response = result.getContent().get(0);
                    assertNotNull(response);
                    assertEquals(cuentaTest.getIban(), response.getIban());
                    assertEquals(cuentaTest.getSaldo(), response.getSaldo());
                    assertEquals(tipoCuentaResponse, response.getTipoCuenta());
                    assertEquals(tarjetaResponse, response.getTarjeta());
                    assertFalse(response.getIsDeleted());
                }
        );

        verify(cuentaRepository).findAll(any(Specification.class), eq(pageable));
        verify(tipoCuentaMapper).toTipoCuentaResponse(tipoCuentaTest);
        verify(tarjetaMapper).toTarjetaResponse(tarjetaTest);
        verify(cuentaMapper).toCuentaResponse(cuentaTest, tipoCuentaResponse, tarjetaResponse);
    }

    @Test
    void getById() {
        String guidCuenta = "1";

        CuentaResponse expectedCuenta = new CuentaResponse();
        expectedCuenta.setGuid(guidCuenta);
        expectedCuenta.setIban("ES9120804243448487618583");
        expectedCuenta.setSaldo(BigDecimal.valueOf(1000.0));
        expectedCuenta.setTipoCuenta(tipoCuentaResponse);
        expectedCuenta.setTarjeta(tarjetaResponse);
        expectedCuenta.setIsDeleted(false);

        when(cuentaRepository.findByGuid(guidCuenta)).thenReturn(Optional.of(cuentaTest));
        when(tipoCuentaMapper.toTipoCuentaResponse(cuentaTest.getTipoCuenta())).thenReturn(tipoCuentaResponse);
        when(tarjetaMapper.toTarjetaResponse(cuentaTest.getTarjeta())).thenReturn(tarjetaResponse);
        when(cuentaMapper.toCuentaResponse(cuentaTest, tipoCuentaResponse, tarjetaResponse)).thenReturn(expectedCuenta);

        CuentaResponse resultCuenta = cuentaService.getById(guidCuenta);

        assertEquals(expectedCuenta, resultCuenta);

        verify(cuentaRepository).findByGuid(guidCuenta);
        verify(tipoCuentaMapper).toTipoCuentaResponse(cuentaTest.getTipoCuenta());
        verify(tarjetaMapper).toTarjetaResponse(cuentaTest.getTarjeta());
        verify(cuentaMapper).toCuentaResponse(cuentaTest, tipoCuentaResponse, tarjetaResponse);
    }

    @Test
    void getByIdNotFound() {
        String idCuenta = "4182d617-ec89-4fbc-be95-85e461778700";

        when(cuentaRepository.findByGuid(idCuenta)).thenReturn(Optional.empty());

        assertThrows(CuentaNotFound.class, () -> cuentaService.getById(idCuenta));

        verify(cuentaRepository).findByGuid(idCuenta);
    }

    @Test
    void save() {
        CuentaRequest cuentaRequest = new CuentaRequest();
        cuentaRequest.setTipoCuentaId(tipoCuentaTest.getGuid());
        cuentaRequest.setTarjetaId(tarjetaTest.getGuid());

        CuentaResponse expectedResponse = new CuentaResponse();
        expectedResponse.setGuid(cuentaTest.getGuid());
        expectedResponse.setIban(cuentaTest.getIban());
        expectedResponse.setSaldo(cuentaTest.getSaldo());
        expectedResponse.setTipoCuenta(tipoCuentaResponse);
        expectedResponse.setTarjeta(tarjetaResponse);
        expectedResponse.setIsDeleted(false);

        when(tipoCuentaRepository.findByGuid(cuentaRequest.getTipoCuentaId())).thenReturn(Optional.of(tipoCuentaTest));
        when(tarjetaRepository.findByGuid(cuentaRequest.getTarjetaId())).thenReturn(Optional.of(tarjetaTest));
        when(cuentaMapper.toCuenta(tipoCuentaTest, tarjetaTest)).thenReturn(cuentaTest);
        when(cuentaRepository.save(cuentaTest)).thenReturn(cuentaTest);
        when(tipoCuentaMapper.toTipoCuentaResponse(tipoCuentaTest)).thenReturn(tipoCuentaResponse);
        when(tarjetaMapper.toTarjetaResponse(tarjetaTest)).thenReturn(tarjetaResponse);
        when(cuentaMapper.toCuentaResponse(cuentaTest, tipoCuentaResponse, tarjetaResponse)).thenReturn(expectedResponse);

        var result = cuentaService.save(cuentaRequest);

        assertAll(
                () -> assertEquals(expectedResponse.getGuid(), result.getGuid()),
                () -> assertEquals(expectedResponse.getIban(), result.getIban()),
                () -> assertEquals(expectedResponse.getSaldo(), result.getSaldo()),
                () -> assertEquals(expectedResponse.getTipoCuenta(), result.getTipoCuenta()),
                () -> assertEquals(expectedResponse.getTarjeta(), result.getTarjeta()),
                () -> assertFalse(result.getIsDeleted())
        );

        verify(tipoCuentaRepository).findByGuid(cuentaRequest.getTipoCuentaId());
        verify(tarjetaRepository).findByGuid(cuentaRequest.getTarjetaId());
        verify(cuentaMapper).toCuenta(tipoCuentaTest, tarjetaTest);
        verify(cuentaRepository).save(cuentaTest);
        verify(tipoCuentaMapper).toTipoCuentaResponse(tipoCuentaTest);
        verify(tarjetaMapper).toTarjetaResponse(tarjetaTest);
        verify(cuentaMapper).toCuentaResponse(cuentaTest, tipoCuentaResponse, tarjetaResponse);
    }

    @Test
    void update() {
        String idCuenta = cuentaTest.getGuid();
        CuentaRequestUpdate cuentaRequestUpdate = new CuentaRequestUpdate();
        cuentaRequestUpdate.setSaldo(BigDecimal.valueOf(1200.0));
        cuentaRequestUpdate.setTipoCuentaId(tipoCuentaTest.getGuid());
        cuentaRequestUpdate.setTarjetaId(tarjetaTest.getGuid());

        Cuenta updatedCuenta = new Cuenta();
        updatedCuenta.setGuid(idCuenta);
        updatedCuenta.setIban(cuentaTest.getIban());
        updatedCuenta.setSaldo(cuentaRequestUpdate.getSaldo());
        updatedCuenta.setTipoCuenta(tipoCuentaTest);
        updatedCuenta.setTarjeta(tarjetaTest);
        updatedCuenta.setIsDeleted(false);

        CuentaResponse expectedResponse = new CuentaResponse();
        expectedResponse.setGuid(idCuenta);
        expectedResponse.setIban(updatedCuenta.getIban());
        expectedResponse.setSaldo(updatedCuenta.getSaldo());
        expectedResponse.setTipoCuenta(tipoCuentaResponse);
        expectedResponse.setTarjeta(tarjetaResponse);
        expectedResponse.setIsDeleted(false);

        when(cuentaRepository.findByGuid(idCuenta)).thenReturn(Optional.of(cuentaTest));
        when(cuentaMapper.toCuentaUpdate(cuentaRequestUpdate, cuentaTest, tipoCuentaTest, tarjetaTest)).thenReturn(updatedCuenta);
        when(cuentaRepository.save(updatedCuenta)).thenReturn(updatedCuenta);
        when(tipoCuentaMapper.toTipoCuentaResponse(tipoCuentaTest)).thenReturn(tipoCuentaResponse);
        when(tarjetaMapper.toTarjetaResponse(tarjetaTest)).thenReturn(tarjetaResponse);
        when(cuentaMapper.toCuentaResponse(updatedCuenta, tipoCuentaResponse, tarjetaResponse)).thenReturn(expectedResponse);

        CuentaResponse result = cuentaService.update(idCuenta, cuentaRequestUpdate);

        assertEquals(expectedResponse, result);

        verify(cuentaRepository).findByGuid(idCuenta);
        verify(cuentaMapper).toCuentaUpdate(cuentaRequestUpdate, cuentaTest, tipoCuentaTest, tarjetaTest);
        verify(cuentaRepository).save(updatedCuenta);
        verify(tipoCuentaMapper).toTipoCuentaResponse(tipoCuentaTest);
        verify(tarjetaMapper).toTarjetaResponse(tarjetaTest);
        verify(cuentaMapper).toCuentaResponse(updatedCuenta, tipoCuentaResponse, tarjetaResponse);
    }

    @Test
    void updateNotFound() {
        String idCuenta = "4182d617-ec89-4fbc-be95-85e461778700";
        CuentaRequestUpdate cuentaRequestUpdate = new CuentaRequestUpdate();
        when(cuentaRepository.findByGuid(idCuenta)).thenReturn(Optional.empty());

        assertThrows(CuentaNotFound.class, () -> cuentaService.update(idCuenta, cuentaRequestUpdate));

        verify(cuentaRepository).findByGuid(idCuenta);
        verify(cuentaRepository, times(0)).save(any(Cuenta.class));
    }

    @Test
    void delete() {
        String idCuenta = cuentaTest.getGuid();

        when(cuentaRepository.findByGuid(idCuenta)).thenReturn(Optional.of(cuentaTest));
        when(cuentaRepository.save(any(Cuenta.class))).thenAnswer(invocation -> {
            Cuenta savedCuenta = invocation.getArgument(0);
            assertTrue(savedCuenta.getIsDeleted());
            return savedCuenta;
        });

        cuentaService.deleteById(idCuenta);

        verify(cuentaRepository).findByGuid(idCuenta);
        verify(cuentaRepository).save(any(Cuenta.class));
    }

    @Test
    void deleteNotFound() {
        String idCuenta = "5f5c2645-a470-4fad-b003-5fefc08fceca";

        when(cuentaRepository.findByGuid(idCuenta)).thenReturn(Optional.empty());

        assertThrows(CuentaNotFound.class, () -> cuentaService.deleteById(idCuenta));

        verify(cuentaRepository, times(1)).findByGuid(idCuenta);
    }
}*/