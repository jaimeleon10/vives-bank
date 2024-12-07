package org.example.vivesbankproject.movimientos.services;

import org.bson.types.ObjectId;
import org.example.vivesbankproject.cliente.exceptions.ClienteNotFoundByUser;
import org.example.vivesbankproject.cliente.service.ClienteService;
import org.example.vivesbankproject.cuenta.services.CuentaService;
import org.example.vivesbankproject.movimientos.dto.MovimientoRequest;
import org.example.vivesbankproject.movimientos.dto.MovimientoResponse;
import org.example.vivesbankproject.movimientos.exceptions.movimientos.ClienteHasNoMovements;
import org.example.vivesbankproject.movimientos.exceptions.movimientos.MovimientoNotFound;
import org.example.vivesbankproject.movimientos.mappers.MovimientoMapper;
import org.example.vivesbankproject.movimientos.models.*;
import org.example.vivesbankproject.movimientos.repositories.DomiciliacionRepository;
import org.example.vivesbankproject.movimientos.repositories.MovimientosRepository;
import org.example.vivesbankproject.tarjeta.service.TarjetaService;
import org.example.vivesbankproject.websocket.notifications.config.WebSocketConfig;
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
    private MovimientoMapper movimientosMapper;

    @Mock
    private WebSocketConfig webSocketConfig;

    @InjectMocks
    private MovimientosServiceImpl movimientosService;

    private Movimiento movimiento;
    private MovimientoResponse movimientoResponse;

    @BeforeEach
    void setUp() {
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
}