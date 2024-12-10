package org.example.vivesbankproject.movimientos.services;

import org.bson.types.ObjectId;
import org.example.vivesbankproject.rest.cliente.dto.ClienteResponse;
import org.example.vivesbankproject.rest.cliente.exceptions.ClienteNotFoundByUser;
import org.example.vivesbankproject.rest.cliente.models.Cliente;
import org.example.vivesbankproject.rest.cliente.service.ClienteService;
import org.example.vivesbankproject.rest.cuenta.dto.cuenta.CuentaRequestUpdate;
import org.example.vivesbankproject.rest.cuenta.dto.cuenta.CuentaResponse;
import org.example.vivesbankproject.rest.cuenta.exceptions.cuenta.CuentaNotFound;
import org.example.vivesbankproject.rest.cuenta.exceptions.cuenta.CuentaNotFoundByClienteGuid;
import org.example.vivesbankproject.rest.cuenta.exceptions.cuenta.CuentaNotFoundByTarjetaId;
import org.example.vivesbankproject.rest.cuenta.mappers.CuentaMapper;
import org.example.vivesbankproject.rest.cuenta.models.Cuenta;
import org.example.vivesbankproject.rest.cuenta.services.CuentaService;
import org.example.vivesbankproject.rest.movimientos.dto.MovimientoRequest;
import org.example.vivesbankproject.rest.movimientos.dto.MovimientoResponse;
import org.example.vivesbankproject.rest.movimientos.exceptions.domiciliacion.DuplicatedDomiciliacionException;
import org.example.vivesbankproject.rest.movimientos.exceptions.domiciliacion.SaldoInsuficienteException;
import org.example.vivesbankproject.rest.movimientos.exceptions.movimientos.*;
import org.example.vivesbankproject.rest.movimientos.mappers.MovimientoMapper;
import org.example.vivesbankproject.rest.movimientos.models.*;
import org.example.vivesbankproject.rest.movimientos.repositories.DomiciliacionRepository;
import org.example.vivesbankproject.rest.movimientos.repositories.MovimientosRepository;
import org.example.vivesbankproject.rest.movimientos.services.MovimientosServiceImpl;
import org.example.vivesbankproject.rest.tarjeta.dto.TarjetaResponse;
import org.example.vivesbankproject.rest.tarjeta.exceptions.TarjetaNotFoundByNumero;
import org.example.vivesbankproject.rest.tarjeta.models.Tarjeta;
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
import java.util.ArrayList;
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

    @Test
    void testSaveIngresoDeNomina_CuentaNotFound() {
        // Arrange
        User user = User.builder()
                .guid("user-guid")
                .build();

        IngresoDeNomina ingresoDeNomina = new IngresoDeNomina();
        ingresoDeNomina.setIban_Destino("ES60123412347246753334");
        ingresoDeNomina.setIban_Origen("ES60123412347246753335");
        ingresoDeNomina.setCifEmpresa("B12345678");
        ingresoDeNomina.setCantidad(100.00);


        when(clienteService.getUserAuthenticatedByGuid(user.getGuid())).thenReturn(clienteResponse);
        when(cuentaService.getByIban(ingresoDeNomina.getIban_Destino())).thenReturn(null);


        // Act
        var result = assertThrows(CuentaNotFound.class, () -> movimientosService.saveIngresoDeNomina(user, ingresoDeNomina));

        // Assert
        assertEquals(CuentaNotFound.class, result.getClass());

        verify(movimientosRepository, times(0)).save(any(Movimiento.class));
    }

    @Test
    void testSaveIngresoDeNomina_NegativeAmount() {
        // Arrange
        User user = User.builder()
                .guid("user-guid")
                .build();

        IngresoDeNomina ingresoDeNomina = new IngresoDeNomina();
        ingresoDeNomina.setIban_Destino("ES60123412347246753334");
        ingresoDeNomina.setIban_Origen("ES60123412347246753335");
        ingresoDeNomina.setCifEmpresa("B12345678");
        ingresoDeNomina.setCantidad(-100.00);



        when(clienteService.getUserAuthenticatedByGuid(user.getGuid())).thenReturn(clienteResponse);
        when(cuentaService.getByIban(ingresoDeNomina.getIban_Destino())).thenReturn(cuenta);


        // Act
        var result = assertThrows(NegativeAmount.class, () -> movimientosService.saveIngresoDeNomina(user, ingresoDeNomina));

        // Assert
        assertEquals(NegativeAmount.class, result.getClass());

        verify(movimientosRepository, times(0)).save(any(Movimiento.class));
    }

    @Test
    void testSavePagoConTarjeta_Success() {
        // Arrange
        User user = User.builder()
                .guid("user-guid")
                .build();

        PagoConTarjeta pagoConTarjeta = PagoConTarjeta.builder()
                .numeroTarjeta("1234567812345678")
                .cantidad(100.0)
                .build();


        Tarjeta clienteTarjeta = new Tarjeta();
        clienteTarjeta.setGuid("tarjeta-guid");
        clienteTarjeta.setNumeroTarjeta("4149434231419594");

        TarjetaResponse clienteTarjetaResponse = new TarjetaResponse();
        clienteTarjetaResponse.setGuid("tarjeta-guid");
        clienteTarjetaResponse.setNumeroTarjeta("4149434231419594");


        cuenta.setTarjetaId(clienteTarjeta.getGuid());
        cuenta.setClienteId(clienteResponse.getGuid());

        movimiento.setPagoConTarjeta(pagoConTarjeta);
        movimientoResponse.setPagoConTarjeta(pagoConTarjeta);

        when(clienteService.getUserAuthenticatedByGuid(user.getGuid())).thenReturn(clienteResponse);
        when(tarjetaService.getByNumeroTarjeta(pagoConTarjeta.getNumeroTarjeta())).thenReturn(clienteTarjetaResponse);
        when(cuentaService.getAllCuentasByClienteGuid(clienteResponse.getGuid())).thenReturn(new ArrayList<>(List.of(cuenta)));
        when(cuentaService.getByNumTarjeta(clienteTarjeta.getNumeroTarjeta())).thenReturn(cuenta);
        when(clienteService.getById(cuenta.getClienteId())).thenReturn(clienteResponse);
        when(userService.getById(clienteResponse.getUserId())).thenReturn(userResponse);
        when(movimientosRepository.save(any(Movimiento.class))).thenReturn(movimiento);
        when(movimientosMapper.toMovimientoResponse(any(Movimiento.class))).thenReturn(movimientoResponse);

        // Act
        MovimientoResponse response = movimientosService.savePagoConTarjeta(user, pagoConTarjeta);

        // Assert
        assertNotNull(response.getPagoConTarjeta());

        verify(movimientosRepository).save(any(Movimiento.class));
        verify(movimientosMapper).toMovimientoResponse(any(Movimiento.class));
    }

    @Test
    void testSavePagoConTarjeta_ClientNotFound() {
        // Arrange
        User user = User.builder()
                .guid("user-guid")
                .build();

        PagoConTarjeta pagoConTarjeta = PagoConTarjeta.builder()
                .numeroTarjeta("1234567812345678")
                .cantidad(100.0)
                .build();

        when(clienteService.getUserAuthenticatedByGuid(user.getGuid())).thenReturn(null);

        // Act
        var response = assertThrows(ClienteNotFoundByUser.class, () -> movimientosService.savePagoConTarjeta(user, pagoConTarjeta));

        // Assert
        assertEquals(ClienteNotFoundByUser.class, response.getClass());

        verify(movimientosRepository, times(0)).save(any(Movimiento.class));
    }

    @Test
    void testSavePagoConTarjeta_TarjetaNotFound() {
        // Arrange
        User user = User.builder()
                .guid("user-guid")
                .build();

        PagoConTarjeta pagoConTarjeta = PagoConTarjeta.builder()
                .numeroTarjeta("1234567812345678")
                .cantidad(100.0)
                .build();

        when(clienteService.getUserAuthenticatedByGuid(user.getGuid())).thenReturn(clienteResponse);
        when(tarjetaService.getByNumeroTarjeta(pagoConTarjeta.getNumeroTarjeta())).thenReturn(null);


        // Act
        var response = assertThrows(TarjetaNotFoundByNumero.class, () -> movimientosService.savePagoConTarjeta(user, pagoConTarjeta));

        // Assert
        assertEquals(TarjetaNotFoundByNumero.class, response.getClass());

        verify(movimientosRepository, times(0)).save(any(Movimiento.class));
    }

    @Test
    void testSavePagoConTarjeta_CuentaNotFoundByClienteGuid() {
        // Arrange
        User user = User.builder()
                .guid("user-guid")
                .build();

        PagoConTarjeta pagoConTarjeta = PagoConTarjeta.builder()
                .numeroTarjeta("1234567812345678")
                .cantidad(100.0)
                .build();

        TarjetaResponse clienteTarjetaResponse = new TarjetaResponse();
        clienteTarjetaResponse.setGuid("tarjeta-guid");
        clienteTarjetaResponse.setNumeroTarjeta("4149434231419594");

        when(clienteService.getUserAuthenticatedByGuid(user.getGuid())).thenReturn(clienteResponse);
        when(tarjetaService.getByNumeroTarjeta(pagoConTarjeta.getNumeroTarjeta())).thenReturn(clienteTarjetaResponse);
        when(cuentaService.getAllCuentasByClienteGuid(clienteResponse.getGuid())).thenReturn(null);


        // Act
        var response = assertThrows(CuentaNotFoundByClienteGuid.class, () -> movimientosService.savePagoConTarjeta(user, pagoConTarjeta));

        // Assert
        assertEquals(CuentaNotFoundByClienteGuid.class, response.getClass());

        verify(movimientosRepository, times(0)).save(any(Movimiento.class));
    }

    @Test
    void testSavePagoConTarjeta_CuentaNotFoundByTarjetaId() {
        // Arrange
        User user = User.builder()
                .guid("user-guid")
                .build();

        PagoConTarjeta pagoConTarjeta = PagoConTarjeta.builder()
                .numeroTarjeta("1234567812345678")
                .cantidad(100.0)
                .build();

        TarjetaResponse clienteTarjetaResponse = new TarjetaResponse();
        clienteTarjetaResponse.setGuid("tarjeta-guid");
        clienteTarjetaResponse.setNumeroTarjeta("4149434231419594");

        cuenta.setTarjetaId("error");
        cuenta.setClienteId(clienteResponse.getGuid());

        when(clienteService.getUserAuthenticatedByGuid(user.getGuid())).thenReturn(clienteResponse);
        when(tarjetaService.getByNumeroTarjeta(pagoConTarjeta.getNumeroTarjeta())).thenReturn(clienteTarjetaResponse);
        when(cuentaService.getAllCuentasByClienteGuid(clienteResponse.getGuid())).thenReturn(new ArrayList<>(List.of(cuenta)));


        // Act
        var response = assertThrows(CuentaNotFoundByTarjetaId.class, () -> movimientosService.savePagoConTarjeta(user, pagoConTarjeta));

        // Assert
        assertEquals(CuentaNotFoundByTarjetaId.class, response.getClass());

        verify(movimientosRepository, times(0)).save(any(Movimiento.class));
    }

    @Test
    void testSavePagoConTarjeta_NegativeAmount() {
        // Arrange
        User user = User.builder()
                .guid("user-guid")
                .build();

        PagoConTarjeta pagoConTarjeta = PagoConTarjeta.builder()
                .numeroTarjeta("1234567812345678")
                .cantidad(-100.0)
                .build();

        Tarjeta clienteTarjeta = new Tarjeta();
        clienteTarjeta.setGuid("tarjeta-guid");
        clienteTarjeta.setNumeroTarjeta("4149434231419594");

        TarjetaResponse clienteTarjetaResponse = new TarjetaResponse();
        clienteTarjetaResponse.setGuid("tarjeta-guid");
        clienteTarjetaResponse.setNumeroTarjeta("4149434231419594");


        cuenta.setTarjetaId(clienteTarjeta.getGuid());
        cuenta.setClienteId(clienteResponse.getGuid());

        when(clienteService.getUserAuthenticatedByGuid(user.getGuid())).thenReturn(clienteResponse);
        when(tarjetaService.getByNumeroTarjeta(pagoConTarjeta.getNumeroTarjeta())).thenReturn(clienteTarjetaResponse);
        when(cuentaService.getAllCuentasByClienteGuid(clienteResponse.getGuid())).thenReturn(new ArrayList<>(List.of(cuenta)));


        // Act
        var response = assertThrows(NegativeAmount.class, () -> movimientosService.savePagoConTarjeta(user, pagoConTarjeta));

        // Assert
        assertEquals(NegativeAmount.class, response.getClass());

        verify(movimientosRepository, times(0)).save(any(Movimiento.class));
    }

    @Test
    void testSavePagoConTarjeta_SaldoInsuficienteException() {
        // Arrange
        User user = User.builder()
                .guid("user-guid")
                .build();

        PagoConTarjeta pagoConTarjeta = PagoConTarjeta.builder()
                .numeroTarjeta("1234567812345678")
                .cantidad(100.0)
                .build();

        Tarjeta clienteTarjeta = new Tarjeta();
        clienteTarjeta.setGuid("tarjeta-guid");
        clienteTarjeta.setNumeroTarjeta("4149434231419594");

        TarjetaResponse clienteTarjetaResponse = new TarjetaResponse();
        clienteTarjetaResponse.setGuid("tarjeta-guid");
        clienteTarjetaResponse.setNumeroTarjeta("4149434231419594");


        cuenta.setTarjetaId(clienteTarjeta.getGuid());
        cuenta.setClienteId(clienteResponse.getGuid());
        cuenta.setSaldo("0.0");

        when(clienteService.getUserAuthenticatedByGuid(user.getGuid())).thenReturn(clienteResponse);
        when(tarjetaService.getByNumeroTarjeta(pagoConTarjeta.getNumeroTarjeta())).thenReturn(clienteTarjetaResponse);
        when(cuentaService.getAllCuentasByClienteGuid(clienteResponse.getGuid())).thenReturn(new ArrayList<>(List.of(cuenta)));


        // Act
        var response = assertThrows(SaldoInsuficienteException.class, () -> movimientosService.savePagoConTarjeta(user, pagoConTarjeta));

        // Assert
        assertEquals(SaldoInsuficienteException.class, response.getClass());

        verify(movimientosRepository, times(0)).save(any(Movimiento.class));
    }

    @Test
    void testSaveTransferencia_Success() {
        // Arrange
        User user = User.builder()
                .guid("user-guid")
                .build();

        Transferencia transferencia = Transferencia.builder()
                .iban_Origen("ES60123412347246753334")
                .iban_Destino("ES60123412347246753334")
                .cantidad(new BigDecimal("100.00"))
                .nombreBeneficiario("Beneficiario")
                .build();

        Movimiento movimiento = Movimiento.builder()
                .clienteGuid("client-guid")
                .transferencia(transferencia)
                .build();

        MovimientoResponse expectedResponse = MovimientoResponse.builder()
                .clienteGuid("client-guid")
                .transferencia(transferencia)
                .build();


        cuenta.setClienteId(clienteResponse.getGuid());

        when(clienteService.getUserAuthenticatedByGuid(user.getGuid())).thenReturn(clienteResponse);
        when(cuentaService.getByIban(transferencia.getIban_Origen())).thenReturn(cuenta);
        when(cuentaService.getByIban(transferencia.getIban_Destino())).thenReturn(cuenta);
        when(movimientosRepository.save(any(Movimiento.class))).thenReturn(movimiento);
        when(movimientosMapper.toMovimientoResponse(movimiento)).thenReturn(expectedResponse);
        when(cuentaMapper.toCuentaRequestUpdate(any())).thenReturn(new CuentaRequestUpdate());
        when(userService.getById(clienteResponse.getUserId())).thenReturn(userResponse);
        when(clienteService.getById(anyString())).thenReturn(clienteResponse);



        // Act
        MovimientoResponse actualResponse = movimientosService.saveTransferencia(user, transferencia);

        // Assert
        assertAll(
                () -> assertEquals(expectedResponse.getClienteGuid(), actualResponse.getClienteGuid()),
                () -> assertEquals(expectedResponse.getTransferencia(), actualResponse.getTransferencia()),
                () -> assertNotEquals(null, actualResponse.getTransferencia())
        );

        verify(movimientosRepository, times(2)).save(any(Movimiento.class));
    }

    @Test
    void testSaveTransferencia_ClienteNotFound() {
        // Arrange
        User user = User.builder()
                .guid("user-guid")
                .build();

        Transferencia transferencia = Transferencia.builder()
                .iban_Origen("ES60123412347246753334")
                .iban_Destino("ES60123412347246753334")
                .cantidad(new BigDecimal("100.00"))
                .nombreBeneficiario("Beneficiario")
                .build();

        cuenta.setClienteId(clienteResponse.getGuid());

        when(clienteService.getUserAuthenticatedByGuid(user.getGuid())).thenReturn(null);



        // Act
        var actualResponse = assertThrows(ClienteNotFoundByUser.class, () -> movimientosService.saveTransferencia(user, transferencia));

        // Assert
        assertEquals(ClienteNotFoundByUser.class, actualResponse.getClass());

        verify(movimientosRepository, times(0)).save(any(Movimiento.class));
    }

    @Test
    void testSaveTransferencia_CuentaNotFound() {
        // Arrange
        User user = User.builder()
                .guid("user-guid")
                .build();

        Transferencia transferencia = Transferencia.builder()
                .iban_Origen("ES60123412347246753334")
                .iban_Destino("ES60123412347246753334")
                .cantidad(new BigDecimal("100.00"))
                .nombreBeneficiario("Beneficiario")
                .build();

        cuenta.setClienteId(clienteResponse.getGuid());

        when(clienteService.getUserAuthenticatedByGuid(user.getGuid())).thenReturn(clienteResponse);
        when(cuentaService.getByIban(transferencia.getIban_Origen())).thenReturn(null);



        // Act
        var actualResponse = assertThrows(CuentaNotFound.class, () -> movimientosService.saveTransferencia(user, transferencia));

        // Assert
        assertEquals(CuentaNotFound.class, actualResponse.getClass());

        verify(movimientosRepository, times(0)).save(any(Movimiento.class));
    }

    @Test
    void testSaveTransferencia_UnknownIban() {
        // Arrange
        User user = User.builder()
                .guid("user-guid")
                .build();

        Transferencia transferencia = Transferencia.builder()
                .iban_Origen("ES60123412347246753334")
                .iban_Destino("ES60123412347246753334")
                .cantidad(new BigDecimal("100.00"))
                .nombreBeneficiario("Beneficiario")
                .build();

        cuenta.setClienteId("diferente-guid");

        when(clienteService.getUserAuthenticatedByGuid(user.getGuid())).thenReturn(clienteResponse);
        when(cuentaService.getByIban(transferencia.getIban_Origen())).thenReturn(cuenta);

        // Act
        var actualResponse = assertThrows(UnknownIban.class, () -> movimientosService.saveTransferencia(user, transferencia));

        // Assert
        assertEquals(UnknownIban.class, actualResponse.getClass());

        verify(movimientosRepository, times(0)).save(any(Movimiento.class));
    }

    @Test
    void testSaveTransferencia_CuentaDestinoNotFound() {
        // Arrange
        User user = User.builder()
                .guid("user-guid")
                .build();

        Transferencia transferencia = Transferencia.builder()
                .iban_Origen("ES60123412347246753334")
                .iban_Destino("ES60123412347246753335")
                .cantidad(new BigDecimal("100.00"))
                .nombreBeneficiario("Beneficiario")
                .build();

        when(clienteService.getUserAuthenticatedByGuid(user.getGuid())).thenReturn(clienteResponse);
        when(cuentaService.getByIban(transferencia.getIban_Origen())).thenReturn(cuenta);

        // Act
        var actualResponse = assertThrows(CuentaNotFound.class, () -> movimientosService.saveTransferencia(user, transferencia));

        // Assert
        assertEquals(CuentaNotFound.class, actualResponse.getClass());

        verify(movimientosRepository, times(0)).save(any(Movimiento.class));
    }

    @Test
    void testSaveTransferencia_NegativeAmount() {
        // Arrange
        User user = User.builder()
                .guid("user-guid")
                .build();

        Transferencia transferencia = Transferencia.builder()
                .iban_Origen("ES60123412347246753334")
                .iban_Destino("ES60123412347246753334")
                .cantidad(new BigDecimal("-100.00"))
                .nombreBeneficiario("Beneficiario")
                .build();

        when(clienteService.getUserAuthenticatedByGuid(user.getGuid())).thenReturn(clienteResponse);
        when(cuentaService.getByIban(transferencia.getIban_Origen())).thenReturn(cuenta);

        // Act
        var actualResponse = assertThrows(NegativeAmount.class, () -> movimientosService.saveTransferencia(user, transferencia));

        // Assert
        assertEquals(NegativeAmount.class, actualResponse.getClass());

        verify(movimientosRepository, times(0)).save(any(Movimiento.class));
    }

    @Test
    void testSaveTransferencia_SaldoInsuficiente() {
        // Arrange
        User user = User.builder()
                .guid("user-guid")
                .build();

        Transferencia transferencia = Transferencia.builder()
                .iban_Origen("ES60123412347246753334")
                .iban_Destino("ES60123412347246753334")
                .cantidad(new BigDecimal("100.00"))
                .nombreBeneficiario("Beneficiario")
                .build();

        cuenta.setSaldo("0.0");
        when(clienteService.getUserAuthenticatedByGuid(user.getGuid())).thenReturn(clienteResponse);
        when(cuentaService.getByIban(transferencia.getIban_Origen())).thenReturn(cuenta);

        // Act
        var actualResponse = assertThrows(SaldoInsuficienteException.class, () -> movimientosService.saveTransferencia(user, transferencia));

        // Assert
        assertEquals(SaldoInsuficienteException.class, actualResponse.getClass());

        verify(movimientosRepository, times(0)).save(any(Movimiento.class));
    }

    @Test
    void testRevocarTransferencia_Success() {
        // Arrange
        User user = User.builder()
                .guid("user-guid")
                .build();

        Transferencia transferencia = Transferencia.builder()
                .iban_Origen("ES60123412347246753334")
                .iban_Destino("ES60123412347246753335")
                .cantidad(new BigDecimal("100.00"))
                .nombreBeneficiario("Beneficiario")
                .build();

        Movimiento movimiento = Movimiento.builder()
                .id(new ObjectId())
                .clienteGuid(clienteResponse.getGuid())
                .transferencia(transferencia)
                .build();

        MovimientoResponse expectedResponse = MovimientoResponse.builder()
                .clienteGuid("client-guid")
                .transferencia(transferencia)
                .build();


        cuenta.setClienteId(clienteResponse.getGuid());

        when(movimientosRepository.findByGuid(movimiento.getGuid())).thenReturn(Optional.of(movimiento));
        when(clienteService.getUserAuthenticatedByGuid(user.getGuid())).thenReturn(clienteResponse);
        when(cuentaService.getByIban(transferencia.getIban_Origen())).thenReturn(cuenta);
        when(cuentaService.getByIban(transferencia.getIban_Destino())).thenReturn(cuenta);
        when(movimientosRepository.findByGuid(movimiento.getTransferencia().getMovimientoDestino())).thenReturn(Optional.of(movimiento));
        when(movimientosMapper.toMovimientoResponse(movimiento)).thenReturn(expectedResponse);
        when(clienteService.getById(anyString())).thenReturn(clienteResponse);
        when(userService.getById(clienteResponse.getUserId())).thenReturn(userResponse);

        // Act
        MovimientoResponse response = movimientosService.revocarTransferencia(user, movimiento.getGuid());

        // Assert
        assertNotNull(response);
        verify(movimientosRepository, times(2)).save(any(Movimiento.class));
    }

    @Test
    void testRevocarTransferencia_MovimientoNotFound() {
        // Arrange
        User user = User.builder()
                .guid("user-guid")
                .build();

        Transferencia transferencia = Transferencia.builder()
                .iban_Origen("ES60123412347246753334")
                .iban_Destino("ES60123412347246753335")
                .cantidad(new BigDecimal("100.00"))
                .nombreBeneficiario("Beneficiario")
                .build();

        Movimiento movimiento = Movimiento.builder()
                .id(new ObjectId())
                .clienteGuid(clienteResponse.getGuid())
                .transferencia(transferencia)
                .build();

        MovimientoResponse expectedResponse = MovimientoResponse.builder()
                .clienteGuid("client-guid")
                .transferencia(transferencia)
                .build();


        cuenta.setClienteId(clienteResponse.getGuid());

        when(movimientosRepository.findByGuid(movimiento.getGuid())).thenReturn(Optional.empty());


        // Act
        var response = assertThrows(MovimientoNotFound.class, () -> movimientosService.revocarTransferencia(user, movimiento.getGuid()));

        // Assert
        assertEquals(MovimientoNotFound.class, response.getClass());
        verify(movimientosRepository, times(0)).save(any(Movimiento.class));
    }

    @Test
    void testRevocarTransferencia_NoRevocable() {
        // Arrange
        User user = User.builder()
                .guid("user-guid")
                .build();

        Transferencia transferencia = Transferencia.builder()
                .iban_Origen("ES60123412347246753334")
                .iban_Destino("ES60123412347246753335")
                .cantidad(new BigDecimal("100.00"))
                .nombreBeneficiario("Beneficiario")
                .build();

        Movimiento movimiento = Movimiento.builder()
                .id(new ObjectId())
                .clienteGuid(clienteResponse.getGuid())
                .transferencia(transferencia)
                .createdAt(LocalDateTime.now().minusDays(2))
                .build();

        MovimientoResponse expectedResponse = MovimientoResponse.builder()
                .clienteGuid("client-guid")
                .transferencia(transferencia)
                .build();



        cuenta.setClienteId(clienteResponse.getGuid());

        when(movimientosRepository.findByGuid(movimiento.getGuid())).thenReturn(Optional.of(movimiento));

        // Act
        var response = assertThrows(TransferenciaNoRevocableException.class, () -> movimientosService.revocarTransferencia(user, movimiento.getGuid()));

        // Assert
        assertEquals(TransferenciaNoRevocableException.class, response.getClass());
        verify(movimientosRepository, times(0)).save(any(Movimiento.class));
    }

    @Test
    void testRevocarTransferencia_NoEsTransferencia() {
        // Arrange
        User user = User.builder()
                .guid("user-guid")
                .build();

        Transferencia transferencia = Transferencia.builder()
                .iban_Origen("ES60123412347246753334")
                .iban_Destino("ES60123412347246753335")
                .cantidad(new BigDecimal("100.00"))
                .nombreBeneficiario("Beneficiario")
                .build();

        Movimiento movimiento = Movimiento.builder()
                .id(new ObjectId())
                .clienteGuid(clienteResponse.getGuid())
                .build();

        MovimientoResponse expectedResponse = MovimientoResponse.builder()
                .clienteGuid("client-guid")
                .transferencia(transferencia)
                .build();



        cuenta.setClienteId(clienteResponse.getGuid());

        when(movimientosRepository.findByGuid(movimiento.getGuid())).thenReturn(Optional.of(movimiento));

        // Act
        var response = assertThrows(MovimientoIsNotTransferenciaException.class, () -> movimientosService.revocarTransferencia(user, movimiento.getGuid()));

        // Assert
        assertEquals(MovimientoIsNotTransferenciaException.class, response.getClass());
        verify(movimientosRepository, times(0)).save(any(Movimiento.class));
    }

    @Test
    void testRevocarTransferencia_UnknownIban() {
        // Arrange
        User user = User.builder()
                .guid("user-guid")
                .build();

        Transferencia transferencia = Transferencia.builder()
                .iban_Origen("ES60123412347246753334")
                .iban_Destino("ES60123412347246753335")
                .cantidad(new BigDecimal("100.00"))
                .nombreBeneficiario("Beneficiario")
                .build();

        Movimiento movimiento = Movimiento.builder()
                .id(new ObjectId())
                .clienteGuid("error")
                .transferencia(transferencia)
                .build();

        MovimientoResponse expectedResponse = MovimientoResponse.builder()
                .clienteGuid("errer")
                .transferencia(transferencia)
                .build();



        cuenta.setClienteId("diferente_dasd");

        when(movimientosRepository.findByGuid(movimiento.getGuid())).thenReturn(Optional.of(movimiento));
        when(clienteService.getUserAuthenticatedByGuid(user.getGuid())).thenReturn(clienteResponse);

        // Act
        var response = assertThrows(UnknownIban.class, () -> movimientosService.revocarTransferencia(user, movimiento.getGuid()));

        // Assert
        assertEquals(UnknownIban.class, response.getClass());
        verify(movimientosRepository, times(0)).save(any(Movimiento.class));
    }
}