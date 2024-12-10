package org.example.vivesbankproject.cuenta.services;

import org.example.vivesbankproject.config.websockets.WebSocketHandler;
import org.example.vivesbankproject.rest.cliente.exceptions.ClienteNotFound;
import org.example.vivesbankproject.rest.cliente.mappers.ClienteMapper;
import org.example.vivesbankproject.rest.cliente.models.Cliente;
import org.example.vivesbankproject.rest.cliente.repositories.ClienteRepository;
import org.example.vivesbankproject.rest.cuenta.dto.cuenta.CuentaRequest;
import org.example.vivesbankproject.rest.cuenta.dto.cuenta.CuentaRequestUpdate;
import org.example.vivesbankproject.rest.cuenta.dto.cuenta.CuentaResponse;
import org.example.vivesbankproject.rest.cuenta.exceptions.cuenta.CuentaNotFound;
import org.example.vivesbankproject.rest.cuenta.exceptions.cuenta.CuentaNotFoundByIban;
import org.example.vivesbankproject.rest.cuenta.exceptions.cuenta.CuentaNotFoundByNumTarjeta;
import org.example.vivesbankproject.rest.cuenta.exceptions.tipoCuenta.TipoCuentaNotFound;
import org.example.vivesbankproject.rest.cuenta.mappers.CuentaMapper;
import org.example.vivesbankproject.rest.cuenta.mappers.TipoCuentaMapper;
import org.example.vivesbankproject.rest.cuenta.models.Cuenta;
import org.example.vivesbankproject.rest.cuenta.models.TipoCuenta;
import org.example.vivesbankproject.rest.cuenta.repositories.CuentaRepository;
import org.example.vivesbankproject.rest.cuenta.repositories.TipoCuentaRepository;
import org.example.vivesbankproject.rest.cuenta.services.CuentaServiceImpl;
import org.example.vivesbankproject.rest.tarjeta.exceptions.TarjetaNotFound;
import org.example.vivesbankproject.rest.tarjeta.exceptions.TarjetaNotFoundByNumero;
import org.example.vivesbankproject.rest.tarjeta.mappers.TarjetaMapper;
import org.example.vivesbankproject.rest.tarjeta.models.Tarjeta;
import org.example.vivesbankproject.rest.tarjeta.repositories.TarjetaRepository;
import org.example.vivesbankproject.config.websockets.WebSocketConfig;
import org.example.vivesbankproject.rest.users.models.User;
import org.example.vivesbankproject.rest.users.repositories.UserRepository;
import org.example.vivesbankproject.websocket.notifications.models.Notification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CuentaServiceImplTest {

    @InjectMocks
    private CuentaServiceImpl cuentaService;

    @Mock
    private WebSocketConfig webSocketConfig;

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

    @Mock
    private UserRepository userRepository;

    @Mock
    private WebSocketHandler webSocketHandler;

    private Cuenta cuenta;
    private TipoCuenta tipoCuenta;
    private Tarjeta tarjeta;
    private Cliente cliente;
    private User user;

    @BeforeEach
    void setUp() {
        cuenta = new Cuenta();
        tipoCuenta = new TipoCuenta();
        tarjeta = new Tarjeta();
        cliente = new Cliente();
        user = new User();

        cliente.setUser(user);
        cuenta.setTipoCuenta(tipoCuenta);
        cuenta.setTarjeta(tarjeta);
        cuenta.setCliente(cliente);
        user.setUsername("testuser");
    }

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
    void updateNotFound() {
        String cuentaId = "123";
        CuentaRequestUpdate cuentaRequestUpdate = new CuentaRequestUpdate();

        when(cuentaRepository.findByGuid(cuentaId)).thenReturn(Optional.empty());

        assertThrows(CuentaNotFound.class, () -> cuentaService.update(cuentaId, cuentaRequestUpdate));
        verify(cuentaRepository).findByGuid(cuentaId);
        verify(cuentaRepository, never()).save(any());
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

    @Test
    void getAllCuentasByClienteGuid() {
        String clienteGuid = "client123";
        ArrayList<Cuenta> cuentas = new ArrayList<>();
        cuentas.add(cuenta);

        when(cuentaRepository.findAllByCliente_Guid(clienteGuid)).thenReturn(cuentas);
        when(cuentaMapper.toCuentaResponse(
                eq(cuenta),
                eq(cuenta.getTipoCuenta().getGuid()),
                eq(cuenta.getTarjeta().getGuid()),
                eq(cuenta.getCliente().getGuid())
        )).thenReturn(new CuentaResponse());

        ArrayList<CuentaResponse> result = cuentaService.getAllCuentasByClienteGuid(clienteGuid);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(cuentaRepository).findAllByCliente_Guid(clienteGuid);
    }

    @Test
    void getByIban() {
        String iban = "ES1234567890";
        cuenta.setIban(iban);

        when(cuentaRepository.findByIban(iban)).thenReturn(Optional.of(cuenta));
        when(cuentaMapper.toCuentaResponse(
                eq(cuenta),
                eq(cuenta.getTipoCuenta().getGuid()),
                eq(cuenta.getTarjeta().getGuid()),
                eq(cuenta.getCliente().getGuid())
        )).thenReturn(new CuentaResponse());

        CuentaResponse result = cuentaService.getByIban(iban);

        assertNotNull(result);
        verify(cuentaRepository).findByIban(iban);
    }

    @Test
    void getByIbanNotFound() {
        String iban = "ES1234567890";

        when(cuentaRepository.findByIban(iban)).thenReturn(Optional.empty());

        assertThrows(CuentaNotFoundByIban.class, () -> cuentaService.getByIban(iban));
    }

    @Test
    void getByNumTarjeta() {
        String numTarjeta = "1234567890123456";
        tarjeta.setNumeroTarjeta(numTarjeta);

        when(tarjetaRepository.findByNumeroTarjeta(numTarjeta)).thenReturn(Optional.of(tarjeta));
        when(cuentaRepository.findByTarjetaId(tarjeta.getId())).thenReturn(Optional.of(cuenta));
        when(cuentaMapper.toCuentaResponse(
                eq(cuenta),
                eq(cuenta.getTipoCuenta().getGuid()),
                eq(cuenta.getTarjeta().getGuid()),
                eq(cuenta.getCliente().getGuid())
        )).thenReturn(new CuentaResponse());

        CuentaResponse result = cuentaService.getByNumTarjeta(numTarjeta);

        assertNotNull(result);
        verify(tarjetaRepository).findByNumeroTarjeta(numTarjeta);
        verify(cuentaRepository).findByTarjetaId(tarjeta.getId());
    }

    @Test
    void getByNumTarjetaNotFound() {
        String numTarjeta = "1234567890123456";

        when(tarjetaRepository.findByNumeroTarjeta(numTarjeta)).thenReturn(Optional.empty());

        assertThrows(TarjetaNotFoundByNumero.class, () -> cuentaService.getByNumTarjeta(numTarjeta));
    }

    @Test
    void save() {
        CuentaRequest cuentaRequest = new CuentaRequest();
        cuentaRequest.setTipoCuentaId("tipoCuenta123");
        cuentaRequest.setTarjetaId("tarjeta123");
        cuentaRequest.setClienteId("cliente123");

        when(tipoCuentaRepository.findByGuid(cuentaRequest.getTipoCuentaId())).thenReturn(Optional.of(tipoCuenta));
        when(tarjetaRepository.findByGuid(cuentaRequest.getTarjetaId())).thenReturn(Optional.of(tarjeta));
        when(clienteRepository.findByGuid(cuentaRequest.getClienteId())).thenReturn(Optional.of(cliente));
        when(cuentaMapper.toCuenta(tipoCuenta, tarjeta, cliente)).thenReturn(cuenta);
        when(cuentaRepository.save(cuenta)).thenReturn(cuenta);
        when(cuentaMapper.toCuentaResponse(
                eq(cuenta),
                eq(cuenta.getTipoCuenta().getGuid()),
                eq(cuenta.getTarjeta().getGuid()),
                eq(cuenta.getCliente().getGuid())
        )).thenReturn(new CuentaResponse());
        when(userRepository.findByGuid(any())).thenReturn(Optional.of(user));

        CuentaResponse result = cuentaService.save(cuentaRequest);

        assertNotNull(result);
        verify(tipoCuentaRepository).findByGuid(cuentaRequest.getTipoCuentaId());
        verify(tarjetaRepository).findByGuid(cuentaRequest.getTarjetaId());
        verify(clienteRepository).findByGuid(cuentaRequest.getClienteId());
    }

    @Test
    void saveTipoCuentaNotFound() {
        CuentaRequest cuentaRequest = new CuentaRequest();
        cuentaRequest.setTipoCuentaId("tipoCuenta123");

        when(tipoCuentaRepository.findByGuid(cuentaRequest.getTipoCuentaId())).thenReturn(Optional.empty());

        assertThrows(TipoCuentaNotFound.class, () -> cuentaService.save(cuentaRequest));
    }

    @Test
    void update() {
        String cuentaId = "cuenta123";
        CuentaRequestUpdate cuentaRequestUpdate = new CuentaRequestUpdate();
        cuentaRequestUpdate.setTipoCuentaId("tipoCuenta123");
        cuentaRequestUpdate.setTarjetaId("tarjeta123");
        cuentaRequestUpdate.setClienteId("cliente123");

        when(cuentaRepository.findByGuid(cuentaId)).thenReturn(Optional.of(cuenta));
        when(tipoCuentaRepository.findByGuid(cuentaRequestUpdate.getTipoCuentaId())).thenReturn(Optional.of(tipoCuenta));
        when(tarjetaRepository.findByGuid(cuentaRequestUpdate.getTarjetaId())).thenReturn(Optional.of(tarjeta));
        when(clienteRepository.findByGuid(cuentaRequestUpdate.getClienteId())).thenReturn(Optional.of(cliente));
        when(cuentaMapper.toCuentaUpdate(cuentaRequestUpdate, cuenta, tipoCuenta, tarjeta, cliente)).thenReturn(cuenta);
        when(cuentaRepository.save(cuenta)).thenReturn(cuenta);
        when(cuentaMapper.toCuentaResponse(
                eq(cuenta),
                eq(cuenta.getTipoCuenta().getGuid()),
                eq(cuenta.getTarjeta().getGuid()),
                eq(cuenta.getCliente().getGuid())
        )).thenReturn(new CuentaResponse());
        when(userRepository.findByGuid(any())).thenReturn(Optional.of(user));

        CuentaResponse result = cuentaService.update(cuentaId, cuentaRequestUpdate);

        assertNotNull(result);
        verify(cuentaRepository).findByGuid(cuentaId);
        verify(tipoCuentaRepository).findByGuid(cuentaRequestUpdate.getTipoCuentaId());
        verify(tarjetaRepository).findByGuid(cuentaRequestUpdate.getTarjetaId());
        verify(clienteRepository).findByGuid(cuentaRequestUpdate.getClienteId());
    }

    @Test
    void updateParcial() {
        String cuentaId = "cuenta123";
        CuentaRequestUpdate cuentaRequestUpdate = new CuentaRequestUpdate();
        cuentaRequestUpdate.setTipoCuentaId("");
        cuentaRequestUpdate.setTarjetaId("");
        cuentaRequestUpdate.setClienteId("");

        when(cuentaRepository.findByGuid(cuentaId)).thenReturn(Optional.of(cuenta));
        when(cuentaMapper.toCuentaUpdate(cuentaRequestUpdate, cuenta, tipoCuenta, tarjeta, cliente)).thenReturn(cuenta);
        when(cuentaRepository.save(cuenta)).thenReturn(cuenta);
        when(cuentaMapper.toCuentaResponse(
                eq(cuenta),
                eq(cuenta.getTipoCuenta().getGuid()),
                eq(cuenta.getTarjeta().getGuid()),
                eq(cuenta.getCliente().getGuid())
        )).thenReturn(new CuentaResponse());
        when(userRepository.findByGuid(any())).thenReturn(Optional.of(user));

        CuentaResponse result = cuentaService.update(cuentaId, cuentaRequestUpdate);

        assertNotNull(result);
        verify(cuentaRepository).findByGuid(cuentaId);
        verify(cuentaRepository).save(cuenta);
    }
}