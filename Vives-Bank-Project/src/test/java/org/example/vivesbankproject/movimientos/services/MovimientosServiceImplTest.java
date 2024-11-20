package org.example.vivesbankproject.movimientos.services;

import org.bson.types.ObjectId;
import org.example.vivesbankproject.cliente.exceptions.ClienteNotFound;
import org.example.vivesbankproject.cliente.models.Cliente;
import org.example.vivesbankproject.cliente.repositories.ClienteRepository;
import org.example.vivesbankproject.movimientos.exceptions.ClienteHasNoMovements;
import org.example.vivesbankproject.movimientos.exceptions.MovimientoNotFound;
import org.example.vivesbankproject.movimientos.models.Movimientos;
import org.example.vivesbankproject.movimientos.repositories.MovimientosRepository;
import org.example.vivesbankproject.users.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class MovimientosServiceImplTest {

    @MockBean
    private MovimientosRepository movimientosRepository;

    @MockBean
    private ClienteRepository clienteRepository;

    @InjectMocks
    private MovimientosServiceImpl movimientosService;

    private Cliente cliente;

    private Movimientos movimiento;

    private ObjectId movimientoId;

    @BeforeEach
    void setUp() {

        movimientoId = new ObjectId();

       cliente = Cliente.builder()
                .id(UUID.fromString("5f8761020988676500000001"))
                .dni("12345678A")
                .nombre("John")
                .apellidos("Doe")
                .email("john.doe@example.com")
                .telefono("123456789")
                .fotoPerfil("perfil.jpg")
                .fotoDni("dni.jpg")
                .cuentas(new ArrayList<>())
                .user(new User())
                .idMovimientos(movimientoId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
       movimiento = Movimientos.builder()
                .id(movimientoId)
                .idUsuario(UUID.randomUUID())
                .cliente(cliente)
                .transacciones(new ArrayList<>())
                .isDeleted(false)
                .totalItems(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

    }

    @Test
    void getAll() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<Movimientos> mockPage = new PageImpl<>(Arrays.asList(movimiento));
        when(movimientosRepository.findAll(any(Pageable.class))).thenReturn(mockPage);

        Page<Movimientos> result = movimientosService.getAll(pageable);

        assertAll(
                () -> assertEquals(1, result.getTotalElements())
        );

        verify(movimientosRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getById() {
        when(movimientosRepository.findById(any(ObjectId.class))).thenReturn(Optional.of(movimiento));

        Movimientos result = movimientosService.getById(movimientoId);

        assertAll(
                () -> assertEquals(movimientoId, result.getId())
        );

        verify(movimientosRepository, times(1)).findById(any(ObjectId.class));
    }

    @Test
    void getById_notFound() {
        when(movimientosRepository.findById(any(ObjectId.class))).thenReturn(Optional.empty());

        var result = assertThrows(MovimientoNotFound.class, () -> movimientosService.getById(movimientoId));

        assertEquals("El movimiento con id " + movimientoId + " no existe", result.getMessage());

        verify(movimientosRepository, times(0)).findById(any(ObjectId.class));
    }

    @Test
    void getMovimientosByClienteId() {
        when(movimientosRepository.findMovimientosByClienteId(any(UUID.class))).thenReturn(Optional.of(movimiento));
        when(clienteRepository.findById(any(UUID.class))).thenReturn(Optional.of(cliente));

        var result = movimientosService.getByClienteId(cliente.getId());

        assertAll(
                () -> assertEquals(movimientoId, result.getId())
        );

        verify(movimientosRepository, times(1)).findMovimientosByClienteId(any(UUID.class));
        verify(clienteRepository, times(1)).findById(any(UUID.class));
    }
    @Test
    void getMovimientos_ByClienteNotFound() {
        when(movimientosRepository.findMovimientosByClienteId(any(UUID.class))).thenReturn(Optional.empty());
        when(clienteRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        var result = assertThrows(ClienteNotFound.class, () -> movimientosService.getByClienteId(cliente.getId()));

        assertEquals("Cliente con id " + cliente.getId() + " no encontrado", result.getMessage());

        verify(movimientosRepository, times(0)).findById(any(ObjectId.class));
        verify(clienteRepository, times(0)).findById(any(UUID.class));
    }

    @Test
    void getMovimientos_ByClienteHasNoMovimientos() {
        when(movimientosRepository.findMovimientosByClienteId(any(UUID.class))).thenReturn(Optional.empty());
        when(clienteRepository.findById(any(UUID.class))).thenReturn(Optional.of(cliente));

        var result = assertThrows(ClienteHasNoMovements.class, () -> movimientosService.getByClienteId(cliente.getId()));

        assertEquals("El cliente con Id" + cliente.getId() + " no tiene movimientos", result.getMessage());

        verify(movimientosRepository, times(1)).findMovimientosByClienteId(any(UUID.class));
        verify(clienteRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void create() {
        when(movimientosRepository.save(any(Movimientos.class))).thenReturn(movimiento);
        when(clienteRepository.findById(any(UUID.class))).thenReturn(Optional.of(cliente));
        Movimientos result = movimientosService.save(movimiento);

        assertAll(
                () -> assertEquals(movimientoId, result.getId())
        );

        verify(movimientosRepository, times(1)).save(any(Movimientos.class));
        verify(clienteRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void create_ClienteWithNoMovimientos() {
        cliente.setIdMovimientos(null);
        when(movimientosRepository.save(any(Movimientos.class))).thenReturn(movimiento);
        when(clienteRepository.findById(any(UUID.class))).thenReturn(Optional.of(cliente));

        Movimientos result = movimientosService.save(movimiento);

        assertAll(
                () -> assertEquals(movimientoId, result.getId()),
                () -> assertEquals(movimientoId, result.getCliente().getIdMovimientos())
        );

        verify(movimientosRepository, times(1)).save(any(Movimientos.class));
        verify(clienteRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void create_ClienteNotFound() {
        when(movimientosRepository.save(any(Movimientos.class))).thenReturn(movimiento);
        when(clienteRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        var result = assertThrows(ClienteNotFound.class, () -> movimientosService.save(movimiento));

        assertEquals("Cliente con id " + cliente.getId() + " no encontrado", result.getMessage());

        verify(movimientosRepository, times(0)).save(any(Movimientos.class));
        verify(clienteRepository, times(1)).findById(any(UUID.class));
    }


}