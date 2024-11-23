package org.example.vivesbankproject.cliente.mappers;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.example.vivesbankproject.cliente.dto.ClienteRequestSave;
import org.example.vivesbankproject.cliente.dto.ClienteRequestUpdate;
import org.example.vivesbankproject.cliente.dto.ClienteResponse;
import org.example.vivesbankproject.cliente.models.Cliente;
import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaResponse;
import org.example.vivesbankproject.cuenta.models.Cuenta;
import org.example.vivesbankproject.users.dto.UserResponse;
import org.example.vivesbankproject.users.models.Role;
import org.example.vivesbankproject.users.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ClienteMapperTest {

    private ClienteMapper clienteMapper;

    @BeforeEach
    void setUp() {
        clienteMapper = new ClienteMapper();
    }

    @Test
    void testToClienteResponse() {
        Cliente cliente = Cliente.builder()
                .guid(UUID.randomUUID().toString())
                .dni("12345678A")
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .fotoPerfil("fotoPerfil.jpg")
                .fotoDni("fotoDni.jpg")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        UserResponse userResponse = UserResponse.builder()
                .guid("unique-guid")
                .username("testuser")
                .password("password")
                .roles(new HashSet<>(Set.of(Role.USER)))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        Set<CuentaResponse> cuentas = new HashSet<>();

        ClienteResponse clienteResponse = clienteMapper.toClienteResponse(cliente, userResponse, cuentas);

        assertNotNull(clienteResponse);
        assertEquals(cliente.getGuid(), clienteResponse.getGuid());
        assertEquals(cliente.getDni(), clienteResponse.getDni());
        assertEquals(cliente.getNombre(), clienteResponse.getNombre());
        assertEquals(cliente.getApellidos(), clienteResponse.getApellidos());
        assertEquals(cliente.getEmail(), clienteResponse.getEmail());
        assertEquals(cliente.getTelefono(), clienteResponse.getTelefono());
        assertEquals(cliente.getFotoPerfil(), clienteResponse.getFotoPerfil());
        assertEquals(cliente.getFotoDni(), clienteResponse.getFotoDni());
        assertEquals(cliente.getCreatedAt(), clienteResponse.getCreatedAt());
        assertEquals(cliente.getUpdatedAt(), clienteResponse.getUpdatedAt());
        assertEquals(cliente.getIsDeleted(), clienteResponse.getIsDeleted());
        assertEquals(userResponse, clienteResponse.getUser());
        assertEquals(cuentas, clienteResponse.getCuentas());
    }

    @Test
    void testToCliente() {
        ClienteRequestSave clienteRequestSave = ClienteRequestSave.builder()
                .dni("12345678A")
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .fotoPerfil("fotoPerfil.jpg")
                .fotoDni("fotoDni.jpg")
                .isDeleted(false)
                .build();

        User user = User.builder()
                .id(1L)
                .guid("unique-guid")
                .username("testuser")
                .password("password")
                .roles(new HashSet<>(Set.of(Role.USER)))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        Set<Cuenta> cuentas = new HashSet<>();

        Cliente cliente = clienteMapper.toCliente(clienteRequestSave, user, cuentas);

        assertNotNull(cliente);
        assertEquals(clienteRequestSave.getDni(), cliente.getDni());
        assertEquals(clienteRequestSave.getNombre(), cliente.getNombre());
        assertEquals(clienteRequestSave.getApellidos(), cliente.getApellidos());
        assertEquals(clienteRequestSave.getEmail(), cliente.getEmail());
        assertEquals(clienteRequestSave.getTelefono(), cliente.getTelefono());
        assertEquals(clienteRequestSave.getFotoPerfil(), cliente.getFotoPerfil());
        assertEquals(clienteRequestSave.getFotoDni(), cliente.getFotoDni());
        assertEquals(clienteRequestSave.getIsDeleted(), cliente.getIsDeleted());
        assertEquals(user, cliente.getUser());
        assertEquals(cuentas, cliente.getCuentas());
    }

    @Test
    void testToClienteUpdate() {
        Cliente cliente = Cliente.builder()
                .id(1L)
                .guid(UUID.randomUUID().toString())
                .dni("12345678A")
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .fotoPerfil("fotoPerfil.jpg")
                .fotoDni("fotoDni.jpg")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        ClienteRequestUpdate clienteRequestUpdate = ClienteRequestUpdate.builder()
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .fotoPerfil("fotoPerfil.jpg")
                .fotoDni("fotoDni.jpg")
                .build();

        User user = User.builder()
                .id(1L)
                .guid("unique-guid")
                .username("testuser")
                .password("password")
                .roles(new HashSet<>(Set.of(Role.USER)))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        Cliente updatedCliente = clienteMapper.toClienteUpdate(clienteRequestUpdate, cliente, user);

        assertNotNull(updatedCliente);
        assertEquals(cliente.getId(), updatedCliente.getId());
        assertEquals(cliente.getGuid(), updatedCliente.getGuid());
        assertEquals(cliente.getDni(), updatedCliente.getDni());
        assertEquals(clienteRequestUpdate.getNombre(), updatedCliente.getNombre());
        assertEquals(clienteRequestUpdate.getApellidos(), updatedCliente.getApellidos());
        assertEquals(clienteRequestUpdate.getEmail(), updatedCliente.getEmail());
        assertEquals(clienteRequestUpdate.getTelefono(), updatedCliente.getTelefono());
        assertEquals(clienteRequestUpdate.getFotoPerfil(), updatedCliente.getFotoPerfil());
        assertEquals(clienteRequestUpdate.getFotoDni(), updatedCliente.getFotoDni());
        assertEquals(cliente.getCreatedAt(), updatedCliente.getCreatedAt());
        assertEquals(cliente.getIsDeleted(), updatedCliente.getIsDeleted());
        assertEquals(cliente.getCuentas(), updatedCliente.getCuentas());
        assertEquals(user, updatedCliente.getUser());
    }
}
