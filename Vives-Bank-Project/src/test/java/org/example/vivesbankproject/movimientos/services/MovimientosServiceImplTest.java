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

    @Autowired
    public MovimientosServiceImplTest(MovimientosRepository movimientosRepository, ClienteRepository clienteRepository) {
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
                .cuentas(Set.of())
                .user(new User())
                .idMovimientos(movimientoId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
       movimiento = Movimientos.builder()
                .id(movimientoId)
                .guid("hola")
                .idUsuario("idusuario")
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
        Page<Movimientos> mockPage = new PageImpl<>(List.of(movimiento));
        when(movimientosRepository.findAll(pageable)).thenReturn(mockPage);

        Page<Movimientos> result = movimientosService.getAll(pageable);

        assertAll(
                () -> assertEquals(1, result.getTotalElements())
        );

        verify(movimientosRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getById() {
        when(movimientosRepository.findByGuid(movimiento.getGuid())).thenReturn(Optional.of(movimiento));

        Movimientos result = movimientosService.getById(movimiento.getGuid());

        assertAll(
                () -> assertEquals(movimientoId, result.getId())
        );

        verify(movimientosRepository, times(1)).findByGuid(movimiento.getGuid());
    }

    @Test
    void getById_notFound() {
        when(movimientosRepository.findById(any(ObjectId.class))).thenReturn(Optional.empty());

        var result = assertThrows(MovimientoNotFound.class, () -> movimientosService.getById(movimiento.getGuid()));

        assertEquals("El movimiento con id hola no existe", result.getMessage());

        verify(movimientosRepository, times(0)).findById(any(ObjectId.class));
    }

    @Test
    void getMovimientosByClienteId() {
        when(clienteRepository.findByGuid(cliente.getGuid())).thenReturn(Optional.of(cliente));
        when(movimientosRepository.findMovimientosByClienteId(cliente.getGuid())).thenReturn(Optional.of(movimiento));

        var result = movimientosService.getByClienteId(cliente.getGuid());

        assertAll(
                () -> assertEquals(movimientoId, result.getId())
        );

        verify(movimientosRepository, times(1)).findMovimientosByClienteId(cliente.getGuid());
        verify(clienteRepository, times(1)).findByGuid(cliente.getGuid());
    }

    @Test
    void getMovimientos_ByClienteNotFound() {
        when(movimientosRepository.findMovimientosByClienteId("cliente.getGuid()")).thenReturn(Optional.empty());
        when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.empty());

        var result = assertThrows(ClienteNotFound.class, () -> movimientosService.getByClienteId("cliente.getGuid()"));

        assertEquals("Cliente con id 'cliente.getGuid()' no encontrado", result.getMessage());

        verify(movimientosRepository, times(0)).findById(any(ObjectId.class));
        verify(clienteRepository, times(0)).findById(cliente.getId());
    }

    @Test
    void getMovimientos_ByClienteHasNoMovimientos() {
        when(clienteRepository.findByGuid(cliente.getGuid())).thenReturn(Optional.of(cliente));
        when(movimientosRepository.findMovimientosByClienteId(cliente.getGuid())).thenReturn(Optional.empty());

        var result = assertThrows(ClienteHasNoMovements.class, () -> movimientosService.getByClienteId(cliente.getGuid()));

        assertEquals("El cliente con Id5f8761020988676500000001 no tiene movimientos", result.getMessage());

        verify(movimientosRepository, times(1)).findMovimientosByClienteId(cliente.getGuid());
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
                .cuentas(Set.of())
                .user(new User())
                .idMovimientos(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Movimientos movimiento = Movimientos.builder()
                .id(new ObjectId())
                .guid("mama")
                .idUsuario("idusuario")
                .cliente(cliente)
                .transacciones(new ArrayList<>())
                .isDeleted(false)
                .totalItems(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.of(cliente));
        when(movimientosRepository.save(movimiento)).thenReturn(movimiento);

        Movimientos result = movimientosService.save(movimiento);

        assertAll(
                () -> assertEquals(movimiento.getId(), result.getId())
        );

        verify(movimientosRepository, times(2)).save(any(Movimientos.class));
        verify(clienteRepository, times(1)).findById(cliente.getId());
    }

    @Test
    void create_ClienteWithNoMovimientos() {
        cliente.setIdMovimientos(null);

        when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.of(cliente));
        when(movimientosRepository.save(any(Movimientos.class))).thenReturn(movimiento);

        Movimientos result = movimientosService.save(movimiento);

        assertAll(
                () -> assertEquals(movimientoId, result.getId()),
                () -> assertEquals(movimiento.getId().toHexString(), result.getCliente().getIdMovimientos())
        );

        verify(movimientosRepository, times(2)).save(any(Movimientos.class));
        verify(clienteRepository, times(1)).findById(cliente.getId());
    }

    @Test
    void create_ClienteNotFound() {
        when(movimientosRepository.save(any(Movimientos.class))).thenReturn(movimiento);
        when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.empty());

        var result = assertThrows(ClienteNotFound.class, () -> movimientosService.save(movimiento));

        assertEquals("Cliente con id '5f8761020988676500000001' no encontrado", result.getMessage());

        verify(movimientosRepository, times(0)).save(any(Movimientos.class));
        verify(clienteRepository, times(1)).findById(cliente.getId());
    }
}