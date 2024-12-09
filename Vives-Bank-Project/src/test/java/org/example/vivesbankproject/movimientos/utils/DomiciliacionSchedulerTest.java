package org.example.vivesbankproject.movimientos.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.vivesbankproject.config.websockets.WebSocketConfig;
import org.example.vivesbankproject.config.websockets.WebSocketHandler;
import org.example.vivesbankproject.rest.cliente.dto.ClienteResponse;
import org.example.vivesbankproject.rest.cuenta.dto.cuenta.CuentaRequestUpdate;
import org.example.vivesbankproject.rest.cuenta.dto.cuenta.CuentaResponse;
import org.example.vivesbankproject.rest.movimientos.models.Periodicidad;
import org.example.vivesbankproject.rest.movimientos.repositories.DomiciliacionRepository;
import org.example.vivesbankproject.rest.movimientos.repositories.MovimientosRepository;
import org.example.vivesbankproject.rest.cuenta.services.CuentaService;
import org.example.vivesbankproject.rest.cuenta.mappers.CuentaMapper;
import org.example.vivesbankproject.rest.movimientos.utils.DomiciliacionScheduler;
import org.example.vivesbankproject.rest.users.dto.UserResponse;
import org.example.vivesbankproject.rest.users.services.UserService;
import org.example.vivesbankproject.rest.cliente.service.ClienteService;
import org.example.vivesbankproject.rest.movimientos.models.Domiciliacion;
import org.example.vivesbankproject.rest.movimientos.models.Movimiento;

import org.example.vivesbankproject.websocket.notifications.mappers.NotificationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DomiciliacionSchedulerTest {

    @Mock
    private DomiciliacionRepository domiciliacionRepository;

    @Mock
    private MovimientosRepository movimientosRepository;

    @Mock
    private CuentaService cuentaService;

    @Mock
    private CuentaMapper cuentaMapper;

    @Mock
    private UserService userService;

    @Mock
    private WebSocketConfig webSocketConfig;

    @Mock
    private NotificationMapper notificationMapper;

    @Mock
    private ClienteService clienteService;

    @Mock
    private WebSocketHandler webSocketService;

    @Mock
    ObjectMapper mockMapper = mock(ObjectMapper.class);


    @InjectMocks
    private DomiciliacionScheduler domiciliacionScheduler;

    private Domiciliacion domiciliacion;
    CuentaResponse cuenta = new CuentaResponse();
    ClienteResponse clienteResponse = new ClienteResponse();
    UserResponse userResponse = new UserResponse();


    @BeforeEach
    void setUp() {
        userResponse.setUsername("username123");

        clienteResponse.setUserId("user123");

        cuenta.setIban("IBAN123");
        cuenta.setSaldo("100.00");
        cuenta.setClienteId("CLIENTE123");

        domiciliacion = new Domiciliacion();
        domiciliacion.setActiva(true);
        domiciliacion.setUltimaEjecucion(LocalDateTime.now().minusDays(2));
        domiciliacion.setPeriodicidad(Periodicidad.DIARIA);
        domiciliacion.setIbanOrigen("IBAN123");
        domiciliacion.setCantidad(new BigDecimal("100.00"));
    }

    @Test
    void testProcesarDomiciliaciones() {

        when(clienteService.getById("CLIENTE123")).thenReturn(clienteResponse);
        when(userService.getById("user123")).thenReturn(userResponse);
        when(domiciliacionRepository.findAll()).thenReturn(List.of(domiciliacion));
        when(cuentaService.getByIban("IBAN123")).thenReturn(cuenta);
        when(cuentaMapper.toCuentaRequestUpdate(any())).thenReturn(new CuentaRequestUpdate());

        domiciliacionScheduler.procesarDomiciliaciones();

        verify(domiciliacionRepository).save(domiciliacion);
        verify(movimientosRepository).save(any(Movimiento.class));
    }

    @Test
    void testProcesarDomiciliaciones_InvalidSaldo_mensual() {
        cuenta.setSaldo("0.00");
        domiciliacion.setPeriodicidad(Periodicidad.MENSUAL);
        domiciliacion.setUltimaEjecucion(LocalDateTime.now().minusMonths(2));

        when(domiciliacionRepository.findAll()).thenReturn(List.of(domiciliacion));
        when(cuentaService.getByIban("IBAN123")).thenReturn(cuenta);

        domiciliacionScheduler.procesarDomiciliaciones();

        verify(domiciliacionRepository, times(0)).save(domiciliacion);
        verify(movimientosRepository, times(0)).save(any(Movimiento.class));
    }

    @Test
    void testProcesarDomiciliaciones_InvalidSaldo_semanal() {
        cuenta.setSaldo("0.00");
        domiciliacion.setPeriodicidad(Periodicidad.MENSUAL);
        domiciliacion.setUltimaEjecucion(LocalDateTime.now().minusWeeks(2));

        when(domiciliacionRepository.findAll()).thenReturn(List.of(domiciliacion));


        domiciliacionScheduler.procesarDomiciliaciones();

        verify(domiciliacionRepository, times(0)).save(domiciliacion);
        verify(movimientosRepository, times(0)).save(any(Movimiento.class));
    }

    @Test
    void testProcesarDomiciliaciones_InvalidSaldo_anual() {
        cuenta.setSaldo("0.00");
        domiciliacion.setPeriodicidad(Periodicidad.MENSUAL);
        domiciliacion.setUltimaEjecucion(LocalDateTime.now().minusYears(2));

        when(domiciliacionRepository.findAll()).thenReturn(List.of(domiciliacion));
        when(cuentaService.getByIban("IBAN123")).thenReturn(cuenta);

        domiciliacionScheduler.procesarDomiciliaciones();

        verify(domiciliacionRepository, times(0)).save(domiciliacion);
        verify(movimientosRepository, times(0)).save(any(Movimiento.class));
    }

    @Test
    void onChangeDomiciliacionEjecutada() {
    }

    @Test
    void setWebSocketService() {
    }
}