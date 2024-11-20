package org.example.vivesbankproject.cuenta.services;

import org.example.vivesbankproject.cuenta.dto.CuentaRequest;
import org.example.vivesbankproject.cuenta.dto.CuentaRequestUpdate;
import org.example.vivesbankproject.cuenta.dto.CuentaResponse;
import org.example.vivesbankproject.cuenta.exceptions.CuentaNotFound;
import org.example.vivesbankproject.cuenta.mappers.CuentaMapper;
import org.example.vivesbankproject.cuenta.models.Cuenta;
import org.example.vivesbankproject.cuenta.models.TipoCuenta;
import org.example.vivesbankproject.cuenta.repositories.CuentaRepository;
import org.example.vivesbankproject.tarjeta.models.Tarjeta;
import org.example.vivesbankproject.tarjeta.models.Tipo;
import org.example.vivesbankproject.tarjeta.models.TipoTarjeta;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CuentaServiceImplTest {
    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private CuentaMapper cuentaMapper;

    @InjectMocks
    private CuentaServiceImpl cuentaService;

    private Cuenta cuentaTest;
    private Tarjeta tarjetaTest;
    private TipoCuenta tipoCuentaTest;

    @BeforeEach
    void setUp() {
        tarjetaTest = new Tarjeta();
        tarjetaTest.setId(UUID.fromString("921f6b86-695d-4361-8905-365d97691024"));
        tarjetaTest.setNumeroTarjeta("4242424242424242");
        tarjetaTest.setFechaCaducidad(LocalDate.parse("2025-12-31"));
        tarjetaTest.setCvv(123);
        tarjetaTest.setPin("1234");
        tarjetaTest.setLimiteDiario(BigDecimal.valueOf(100.0));
        tarjetaTest.setLimiteSemanal(BigDecimal.valueOf(200.0));
        tarjetaTest.setLimiteMensual(BigDecimal.valueOf(500.0));
        tarjetaTest.setTipoTarjeta(TipoTarjeta.builder().nombre(Tipo.valueOf("DEBITO")).build());

        tipoCuentaTest = new TipoCuenta();
        tipoCuentaTest.setNombre("normal");
        tipoCuentaTest.setInteres(BigDecimal.valueOf(2.0));

        cuentaTest = new Cuenta();
        cuentaTest.setId("12d45756-3895-49b2-90d3-c4a12d5ee081");
        cuentaTest.setIban("ES9120804243448487618583");
        cuentaTest.setSaldo(BigDecimal.valueOf(1000.0));
        cuentaTest.setTipoCuenta(tipoCuentaTest);
        cuentaTest.setTarjeta(tarjetaTest);
        cuentaTest.setIsDeleted(false);
    }

    @Test
    void getAll() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("nombre").ascending());

        Page<Cuenta> cuentaPage = new PageImpl<>(List.of(cuentaTest), pageable, 1);

        when(cuentaRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(cuentaPage);

        var result = cuentaService.getAll(Optional.of(cuentaTest.getIban()), Optional.of(cuentaTest.getSaldo()), Optional.of(cuentaTest.getSaldo()), Optional.of(String.valueOf(cuentaTest.getTipoCuenta())), pageable);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1, result.getContent().size()),
                () -> assertTrue(result.getContent().contains(cuentaTest)),
                () -> assertEquals("ES9120804243448487618583", result.getContent().getFirst().getIban()),
                () -> assertEquals(BigDecimal.valueOf(1000.0), result.getContent().getFirst().getSaldo()),
                () -> assertEquals(tipoCuentaTest, result.getContent().getFirst().getTipoCuenta()),
                () -> assertEquals(tarjetaTest, result.getContent().getFirst().getTarjeta()),
                () -> assertFalse(result.getContent().getFirst().getIsDeleted())
        );

        verify(cuentaRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getById() {
        String idCuenta = "hola";

        CuentaResponse expectedCuenta = new CuentaResponse();
        expectedCuenta.setId(idCuenta);
        expectedCuenta.setIban("ES9120804243448487618583");
        expectedCuenta.setSaldo(BigDecimal.valueOf(1000.0));
        expectedCuenta.setTipoCuenta(tipoCuentaTest);
        expectedCuenta.setTarjeta(tarjetaTest);
        expectedCuenta.setIsDeleted(false);

        Cuenta cuentaMapped = new Cuenta();
        cuentaMapped.setId(idCuenta);
        cuentaMapped.setIban(expectedCuenta.getIban());
        cuentaMapped.setSaldo(expectedCuenta.getSaldo());
        cuentaMapped.setTipoCuenta(tipoCuentaTest);
        cuentaMapped.setTarjeta(tarjetaTest);
        cuentaMapped.setIsDeleted(false);

        when(cuentaRepository.findById(idCuenta)).thenReturn(Optional.of(cuentaMapped));
        when(cuentaMapper.toCuentaResponse(cuentaMapped)).thenReturn(expectedCuenta);

        CuentaResponse resultCuenta = cuentaService.getById(idCuenta);

        assertEquals(expectedCuenta, resultCuenta);

        verify(cuentaRepository, times(1)).findById(idCuenta);
    }


    @Test
    void getByIdNotFound() {
        String idCuenta = "4182d617-ec89-4fbc-be95-85e461778700";
        when(cuentaRepository.findById("4182d617-ec89-4fbc-be95-85e461778700")).thenReturn(Optional.empty());

        assertThrows(CuentaNotFound.class, () -> cuentaService.getById(idCuenta));

        verify(cuentaRepository).findById(idCuenta);
    }

    @Test
    void save() {
        Tarjeta tarjeta = new Tarjeta();
        tarjeta.setId(UUID.fromString("7b498e86-5197-4e05-9361-3da894b62353"));
        tarjeta.setNumeroTarjeta("4009156782194826");
        tarjeta.setFechaCaducidad(LocalDate.parse("2025-12-31"));
        tarjeta.setCvv(987);
        tarjeta.setPin("0987");
        tarjeta.setLimiteDiario(BigDecimal.valueOf(100.0));
        tarjeta.setLimiteSemanal(BigDecimal.valueOf(200.0));
        tarjeta.setLimiteMensual(BigDecimal.valueOf(500.0));

        TipoCuenta tipoCuenta = new TipoCuenta();
        tipoCuenta.setNombre("normal");
        tipoCuenta.setInteres(BigDecimal.valueOf(2.0));

        Cuenta cuenta = new Cuenta();
        cuenta.setId("6c257ab6-e588-4cef-a479-c2f8fcd7379a");
        cuenta.setIban("ES3715447107447741413620");
        cuenta.setSaldo(BigDecimal.valueOf(1000.0));
        cuenta.setTipoCuenta(tipoCuenta);
        cuenta.setTarjeta(tarjeta);
        cuenta.setIsDeleted(false);

        CuentaRequest cuentaRequest = new CuentaRequest();
        cuentaRequest.setIban(cuenta.getIban());
        cuentaRequest.setSaldo(cuenta.getSaldo());
        cuentaRequest.setTipoCuenta(cuenta.getTipoCuenta());
        cuentaRequest.setTarjeta(cuenta.getTarjeta());
        cuentaRequest.setIsDeleted(cuenta.getIsDeleted());

        CuentaResponse cuentaResponse = new CuentaResponse();
        cuentaResponse.setId(cuenta.getId());
        cuentaResponse.setIban(cuenta.getIban());
        cuentaResponse.setSaldo(cuenta.getSaldo());
        cuentaResponse.setTipoCuenta(cuenta.getTipoCuenta());
        cuentaResponse.setTarjeta(cuenta.getTarjeta());
        cuentaResponse.setIsDeleted(cuenta.getIsDeleted());

        when(cuentaRepository.findByIban(cuentaRequest.getIban())).thenReturn(Optional.empty());
        when(cuentaRepository.save(any(Cuenta.class))).thenReturn(cuenta);
        when(cuentaMapper.toCuenta(cuentaRequest)).thenReturn(cuenta);
        when(cuentaMapper.toCuentaResponse(cuenta)).thenReturn(cuentaResponse);

        var result = cuentaService.save(cuentaRequest);

        assertAll(
                () -> assertEquals(cuentaResponse.getId(), result.getId()),
                () -> assertEquals(cuentaResponse.getIban(), result.getIban()),
                () -> assertEquals(cuentaResponse.getSaldo(), result.getSaldo()),
                () -> assertEquals(cuentaResponse.getTipoCuenta(), result.getTipoCuenta()),
                () -> assertEquals(cuentaResponse.getTarjeta(), result.getTarjeta()),
                () -> assertFalse(result.getIsDeleted())
        );

        verify(cuentaRepository, times(1)).findByIban(cuentaRequest.getIban());
        verify(cuentaRepository, times(1)).save(any(Cuenta.class));
        verify(cuentaMapper, times(1)).toCuenta(cuentaRequest);
        verify(cuentaMapper, times(1)).toCuentaResponse(cuenta);
    }

    @Test
    void update() {
        String idCuenta = "6c257ab6-e588-4cef-a479-c2f8fcd7379a";

        Cuenta cuenta = new Cuenta();
        cuenta.setId(idCuenta);
        cuenta.setIban("ES1331839032611076912510");
        cuenta.setSaldo(BigDecimal.valueOf(1000.0));
        cuenta.setTipoCuenta(tipoCuentaTest);
        cuenta.setTarjeta(tarjetaTest);
        cuenta.setIsDeleted(false);

        CuentaRequestUpdate cuentaRequestUpdate = new CuentaRequestUpdate();
        cuentaRequestUpdate.setSaldo(BigDecimal.valueOf(1200.0));
        cuentaRequestUpdate.setTipoCuenta(tipoCuentaTest);
        cuentaRequestUpdate.setTarjeta(tarjetaTest);

        CuentaResponse expectedResponse = new CuentaResponse();
        expectedResponse.setId(idCuenta);
        expectedResponse.setIban(cuenta.getIban());
        expectedResponse.setSaldo(cuentaRequestUpdate.getSaldo());
        expectedResponse.setTipoCuenta(cuentaRequestUpdate.getTipoCuenta());
        expectedResponse.setTarjeta(cuentaRequestUpdate.getTarjeta());
        expectedResponse.setIsDeleted(cuenta.getIsDeleted());

        when(cuentaRepository.findById(idCuenta)).thenReturn(Optional.of(cuenta));
        when(cuentaRepository.save(any(Cuenta.class))).thenReturn(cuenta);
        when(cuentaMapper.toCuentaUpdate(cuentaRequestUpdate, cuenta)).thenReturn(cuenta);
        when(cuentaMapper.toCuentaResponse(cuenta)).thenReturn(expectedResponse);

        CuentaResponse result = cuentaService.update(idCuenta, cuentaRequestUpdate);

        assertEquals(expectedResponse, result);

        verify(cuentaRepository, times(1)).findById(idCuenta);
        verify(cuentaRepository, times(1)).save(any(Cuenta.class));
        verify(cuentaMapper, times(1)).toCuentaUpdate(cuentaRequestUpdate, cuenta);
        verify(cuentaMapper, times(1)).toCuentaResponse(cuenta);
    }

    @Test
    void updateNotFound() {
        String idCuenta = "4182d617-ec89-4fbc-be95-85e461778700";
        CuentaRequestUpdate cuentaRequestUpdate = new CuentaRequestUpdate();
        when(cuentaRepository.findById(idCuenta)).thenReturn(Optional.empty());

        assertThrows(CuentaNotFound.class, () -> cuentaService.update(idCuenta, cuentaRequestUpdate));

        verify(cuentaRepository).findById(idCuenta);
        verify(cuentaRepository, never()).save(any(Cuenta.class));
    }

    @Test
    void delete() {
        String idCuenta = "hola";

        Tarjeta tarjeta = new Tarjeta();
        tarjeta.setId(UUID.fromString("921f6b86-695d-4361-8905-365d97691024"));
        tarjeta.setNumeroTarjeta("4009156782194826");
        tarjeta.setFechaCaducidad(LocalDate.parse("2025-12-31"));
        tarjeta.setCvv(456);
        tarjeta.setPin("4567");
        tarjeta.setLimiteDiario(BigDecimal.valueOf(100.0));
        tarjeta.setLimiteSemanal(BigDecimal.valueOf(200.0));
        tarjeta.setLimiteMensual(BigDecimal.valueOf(500.0));
        tarjeta.setTipoTarjeta(TipoTarjeta.builder().nombre(Tipo.valueOf("DEBITO")).build());

        TipoCuenta tipoCuenta = new TipoCuenta();
        tipoCuenta.setNombre("ahorro");
        tipoCuenta.setInteres(BigDecimal.valueOf(3.0));

        Cuenta cuentaToDelete = new Cuenta();
        cuentaToDelete.setId(idCuenta);
        cuentaToDelete.setIban("ES7302413102733585086708");
        cuentaToDelete.setSaldo(BigDecimal.valueOf(1000.0));
        cuentaToDelete.setTarjeta(tarjeta);
        cuentaToDelete.setTipoCuenta(tipoCuenta);
        cuentaToDelete.setIsDeleted(false);
        
        when(cuentaRepository.findById(idCuenta)).thenReturn(Optional.of(cuentaToDelete));

        cuentaService.delete(idCuenta);

        verify(cuentaRepository, times(1)).findById(idCuenta);
    }

    @Test
    void deleteNotFound() {
        String idCuenta = "5f5c2645-a470-4fad-b003-5fefc08fceca";

        when(cuentaRepository.findById(idCuenta)).thenReturn(Optional.empty());

        assertThrows(CuentaNotFound.class, () -> cuentaService.delete(idCuenta));

        verify(cuentaRepository, times(0)).deleteById(idCuenta);
    }
}