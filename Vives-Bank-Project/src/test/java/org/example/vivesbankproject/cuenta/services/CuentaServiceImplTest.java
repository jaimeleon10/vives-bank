package org.example.vivesbankproject.cuenta.services;

import org.example.vivesbankproject.cliente.dto.ClienteResponse;
import org.example.vivesbankproject.cliente.mappers.ClienteMapper;
import org.example.vivesbankproject.cliente.models.Cliente;
import org.example.vivesbankproject.cliente.repositories.ClienteRepository;
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
import org.example.vivesbankproject.tarjeta.repositories.TarjetaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CuentaServiceImplTest {

    @InjectMocks
    private CuentaServiceImpl cuentaService;

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

    @Mock
    private ClienteMapper clienteMapper;

    @Mock
    private ClienteRepository clienteRepository;


    @Test
    void getAll() {
        Pageable pageable = PageRequest.of(0, 10);
        TipoCuenta tipoCuenta = new TipoCuenta();
        Cliente cliente = new Cliente();
        Tarjeta tarjeta = new Tarjeta();

        Cuenta cuenta = new Cuenta();
        cuenta.setIban("ES1234567890");
        cuenta.setTipoCuenta(tipoCuenta);
        cuenta.setCliente(cliente);
        cuenta.setTarjeta(tarjeta);

        Page<Cuenta> cuentaPage = new PageImpl<>(List.of(cuenta));

        when(cuentaRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(cuentaPage);
        when(cuentaMapper.toCuentaResponse(any(Cuenta.class), any(), any(), any())).thenReturn(new CuentaResponse());

        Page<CuentaResponse> result = cuentaService.getAll(Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(String.valueOf(tipoCuenta)), pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(cuentaRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getById() {
        String cuentaId = "123";
        TipoCuenta tipoCuenta = new TipoCuenta();
        Cliente cliente = new Cliente();
        Tarjeta tarjeta = new Tarjeta();

        Cuenta cuenta = new Cuenta();
        cuenta.setGuid(cuentaId);
        cuenta.setIban("ES1234567890");
        cuenta.setTipoCuenta(tipoCuenta);
        cuenta.setCliente(cliente);
        cuenta.setTarjeta(tarjeta);

        when(cuentaRepository.findByGuid(cuentaId)).thenReturn(Optional.of(cuenta));
        when(cuentaMapper.toCuentaResponse(any(Cuenta.class), any(), any(), any())).thenReturn(new CuentaResponse());

        CuentaResponse result = cuentaService.getById(cuentaId);

        assertNotNull(result);
        verify(cuentaRepository).findByGuid(cuentaId);
    }

    @Test
    void getByIdCuentaNotFound() {
        String cuentaId = "123";
        when(cuentaRepository.findByGuid(cuentaId)).thenReturn(Optional.empty());

        assertThrows(CuentaNotFound.class, () -> cuentaService.getById(cuentaId));
        verify(cuentaRepository).findByGuid(cuentaId);
    }

    @Test
    void save() {
        CuentaRequest cuentaRequest = new CuentaRequest();
        cuentaRequest.setTipoCuentaId("tipo1");
        cuentaRequest.setTarjetaId("tarjeta1");
        cuentaRequest.setClienteId("cliente1");

        TipoCuenta tipoCuenta = new TipoCuenta();
        Cliente cliente = new Cliente();
        Tarjeta tarjeta = new Tarjeta();

        Cuenta cuenta = new Cuenta();
        cuenta.setIban("ES1234567890");
        cuenta.setTipoCuenta(tipoCuenta);
        cuenta.setCliente(cliente);
        cuenta.setTarjeta(tarjeta);

        when(tipoCuentaRepository.findByGuid(cuentaRequest.getTipoCuentaId())).thenReturn(Optional.of(tipoCuenta));
        when(tarjetaRepository.findByGuid(cuentaRequest.getTarjetaId())).thenReturn(Optional.of(tarjeta));
        when(clienteRepository.findByGuid(cuentaRequest.getClienteId())).thenReturn(Optional.of(cliente));
        when(cuentaMapper.toCuenta(tipoCuenta, tarjeta, cliente)).thenReturn(cuenta);
        when(cuentaRepository.save(cuenta)).thenReturn(cuenta);
        when(cuentaMapper.toCuentaResponse(any(), any(), any(), any())).thenReturn(new CuentaResponse());

        CuentaResponse result = cuentaService.save(cuentaRequest);

        assertNotNull(result);
        verify(cuentaRepository).save(cuenta);
        verify(clienteRepository).save(cliente);
    }

    @Test
    void update() {
        String cuentaId = "123";
        Cuenta cuenta = new Cuenta();
        cuenta.setGuid(cuentaId);

        CuentaRequestUpdate cuentaRequestUpdate = new CuentaRequestUpdate();

        Cuenta cuentaActualizada = new Cuenta();
        cuentaActualizada.setGuid(cuentaId);

        when(cuentaRepository.findByGuid(cuentaId)).thenReturn(Optional.of(cuenta));
        when(cuentaMapper.toCuentaUpdate(cuentaRequestUpdate, cuenta, cuenta.getTipoCuenta(), cuenta.getTarjeta(), cuenta.getCliente())).thenReturn(cuentaActualizada);
        when(cuentaRepository.save(cuentaActualizada)).thenReturn(cuentaActualizada);

        TipoCuentaResponse tipoCuentaResponse = new TipoCuentaResponse();
        when(tipoCuentaMapper.toTipoCuentaResponse(cuentaActualizada.getTipoCuenta())).thenReturn(tipoCuentaResponse);

        TarjetaResponse tarjetaResponse = new TarjetaResponse();
        when(tarjetaMapper.toTarjetaResponse(cuentaActualizada.getTarjeta())).thenReturn(tarjetaResponse);
        ClienteResponse clienteDataResponse = new ClienteResponse();

        when(clienteMapper.toClienteResponse(cuentaActualizada.getCliente(), cuenta.getCliente().getGuid())).thenReturn(clienteDataResponse);

        when(cuentaMapper.toCuentaResponse(any(), any(), any(), any())).thenReturn(new CuentaResponse());

        CuentaResponse result = cuentaService.update(cuentaId, cuentaRequestUpdate);

        assertNotNull(result);
        verify(cuentaRepository).findByGuid(cuentaId);
        verify(cuentaRepository).save(cuentaActualizada);
        verify(cuentaMapper).toCuentaResponse(any(), any(), any(), any());
    }

    @Test
    void updateNotFound() {
        String cuentaId = "123";
        CuentaRequestUpdate cuentaRequestUpdate = new CuentaRequestUpdate();

        when(cuentaRepository.findByGuid(cuentaId)).thenReturn(Optional.empty());

        assertThrows(CuentaNotFound.class, () -> cuentaService.update(cuentaId, cuentaRequestUpdate));
        verify(cuentaRepository).findByGuid(cuentaId);
        verify(cuentaRepository, never()).save(any());
    }

    @Test
    void deleteById() {
        String cuentaId = "123";
        Cuenta cuenta = new Cuenta();
        cuenta.setGuid(cuentaId);

        when(cuentaRepository.findByGuid(cuentaId)).thenReturn(Optional.of(cuenta));

        cuentaService.deleteById(cuentaId);

        verify(cuentaRepository).save(cuenta);
        assertTrue(cuenta.getIsDeleted());
    }

    @Test
    void deleteByIdNotFound() {
        String cuentaId = "123";

        when(cuentaRepository.findByGuid(cuentaId)).thenReturn(Optional.empty());

        assertThrows(CuentaNotFound.class, () -> cuentaService.deleteById(cuentaId));
        verify(cuentaRepository).findByGuid(cuentaId);
        verify(cuentaRepository, never()).save(any());
    }

    @Test
    void evictClienteCache() {
        String clienteGuid = "cliente123";
        cuentaService.evictClienteCache(clienteGuid);
        assertDoesNotThrow(() -> cuentaService.evictClienteCache(clienteGuid));
    }

}