package org.example.vivesbankproject.movimientos.services;

import org.bson.types.ObjectId;
import org.example.vivesbankproject.rest.cliente.dto.ClienteResponse;
import org.example.vivesbankproject.rest.cliente.exceptions.ClienteNotFoundByUser;
import org.example.vivesbankproject.rest.cliente.models.Cliente;
import org.example.vivesbankproject.rest.cliente.service.ClienteService;
import org.example.vivesbankproject.rest.cuenta.dto.cuenta.CuentaRequestUpdate;
import org.example.vivesbankproject.rest.cuenta.dto.cuenta.CuentaResponse;
import org.example.vivesbankproject.rest.cuenta.exceptions.cuenta.CuentaNotFound;
import org.example.vivesbankproject.rest.cuenta.mappers.CuentaMapper;
import org.example.vivesbankproject.rest.cuenta.models.Cuenta;
import org.example.vivesbankproject.rest.cuenta.services.CuentaService;
import org.example.vivesbankproject.rest.movimientos.dto.MovimientoRequest;
import org.example.vivesbankproject.rest.movimientos.dto.MovimientoResponse;
import org.example.vivesbankproject.rest.movimientos.exceptions.domiciliacion.DuplicatedDomiciliacionException;
import org.example.vivesbankproject.rest.movimientos.exceptions.movimientos.ClienteHasNoMovements;
import org.example.vivesbankproject.rest.movimientos.exceptions.movimientos.MovimientoNotFound;
import org.example.vivesbankproject.rest.movimientos.exceptions.movimientos.NegativeAmount;
import org.example.vivesbankproject.rest.movimientos.exceptions.movimientos.UnknownIban;
import org.example.vivesbankproject.rest.movimientos.mappers.MovimientoMapper;
import org.example.vivesbankproject.rest.movimientos.models.Domiciliacion;
import org.example.vivesbankproject.rest.movimientos.models.IngresoDeNomina;
import org.example.vivesbankproject.rest.movimientos.models.Movimiento;
import org.example.vivesbankproject.rest.movimientos.models.Periodicidad;
import org.example.vivesbankproject.rest.movimientos.repositories.DomiciliacionRepository;
import org.example.vivesbankproject.rest.movimientos.repositories.MovimientosRepository;
import org.example.vivesbankproject.rest.movimientos.services.MovimientosServiceImpl;
import org.example.vivesbankproject.rest.tarjeta.service.TarjetaService;
import org.example.vivesbankproject.config.websockets.WebSocketConfig;
import org.example.vivesbankproject.rest.users.dto.UserResponse;
import org.example.vivesbankproject.rest.users.models.User;
import org.example.vivesbankproject.rest.users.services.UserService;
import org.example.vivesbankproject.websocket.notifications.dto.DomiciliacionResponse;
import org.example.vivesbankproject.websocket.notifications.mappers.NotificationMapper;
import org.example.vivesbankproject.websocket.notifications.models.Notification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovimientoServiceImplTest {

    @Mock
    private MovimientosRepository movimientosRepository;

    @Mock
    private DomiciliacionRepository domiciliacionRepository;

    @Mock
    private ClienteService clienteService;

    @Mock
    private TarjetaService tarjetaService;

    @Mock
    private CuentaService cuentaService;

    @Mock
    private CuentaMapper cuentaMapper;

    @Mock
    private NotificationMapper notificationMapper;

    @Mock
    private MovimientoMapper movimientosMapper;

    @Mock
    private WebSocketConfig webSocketConfig;

    @Mock
    private UserService userService;

    @InjectMocks
    private MovimientosServiceImpl movimientosService;

    private Movimiento movimiento;
    private MovimientoResponse movimientoResponse;
    private Domiciliacion domiciliacion;
    CuentaResponse cuenta = new CuentaResponse();
    ClienteResponse clienteResponse = new ClienteResponse();
    UserResponse userResponse = new UserResponse();

    @BeforeEach
    void setUp() {

        userResponse.setUsername("username123");

        clienteResponse.setUserId("user123");
        clienteResponse.setGuid("client123");

        cuenta.setIban("ES60123412347246753334");
        cuenta.setSaldo("100.00");
        cuenta.setClienteId("client123");

        domiciliacion = new Domiciliacion();
        domiciliacion.setActiva(true);
        domiciliacion.setUltimaEjecucion(LocalDateTime.now().minusDays(2));
        domiciliacion.setPeriodicidad(Periodicidad.DIARIA);
        domiciliacion.setIbanOrigen("ES60123412347246753334");
        domiciliacion.setCantidad(new BigDecimal("100.00"));

        movimiento = Movimiento.builder()
                .clienteGuid("client123")
                .build();

        movimientoResponse = MovimientoResponse.builder()
                .clienteGuid("client123")
                .build();
    }

    @Test
    void testGetAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Movimiento> movimientoPage = new PageImpl<>(List.of(movimiento));

        when(movimientosRepository.findAll(pageable))
                .thenReturn(movimientoPage);
        when(movimientosMapper.toMovimientoResponse(any(Movimiento.class)))
                .thenReturn(movimientoResponse);

        Page<MovimientoResponse> result = movimientosService.getAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(movimientosRepository).findAll(pageable);
    }

    @Test
    void testGetById_Existing() {
        ObjectId objectId = new ObjectId();

        when(movimientosRepository.findById(objectId))
                .thenReturn(Optional.of(movimiento));
        when(movimientosMapper.toMovimientoResponse(movimiento))
                .thenReturn(movimientoResponse);

        MovimientoResponse result = movimientosService.getById(objectId);

        assertNotNull(result);
        assertEquals("client123", result.getClienteGuid());
    }

    @Test
    void testGetById_NotFound() {
        ObjectId objectId = new ObjectId();

        when(movimientosRepository.findById(objectId))
                .thenReturn(Optional.empty());

        assertThrows(MovimientoNotFound.class,
                () -> movimientosService.getById(objectId));
    }

    @Test
    void testSave() {
        MovimientoRequest request = MovimientoRequest.builder()
                .clienteGuid("client123")
                .build();

        when(clienteService.getById(request.getClienteGuid())).thenReturn(null);
        when(movimientosMapper.toMovimiento(request)).thenReturn(movimiento);
        when(movimientosRepository.save(movimiento)).thenReturn(movimiento);
        when(movimientosMapper.toMovimientoResponse(movimiento)).thenReturn(movimientoResponse);

        MovimientoResponse result = movimientosService.save(request);

        assertNotNull(result);
        verify(movimientosRepository).save(movimiento);
    }

    @Test
    void testGetByGuid_Existing() {
        String guidMovimiento = "test-guid";

        when(movimientosRepository.findByGuid(guidMovimiento))
                .thenReturn(Optional.of(movimiento));
        when(movimientosMapper.toMovimientoResponse(movimiento))
                .thenReturn(movimientoResponse);

        MovimientoResponse result = movimientosService.getByGuid(guidMovimiento);

        assertNotNull(result);
        assertEquals("client123", result.getClienteGuid());
    }

    @Test
    void testGetByGuid_NotFound() {
        String guidMovimiento = "non-existing-guid";

        when(movimientosRepository.findByGuid(guidMovimiento))
                .thenReturn(Optional.empty());

        assertThrows(MovimientoNotFound.class,
                () -> movimientosService.getByGuid(guidMovimiento));
    }

    @Test
    void testGetByClienteGuid_Existing() {
        String clienteGuid = "client123";

        when(clienteService.getById(clienteGuid)).thenReturn(null);
        when(movimientosRepository.findByClienteGuid(clienteGuid))
                .thenReturn(Optional.of(movimiento));
        when(movimientosMapper.toMovimientoResponse(movimiento))
                .thenReturn(movimientoResponse);

        MovimientoResponse result = movimientosService.getByClienteGuid(clienteGuid);

        assertNotNull(result);
        assertEquals("client123", result.getClienteGuid());
    }

    @Test
    void testGetByClienteGuid_NoMovements() {
        String clienteGuid = "client-no-movements";

        when(clienteService.getById(clienteGuid)).thenReturn(null);
        when(movimientosRepository.findByClienteGuid(clienteGuid))
                .thenReturn(Optional.empty());

        assertThrows(ClienteHasNoMovements.class,
                () -> movimientosService.getByClienteGuid(clienteGuid));
    }

    @Test
    void testSave_InvalidClienteGuid() {
        MovimientoRequest request = MovimientoRequest.builder()
                .clienteGuid("invalid-client-guid")
                .build();

        when(clienteService.getById(request.getClienteGuid()))
                .thenThrow(new ClienteNotFoundByUser(request.getClienteGuid()));

        assertThrows(ClienteNotFoundByUser.class,
                () -> movimientosService.save(request));
    }

    @Test
    void testSaveDomiciliacion_Success() {
        // Arrange
        User user = User.builder()
                .guid(userResponse.getGuid())
                .username(userResponse.getUsername())
                .password(userResponse.getPassword())
                .roles(userResponse.getRoles())
                .build();

        DomiciliacionResponse domiciliacionResponse = new DomiciliacionResponse(
                domiciliacion.getGuid(),
                domiciliacion.getIbanOrigen(),
                domiciliacion.getIbanDestino(),
                domiciliacion.getCantidad(),
                domiciliacion.getNombreAcreedor(),
                domiciliacion.getFechaInicio().toString(),
                domiciliacion.getPeriodicidad().toString(),
                domiciliacion.getActiva(),
                domiciliacion.getUltimaEjecucion().toString()
        );


        when(clienteService.getUserAuthenticatedByGuid(userResponse.getGuid())).thenReturn(clienteResponse);
        when(cuentaService.getByIban(domiciliacion.getIbanOrigen())).thenReturn(cuenta);
        when(domiciliacionRepository.findByClienteGuid(anyString())).thenReturn(Optional.empty());
        when(domiciliacionRepository.save(any(Domiciliacion.class))).thenReturn(domiciliacion);
        when(notificationMapper.toDomiciliacionDto(domiciliacion)).thenReturn(domiciliacionResponse);
        when(clienteService.getById(anyString())).thenReturn(clienteResponse);
        when(userService.getById(anyString())).thenReturn(userResponse); // Add this line to mock the userService call

        // Act
        Domiciliacion result = movimientosService.saveDomiciliacion(user, domiciliacion);

        // Assert
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(domiciliacionResponse.guid(), result.getGuid())
        );

        verify(domiciliacionRepository).save(domiciliacion);
    }

    @Test
    void testSaveDomiciliacion_ClienteNotFound() {
        // Arrange
        User user = User.builder()
                .guid(userResponse.getGuid())
                .username(userResponse.getUsername())
                .password(userResponse.getPassword())
                .roles(userResponse.getRoles())
                .build();

        when(clienteService.getUserAuthenticatedByGuid(userResponse.getGuid())).thenReturn(null);

        // Act
        var result = assertThrows(ClienteNotFoundByUser.class, () -> movimientosService.saveDomiciliacion(user, domiciliacion));

        // Assert
        assertEquals(ClienteNotFoundByUser.class, result.getClass());

        verify(domiciliacionRepository, times(0)).save(domiciliacion);
    }

    @Test
    void testSaveDomiciliacion_CuentaNotFound() {
        // Arrange
        User user = User.builder()
                .guid(userResponse.getGuid())
                .username(userResponse.getUsername())
                .password(userResponse.getPassword())
                .roles(userResponse.getRoles())
                .build();

        when(clienteService.getUserAuthenticatedByGuid(userResponse.getGuid())).thenReturn(clienteResponse);
        when(cuentaService.getByIban(domiciliacion.getIbanOrigen())).thenReturn(null);

        // Act
        var result = assertThrows(CuentaNotFound.class, () -> movimientosService.saveDomiciliacion(user, domiciliacion));
        // Assert
        assertEquals(CuentaNotFound.class, result.getClass());

        verify(domiciliacionRepository, times(0)).save(domiciliacion);
    }

    @Test
    void testSaveDomiciliacion_IbanOrigenDiferenteQuienHaceLaPeticion() {
        // Arrange
        User user = User.builder()
                .guid(userResponse.getGuid())
                .username(userResponse.getUsername())
                .password(userResponse.getPassword())
                .roles(userResponse.getRoles())
                .build();

        clienteResponse.setGuid("different-user-guid");
        when(clienteService.getUserAuthenticatedByGuid(userResponse.getGuid())).thenReturn(clienteResponse);
        when(cuentaService.getByIban(domiciliacion.getIbanOrigen())).thenReturn(cuenta);

        // Act
        var result = assertThrows(UnknownIban.class, () -> movimientosService.saveDomiciliacion(user, domiciliacion));
        // Assert
        assertEquals(UnknownIban.class, result.getClass());

        verify(domiciliacionRepository, times(0)).save(domiciliacion);
    }

    @Test
    void testSaveDomiciliacion_DuplicatedDomiciliacion() {
        // Arrange
        User user = User.builder()
                .guid(userResponse.getGuid())
                .username(userResponse.getUsername())
                .password(userResponse.getPassword())
                .roles(userResponse.getRoles())
                .build();

        // Ensure domiciliacion has a non-null ibanDestino
        domiciliacion.setIbanDestino("ES60123412347246753335");

        when(clienteService.getUserAuthenticatedByGuid(userResponse.getGuid())).thenReturn(clienteResponse);
        when(cuentaService.getByIban(domiciliacion.getIbanOrigen())).thenReturn(cuenta);
        when(domiciliacionRepository.findByClienteGuid(anyString())).thenReturn(Optional.ofNullable(domiciliacion));

        // Act
        var result = assertThrows(DuplicatedDomiciliacionException.class, () -> movimientosService.saveDomiciliacion(user, domiciliacion));
        // Assert
        assertEquals(DuplicatedDomiciliacionException.class, result.getClass());

        verify(domiciliacionRepository, times(0)).save(domiciliacion);
    }

    @Test
    void testSaveDomiciliacion_NegativeAmount() {
        // Arrange
        User user = User.builder()
                .guid(userResponse.getGuid())
                .username(userResponse.getUsername())
                .password(userResponse.getPassword())
                .roles(userResponse.getRoles())
                .build();

        // Ensure domiciliacion has a non-null ibanDestino
        domiciliacion.setIbanDestino("ES60123412347246753335");
        domiciliacion.setCantidad(BigDecimal.valueOf(-250.75)); // Negative amount

        when(clienteService.getUserAuthenticatedByGuid(userResponse.getGuid())).thenReturn(clienteResponse);
        when(cuentaService.getByIban(domiciliacion.getIbanOrigen())).thenReturn(cuenta);
        when(domiciliacionRepository.findByClienteGuid(anyString())).thenReturn(Optional.empty());

        // Act
        var result = assertThrows(NegativeAmount.class, () -> movimientosService.saveDomiciliacion(user, domiciliacion));
        // Assert
        assertEquals(NegativeAmount.class, result.getClass());

        verify(domiciliacionRepository, times(0)).save(domiciliacion);
    }


    @Test
    void testSaveIngresoDeNomina_Success() {
        // Arrange
        User user = User.builder()
                .guid("user-guid")
                .build();

        IngresoDeNomina ingresoDeNomina = new IngresoDeNomina();
        ingresoDeNomina.setIban_Destino("ES60123412347246753334");
        ingresoDeNomina.setIban_Origen("ES60123412347246753335");
        ingresoDeNomina.setCifEmpresa("B12345678");
        ingresoDeNomina.setCantidad(100.00);


        Movimiento movimiento = Movimiento.builder()
                .clienteGuid("client-guid")
                .ingresoDeNomina(ingresoDeNomina)
                .build();

        MovimientoResponse expectedResponse = MovimientoResponse.builder()
                .clienteGuid("client-guid")
                .ingresoDeNomina(ingresoDeNomina)
                .build();

        when(clienteService.getUserAuthenticatedByGuid(user.getGuid())).thenReturn(clienteResponse);
        when(cuentaService.getByIban(ingresoDeNomina.getIban_Destino())).thenReturn(cuenta);
        when(movimientosRepository.save(any(Movimiento.class))).thenReturn(movimiento);
        when(movimientosMapper.toMovimientoResponse(movimiento)).thenReturn(expectedResponse);
        when(cuentaMapper.toCuentaRequestUpdate(any())).thenReturn(new CuentaRequestUpdate());
        when(cuentaService.getByIban(domiciliacion.getIbanOrigen())).thenReturn(cuenta);
        when(clienteService.getById(anyString())).thenReturn(clienteResponse);
        when(userService.getById(anyString())).thenReturn(userResponse);

        // Act
        MovimientoResponse actualResponse = movimientosService.saveIngresoDeNomina(user, ingresoDeNomina);

        // Assert
        assertAll(
                () -> assertEquals(expectedResponse.getClienteGuid(), actualResponse.getClienteGuid()),
                () -> assertEquals(expectedResponse.getIngresoDeNomina(), actualResponse.getIngresoDeNomina()),
                () -> assertNotEquals(null, actualResponse.getIngresoDeNomina())
        );

        verify(movimientosRepository, times(1)).save(any(Movimiento.class));
    }

    @Test
    void testSaveIngresoDeNomina_ClienteNotFound() {
        // Arrange
        User user = User.builder()
                .guid("user-guid")
                .build();

        IngresoDeNomina ingresoDeNomina = new IngresoDeNomina();
        ingresoDeNomina.setIban_Destino("ES60123412347246753334");
        ingresoDeNomina.setIban_Origen("ES60123412347246753335");
        ingresoDeNomina.setCifEmpresa("B12345678");
        ingresoDeNomina.setCantidad(100.00);


        when(clienteService.getUserAuthenticatedByGuid(user.getGuid())).thenReturn(null);

        // Act
        var result = assertThrows(ClienteNotFoundByUser.class, () -> movimientosService.saveIngresoDeNomina(user, ingresoDeNomina));

        // Assert
        assertEquals(ClienteNotFoundByUser.class, result.getClass());

        verify(movimientosRepository, times(0)).save(any(Movimiento.class));
    }
}