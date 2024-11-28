package org.example.vivesbankproject.movimientos.services;

import org.bson.types.ObjectId;
import org.example.vivesbankproject.cliente.exceptions.ClienteNotFound;
import org.example.vivesbankproject.cliente.models.Cliente;
import org.example.vivesbankproject.cliente.repositories.ClienteRepository;
import org.example.vivesbankproject.movimientos.dto.MovimientoRequest;
import org.example.vivesbankproject.movimientos.dto.MovimientoResponse;
import org.example.vivesbankproject.movimientos.exceptions.movimientos.ClienteHasNoMovements;
import org.example.vivesbankproject.movimientos.exceptions.movimientos.MovimientoNotFound;
import org.example.vivesbankproject.movimientos.models.*;
import org.example.vivesbankproject.movimientos.repositories.MovimientosRepository;
import org.example.vivesbankproject.users.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
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
    private ClienteRepository clienteRepository;

    @InjectMocks
    private MovimientosServiceImpl movimientosService;

    private Cliente cliente;

    private Movimiento movimiento;

    private ObjectId movimientoId;

    @Autowired
    public MovimientoServiceImplTest(MovimientosRepository movimientosRepository, ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
        this.movimientosRepository = movimientosRepository;
    }

    @BeforeEach
    void setUp() {

        movimientoId = new ObjectId();

       cliente = Cliente.builder()
                .guid("5f8761020988676500000001")
                .dni("12345678A")
                .nombre("John")
                .apellidos("Doe")
                .email("john.doe@example.com")
                .telefono("123456789")
                .fotoPerfil("perfil.jpg")
                .fotoDni("dni.jpg")
                .user(new User())
                .idMovimientos(movimientoId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
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
        when(movimientosRepository.findAll(pageable)).thenReturn(mockPage);

        Page<MovimientoResponse> result = movimientosService.getAll(pageable);

        assertAll(
                () -> assertEquals(1, result.getTotalElements())
        );

        verify(movimientosRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getById() {
        when(movimientosRepository.findByGuid(movimiento.getGuid())).thenReturn(Optional.of(movimiento));

        MovimientoResponse result = movimientosService.getById(movimiento.getId());

        assertAll(
                () -> assertEquals(movimientoId, result.getGuid())
        );

        verify(movimientosRepository, times(1)).findByGuid(movimiento.getGuid());
    }

    @Test
    void getById_notFound() {
        when(movimientosRepository.findById(any(ObjectId.class))).thenReturn(Optional.empty());

        var result = assertThrows(MovimientoNotFound.class, () -> movimientosService.getById(movimiento.getId()));

        assertEquals("El movimiento con id hola no existe", result.getMessage());

        verify(movimientosRepository, times(0)).findById(any(ObjectId.class));
    }

    @Test
    void getMovimientosByClienteId() {
        when(clienteRepository.findByGuid(cliente.getGuid())).thenReturn(Optional.of(cliente));
        when(movimientosRepository.findMovimientosByClienteGuid(cliente.getGuid())).thenReturn(Optional.of(movimiento));

        var result = movimientosService.getByClienteGuid(cliente.getGuid());

        assertAll(
                () -> assertEquals(movimientoId, result.getGuid())
        );

        verify(movimientosRepository, times(1)).findMovimientosByClienteGuid(cliente.getGuid());
        verify(clienteRepository, times(1)).findByGuid(cliente.getGuid());
    }

    @Test
    void getMovimientos_ByClienteNotFound() {
        when(movimientosRepository.findMovimientosByClienteGuid("cliente.getGuid()")).thenReturn(Optional.empty());
        when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.empty());

        var result = assertThrows(ClienteNotFound.class, () -> movimientosService.getByClienteGuid("cliente.getGuid()"));

        assertEquals("Cliente con id 'cliente.getGuid()' no encontrado", result.getMessage());

        verify(movimientosRepository, times(0)).findById(any(ObjectId.class));
        verify(clienteRepository, times(0)).findById(cliente.getId());
    }

    @Test
    void getMovimientos_ByClienteHasNoMovimientos() {
        when(clienteRepository.findByGuid(cliente.getGuid())).thenReturn(Optional.of(cliente));
        when(movimientosRepository.findMovimientosByClienteGuid(cliente.getGuid())).thenReturn(Optional.empty());

        var result = assertThrows(ClienteHasNoMovements.class, () -> movimientosService.getByClienteGuid(cliente.getGuid()));

        assertEquals("El cliente con Id5f8761020988676500000001 no tiene movimientos", result.getMessage());

        verify(movimientosRepository, times(1)).findMovimientosByClienteGuid(cliente.getGuid());
        verify(clienteRepository, times(1)).findByGuid(cliente.getGuid());
    }

    @Test
    void create() {
        Cliente cliente = Cliente.builder()
                .guid("5f8761020988676500000001")
                .dni("12345678A")
                .nombre("John")
                .apellidos("Doe")
                .email("john.doe@example.com")
                .telefono("123456789")
                .fotoPerfil("perfil.jpg")
                .fotoDni("dni.jpg")
                .user(new User())
                .idMovimientos(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Movimiento movimiento = Movimiento.builder()
                .id(movimientoId)
                .guid("mama")
                .clienteGuid(cliente.getGuid())
                .domiciliacion(new Domiciliacion())
                .ingresoDeNomina(new IngresoDeNomina())
                .pagoConTarjeta(new PagoConTarjeta())
                .transferencia(new Transferencia())
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .build();

        MovimientoRequest movimientoRequest = MovimientoRequest.builder()
                .guid(movimiento.getGuid())
                .clienteGuid(cliente.getGuid())
                .domiciliacion(movimiento.getDomiciliacion())
                .ingresoDeNomina(movimiento.getIngresoDeNomina())
                .pagoConTarjeta(movimiento.getPagoConTarjeta())
                .transferencia(movimiento.getTransferencia())
                .build();

        when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.of(cliente));
        when(movimientosRepository.save(movimiento)).thenReturn(movimiento);

        MovimientoResponse result = movimientosService.save(movimientoRequest);

        assertAll(
                () -> assertEquals(movimiento.getId(), result.getGuid())
        );

        verify(movimientosRepository, times(2)).save(any(Movimiento.class));
        verify(clienteRepository, times(1)).findById(cliente.getId());
    }

    @Test
    void create_ClienteWithNoMovimientos() {
        cliente.setIdMovimientos(null);

        MovimientoRequest movimientoRequest = MovimientoRequest.builder()
                .guid(movimiento.getGuid())
                .clienteGuid(cliente.getGuid())
                .domiciliacion(movimiento.getDomiciliacion())
                .ingresoDeNomina(movimiento.getIngresoDeNomina())
                .pagoConTarjeta(movimiento.getPagoConTarjeta())
                .transferencia(movimiento.getTransferencia())
                .build();

        when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.of(cliente));
        when(movimientosRepository.save(any(Movimiento.class))).thenReturn(movimiento);

        MovimientoResponse result = movimientosService.save(movimientoRequest);

        assertAll(
                () -> assertEquals(movimientoId, result.getGuid()),
                () -> assertEquals(movimiento.getId().toHexString(), result.getClienteGuid())
        );

        verify(movimientosRepository, times(2)).save(any(Movimiento.class));
        verify(clienteRepository, times(1)).findById(cliente.getId());
    }

    @Test
    void create_ClienteNotFound() {
        MovimientoRequest movimientoRequest = MovimientoRequest.builder()
                .guid(movimiento.getGuid())
                .clienteGuid("cliente.getGuid()")
                .domiciliacion(movimiento.getDomiciliacion())
                .ingresoDeNomina(movimiento.getIngresoDeNomina())
                .pagoConTarjeta(movimiento.getPagoConTarjeta())
                .transferencia(movimiento.getTransferencia())
                .build();

        when(movimientosRepository.save(any(Movimiento.class))).thenReturn(movimiento);
        when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.empty());

        var result = assertThrows(ClienteNotFound.class, () -> movimientosService.save(movimientoRequest));

        assertEquals("Cliente con id '5f8761020988676500000001' no encontrado", result.getMessage());

        verify(movimientosRepository, times(0)).save(any(Movimiento.class));
        verify(clienteRepository, times(1)).findById(cliente.getId());
    }
}