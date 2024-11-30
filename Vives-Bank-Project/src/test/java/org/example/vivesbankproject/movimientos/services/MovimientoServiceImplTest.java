package org.example.vivesbankproject.movimientos.services;

import org.bson.types.ObjectId;
import org.example.vivesbankproject.cliente.exceptions.ClienteNotFound;
import org.example.vivesbankproject.cliente.models.Cliente;
import org.example.vivesbankproject.cliente.service.ClienteService;
import org.example.vivesbankproject.movimientos.dto.MovimientoRequest;
import org.example.vivesbankproject.movimientos.dto.MovimientoResponse;
import org.example.vivesbankproject.movimientos.exceptions.movimientos.MovimientoNotFound;
import org.example.vivesbankproject.movimientos.mappers.MovimientoMapper;
import org.example.vivesbankproject.movimientos.models.*;
import org.example.vivesbankproject.movimientos.repositories.MovimientosRepository;
import org.example.vivesbankproject.users.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class MovimientoServiceImplTest {

    @MockBean
    private MovimientosRepository movimientosRepository;

    @MockBean
    private ClienteService clienteService;

    @MockBean
    private MovimientoMapper movimientosMapper;

    @InjectMocks
    private MovimientosServiceImpl movimientosService;

    private Cliente cliente;
    private Movimiento movimiento;
    private ObjectId movimientoId;

    @BeforeEach
    void setUp() {
        movimientoId = new ObjectId();

        // Mock Cliente with required attributes
        cliente = Cliente.builder()
                .guid("5f8761020988676500000001")
                .dni("12345678A")
                .nombre("John")
                .apellidos("Doe")
                .email("john.doe@example.com")
                .telefono("123456789")
                .fotoPerfil("perfil.jpg")
                .fotoDni("dni.jpg")
                .user(new User())  // Assuming you have a User object
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Mock Movimiento
        movimiento = Movimiento.builder()
                .id(movimientoId)
                .guid("hola")
                .clienteGuid(cliente.getGuid())
                .domiciliacion(new Domiciliacion())
                .ingresoDeNomina(new IngresoDeNomina())
                .pagoConTarjeta(new PagoConTarjeta())
                .transferencia(new Transferencia())
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void getAll() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<Movimiento> mockPage = new PageImpl<>(List.of(movimiento));

        // Mock the repository method
        when(movimientosRepository.findAll(pageable)).thenReturn(mockPage);

        // Call service method
        Page<MovimientoResponse> result = movimientosService.getAll(pageable);

        assertAll(
                () -> assertEquals(1, result.getTotalElements())
        );

        verify(movimientosRepository, times(1)).findAll(pageable);
    }

    @Test
    void getById() {
        when(movimientosRepository.findById(movimientoId)).thenReturn(Optional.of(movimiento));
        when(movimientosMapper.toMovimientoResponse(movimiento)).thenReturn(new MovimientoResponse());

        MovimientoResponse result = movimientosService.getById(movimientoId);

        assertAll(
                () -> assertEquals(movimientoId.toHexString(), result.getGuid())
        );

        verify(movimientosRepository, times(1)).findById(movimientoId);
    }

    @Test
    void getById_notFound() {
        when(movimientosRepository.findById(movimientoId)).thenReturn(Optional.empty());

        var result = assertThrows(MovimientoNotFound.class, () -> movimientosService.getById(movimientoId));

        assertEquals("El movimiento con id " + movimientoId.toHexString() + " no existe", result.getMessage());
    }

   /* @Test
    void getMovimientosByClienteGuid() {
        when(clienteService.getById(cliente.getGuid())).thenReturn(cliente);
        when(movimientosRepository.findMovimientosByClienteGuid(cliente.getGuid())).thenReturn(Optional.of(movimiento));
        when(movimientosMapper.toMovimientoResponse(movimiento)).thenReturn(new MovimientoResponse());

        var result = movimientosService.getByClienteGuid(cliente.getGuid());

        assertAll(
                () -> assertEquals(movimiento.getGuid(), result.getGuid())
        );

        verify(clienteService, times(1)).getById(cliente.getGuid());
        verify(movimientosRepository, times(1)).findMovimientosByClienteGuid(cliente.getGuid());
    }*/

    @Test
    void getMovimientos_ByClienteNotFound() {
        when(clienteService.getById(cliente.getGuid())).thenThrow(new ClienteNotFound(cliente.getGuid()));

        var result = assertThrows(ClienteNotFound.class, () -> movimientosService.getByClienteGuid(cliente.getGuid()));

        assertEquals("Cliente con id '5f8761020988676500000001' no encontrado", result.getMessage());
    }

    /*@Test
    void create() {
        MovimientoRequest movimientoRequest = MovimientoRequest.builder()
                .guid(movimiento.getGuid())
                .clienteGuid(cliente.getGuid())
                .domiciliacion(movimiento.getDomiciliacion())
                .ingresoDeNomina(movimiento.getIngresoDeNomina())
                .pagoConTarjeta(movimiento.getPagoConTarjeta())
                .transferencia(movimiento.getTransferencia())
                .build();

        when(clienteService.getById(cliente.getGuid())).thenReturn(cliente);
        when(movimientosMapper.toMovimiento(movimientoRequest)).thenReturn(movimiento);
        when(movimientosRepository.save(movimiento)).thenReturn(movimiento);
        when(movimientosMapper.toMovimientoResponse(movimiento)).thenReturn(new MovimientoResponse());

        MovimientoResponse result = movimientosService.save(movimientoRequest);

        assertAll(
                () -> assertEquals(movimiento.getGuid(), result.getGuid())
        );

        verify(clienteService, times(1)).getById(cliente.getGuid());
        verify(movimientosRepository, times(1)).save(movimiento);
    }*/

    @Test
    void create_ClienteNotFound() {
        MovimientoRequest movimientoRequest = MovimientoRequest.builder()
                .guid(movimiento.getGuid())
                .clienteGuid(cliente.getGuid())
                .domiciliacion(movimiento.getDomiciliacion())
                .ingresoDeNomina(movimiento.getIngresoDeNomina())
                .pagoConTarjeta(movimiento.getPagoConTarjeta())
                .transferencia(movimiento.getTransferencia())
                .build();

        when(clienteService.getById(cliente.getGuid())).thenThrow(new ClienteNotFound(cliente.getGuid()));

        var result = assertThrows(ClienteNotFound.class, () -> movimientosService.save(movimientoRequest));

        assertEquals("Cliente con id '5f8761020988676500000001' no encontrado", result.getMessage());
        verify(movimientosRepository, times(0)).save(any(Movimiento.class));
    }
}