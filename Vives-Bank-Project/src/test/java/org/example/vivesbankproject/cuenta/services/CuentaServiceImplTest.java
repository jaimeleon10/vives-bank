package org.example.vivesbankproject.cuenta.services;

import org.example.vivesbankproject.rest.cliente.mappers.ClienteMapper;
import org.example.vivesbankproject.rest.cliente.models.Cliente;
import org.example.vivesbankproject.rest.cliente.repositories.ClienteRepository;
import org.example.vivesbankproject.rest.cuenta.dto.cuenta.CuentaRequest;
import org.example.vivesbankproject.rest.cuenta.dto.cuenta.CuentaRequestUpdate;
import org.example.vivesbankproject.rest.cuenta.dto.cuenta.CuentaResponse;
import org.example.vivesbankproject.rest.cuenta.exceptions.cuenta.CuentaNotFound;
import org.example.vivesbankproject.rest.cuenta.exceptions.cuenta.CuentaNotFoundByIban;
import org.example.vivesbankproject.rest.cuenta.exceptions.cuenta.CuentaNotFoundByNumTarjeta;
import org.example.vivesbankproject.rest.cuenta.mappers.CuentaMapper;
import org.example.vivesbankproject.rest.cuenta.mappers.TipoCuentaMapper;
import org.example.vivesbankproject.rest.cuenta.models.Cuenta;
import org.example.vivesbankproject.rest.cuenta.models.TipoCuenta;
import org.example.vivesbankproject.rest.cuenta.repositories.CuentaRepository;
import org.example.vivesbankproject.rest.cuenta.repositories.TipoCuentaRepository;
import org.example.vivesbankproject.rest.cuenta.services.CuentaServiceImpl;
import org.example.vivesbankproject.rest.tarjeta.exceptions.TarjetaNotFoundByNumero;
import org.example.vivesbankproject.rest.tarjeta.mappers.TarjetaMapper;
import org.example.vivesbankproject.rest.tarjeta.models.Tarjeta;
import org.example.vivesbankproject.rest.tarjeta.repositories.TarjetaRepository;
import org.example.vivesbankproject.config.websockets.WebSocketConfig;
import org.example.vivesbankproject.rest.users.models.User;
import org.example.vivesbankproject.rest.users.repositories.UserRepository;
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
    private WebSocketConfig webSocketConfig;

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private UserRepository userRepository;

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
    void getByIban() {
        String iban = "ES1234567890123456789012";
        Cuenta cuenta = new Cuenta();
        cuenta.setIban(iban);

        TipoCuenta tipoCuenta = new TipoCuenta();
        tipoCuenta.setGuid("tipoCuenta-guid");
        cuenta.setTipoCuenta(tipoCuenta);

        Tarjeta tarjeta = new Tarjeta();
        tarjeta.setGuid("tarjeta-guid");
        cuenta.setTarjeta(tarjeta);

        Cliente cliente = new Cliente();
        cliente.setGuid("cliente-guid");
        cuenta.setCliente(cliente);

        CuentaResponse cuentaResponse = new CuentaResponse();

        when(cuentaRepository.findByIban(iban)).thenReturn(Optional.of(cuenta));
        when(cuentaMapper.toCuentaResponse(
                cuenta,
                tipoCuenta.getGuid(),
                tarjeta.getGuid(),
                cliente.getGuid()
        )).thenReturn(cuentaResponse);

        CuentaResponse result = cuentaService.getByIban(iban);

        assertNotNull(result);
        verify(cuentaRepository).findByIban(iban);
        verify(cuentaMapper).toCuentaResponse(
                cuenta,
                tipoCuenta.getGuid(),
                tarjeta.getGuid(),
                cliente.getGuid()
        );
    }

    @Test
    void getByIbanNotFound() {
        String iban = "ES9999999999999999999999";
        when(cuentaRepository.findByIban(iban)).thenReturn(Optional.empty());

        assertThrows(CuentaNotFoundByIban.class, () -> cuentaService.getByIban(iban));
        verify(cuentaRepository).findByIban(iban);
    }

    @Test
    void getByNumTarjeta() {
        String numTarjeta = "1234567812345678";

        Tarjeta tarjeta = new Tarjeta();
        tarjeta.setId(1L);
        tarjeta.setNumeroTarjeta(numTarjeta);

        Cuenta cuenta = new Cuenta();
        cuenta.setId(1L);

        TipoCuenta tipoCuenta = new TipoCuenta();
        tipoCuenta.setGuid("tipoCuenta-guid");
        cuenta.setTipoCuenta(tipoCuenta);

        Tarjeta tarjetaCuenta = new Tarjeta();
        tarjetaCuenta.setGuid("tarjeta-guid");
        cuenta.setTarjeta(tarjetaCuenta);

        Cliente cliente = new Cliente();
        cliente.setGuid("cliente-guid");
        cuenta.setCliente(cliente);

        CuentaResponse cuentaResponse = new CuentaResponse();

        when(tarjetaRepository.findByNumeroTarjeta(numTarjeta)).thenReturn(Optional.of(tarjeta));
        when(cuentaRepository.findByTarjetaId(tarjeta.getId())).thenReturn(Optional.of(cuenta));
        when(cuentaMapper.toCuentaResponse(
                cuenta,
                tipoCuenta.getGuid(),
                tarjetaCuenta.getGuid(),
                cliente.getGuid()
        )).thenReturn(cuentaResponse);

        CuentaResponse result = cuentaService.getByNumTarjeta(numTarjeta);

        assertNotNull(result);
        verify(tarjetaRepository).findByNumeroTarjeta(numTarjeta);
        verify(cuentaRepository).findByTarjetaId(tarjeta.getId());
        verify(cuentaMapper).toCuentaResponse(
                cuenta,
                tipoCuenta.getGuid(),
                tarjetaCuenta.getGuid(),
                cliente.getGuid()
        );
    }

    @Test
    void testGetByNumTarjeta_TarjetaNotFound() {
        String numTarjeta = "8765432187654321";

        when(tarjetaRepository.findByNumeroTarjeta(numTarjeta)).thenReturn(Optional.empty());

        assertThrows(TarjetaNotFoundByNumero.class, () -> cuentaService.getByNumTarjeta(numTarjeta));
        verify(tarjetaRepository).findByNumeroTarjeta(numTarjeta);
    }

    @Test
    void testGetByNumTarjeta_CuentaNotFound() {
        String numTarjeta = "1234567812345678";

        Tarjeta tarjeta = new Tarjeta();
        tarjeta.setId(1L);
        tarjeta.setNumeroTarjeta(numTarjeta);

        when(tarjetaRepository.findByNumeroTarjeta(numTarjeta)).thenReturn(Optional.of(tarjeta));
        when(cuentaRepository.findByTarjetaId(tarjeta.getId())).thenReturn(Optional.empty());

        assertThrows(CuentaNotFoundByNumTarjeta.class, () -> cuentaService.getByNumTarjeta(numTarjeta));
        verify(tarjetaRepository).findByNumeroTarjeta(numTarjeta);
        verify(cuentaRepository).findByTarjetaId(tarjeta.getId());
    }

    @Test
    void save() {
        CuentaRequest cuentaRequest = new CuentaRequest();
        cuentaRequest.setTipoCuentaId("tipo1");
        cuentaRequest.setTarjetaId("tarjeta1");
        cuentaRequest.setClienteId("cliente1");

        TipoCuenta tipoCuenta = new TipoCuenta();
        User user = new User();
        Cliente cliente = new Cliente();
        cliente.setUser(user);
        Tarjeta tarjeta = new Tarjeta();

        Cuenta cuenta = new Cuenta();
        cuenta.setIban("ES1234567890");
        cuenta.setTipoCuenta(tipoCuenta);
        cuenta.setCliente(cliente);
        cuenta.setTarjeta(tarjeta);

        when(tipoCuentaRepository.findByGuid(cuentaRequest.getTipoCuentaId())).thenReturn(Optional.of(tipoCuenta));
        when(tarjetaRepository.findByGuid(cuentaRequest.getTarjetaId())).thenReturn(Optional.of(tarjeta));
        when(clienteRepository.findByGuid(cuentaRequest.getClienteId())).thenReturn(Optional.of(cliente));
        when(userRepository.findByGuid(user.getGuid())).thenReturn(Optional.of(user));
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

        User user = new User();

        TipoCuenta tipoCuenta = new TipoCuenta();
        tipoCuenta.setGuid("tipoCuenta-guid");

        Cliente cliente = new Cliente();
        cliente.setGuid("cliente-guid");
        cliente.setUser(user);

        Tarjeta tarjeta = new Tarjeta();
        tarjeta.setGuid("tarjeta-guid");

        Cuenta cuenta = new Cuenta();
        cuenta.setGuid(cuentaId);
        cuenta.setIban("ES1234567890");
        cuenta.setTipoCuenta(tipoCuenta);
        cuenta.setCliente(cliente);
        cuenta.setTarjeta(tarjeta);

        CuentaRequestUpdate cuentaRequestUpdate = new CuentaRequestUpdate();
        cuentaRequestUpdate.setTipoCuentaId("tipoCuenta-guid");
        cuentaRequestUpdate.setTarjetaId("tarjeta-guid");
        cuentaRequestUpdate.setClienteId("cliente-guid");

        Cuenta cuentaActualizada = new Cuenta();
        cuentaActualizada.setGuid(cuentaId);
        cuentaActualizada.setIban(cuenta.getIban());
        cuentaActualizada.setTipoCuenta(tipoCuenta);
        cuentaActualizada.setCliente(cliente);
        cuentaActualizada.setTarjeta(tarjeta);
        cuentaActualizada.setCreatedAt(cuenta.getCreatedAt());
        cuentaActualizada.setUpdatedAt(cuenta.getUpdatedAt());

        CuentaResponse cuentaResponse = new CuentaResponse();
        cuentaResponse.setGuid(cuentaId);

        when(cuentaRepository.findByGuid(cuentaId)).thenReturn(Optional.of(cuenta));
        when(tipoCuentaRepository.findByGuid(cuentaRequestUpdate.getTipoCuentaId())).thenReturn(Optional.of(tipoCuenta));
        when(tarjetaRepository.findByGuid(cuentaRequestUpdate.getTarjetaId())).thenReturn(Optional.of(tarjeta));
        when(clienteRepository.findByGuid(cuentaRequestUpdate.getClienteId())).thenReturn(Optional.of(cliente));
        when(userRepository.findByGuid(user.getGuid())).thenReturn(Optional.of(user));
        when(cuentaMapper.toCuentaUpdate(cuentaRequestUpdate, cuenta, tipoCuenta, tarjeta, cliente)).thenReturn(cuentaActualizada);
        when(cuentaRepository.save(cuentaActualizada)).thenReturn(cuentaActualizada);
        when(cuentaMapper.toCuentaResponse(cuenta, cuenta.getTipoCuenta().getGuid(), cuenta.getTarjeta().getGuid(), cuenta.getCliente().getGuid())).thenReturn(cuentaResponse);

        CuentaResponse result = cuentaService.update(cuentaId, cuentaRequestUpdate);

        assertNotNull(result, "La respuesta no debe ser nula");
        assertEquals(cuentaId, result.getGuid(), "El ID de la cuenta actualizada debe coincidir");

        verify(cuentaRepository).findByGuid(cuentaId);
        verify(cuentaRepository).save(cuentaActualizada);
        verify(cuentaMapper, times(2)).toCuentaResponse(cuenta, cuenta.getTipoCuenta().getGuid(), cuenta.getTarjeta().getGuid(), cuenta.getCliente().getGuid());
        verify(tipoCuentaRepository).findByGuid(cuentaRequestUpdate.getTipoCuentaId());
        verify(tarjetaRepository).findByGuid(cuentaRequestUpdate.getTarjetaId());
        verify(clienteRepository).findByGuid(cuentaRequestUpdate.getClienteId());
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

        TipoCuenta tipoCuenta = new TipoCuenta();
        User user = new User();
        Cliente cliente = new Cliente();
        cliente.setUser(user);
        Tarjeta tarjeta = new Tarjeta();

        Cuenta cuenta = new Cuenta();
        cuenta.setGuid(cuentaId);
        cuenta.setTipoCuenta(tipoCuenta);
        cuenta.setCliente(cliente);
        cuenta.setTarjeta(tarjeta);

        CuentaResponse cuentaResponse = new CuentaResponse();
        cuentaResponse.setGuid(cuentaId);

        when(cuentaRepository.findByGuid(cuentaId)).thenReturn(Optional.of(cuenta));
        when(cuentaMapper.toCuentaResponse(cuenta, tipoCuenta.getGuid(), tarjeta.getGuid(), cliente.getGuid())).thenReturn(cuentaResponse);
        when(userRepository.findByGuid(user.getGuid())).thenReturn(Optional.of(user));

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