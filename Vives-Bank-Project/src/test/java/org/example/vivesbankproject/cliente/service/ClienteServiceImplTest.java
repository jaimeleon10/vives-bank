package org.example.vivesbankproject.cliente.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.example.vivesbankproject.cliente.dto.*;
import org.example.vivesbankproject.cliente.exceptions.ClienteExistsByDni;
import org.example.vivesbankproject.cliente.mappers.ClienteMapper;
import org.example.vivesbankproject.cliente.models.Cliente;
import org.example.vivesbankproject.cliente.repositories.ClienteRepository;
import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaResponse;
import org.example.vivesbankproject.cuenta.dto.tipoCuenta.TipoCuentaResponse;
import org.example.vivesbankproject.cuenta.mappers.CuentaMapper;
import org.example.vivesbankproject.cuenta.mappers.TipoCuentaMapper;
import org.example.vivesbankproject.cuenta.models.Cuenta;
import org.example.vivesbankproject.cuenta.repositories.CuentaRepository;
import org.example.vivesbankproject.tarjeta.dto.TarjetaResponse;
import org.example.vivesbankproject.tarjeta.mappers.TarjetaMapper;
import org.example.vivesbankproject.tarjeta.repositories.TarjetaRepository;
import org.example.vivesbankproject.users.dto.UserResponse;
import org.example.vivesbankproject.users.mappers.UserMapper;

import org.example.vivesbankproject.users.models.User;
import org.example.vivesbankproject.users.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import javax.management.relation.Role;
public class ClienteServiceImplTest {

    private ClienteServiceImpl clienteService;

    @Mock private ClienteRepository clienteRepository;
    @Mock private ClienteMapper clienteMapper;
    @Mock private UserMapper userMapper;
    @Mock private UserRepository userRepository;
    @Mock private CuentaMapper cuentaMapper;
    @Mock private CuentaRepository cuentaRepository;
    @Mock private TipoCuentaMapper tipoCuentaMapper;
    @Mock private TarjetaMapper tarjetaMapper;
    @Mock private TarjetaRepository tarjetaRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        clienteService = new ClienteServiceImpl(
                clienteRepository, clienteMapper, userMapper, userRepository,
                cuentaMapper, cuentaRepository, tipoCuentaMapper,
                tarjetaMapper, tarjetaRepository);
    }

    @Test
    void GetAll() {
        User user = User.builder().guid("user-guid").username("testuser").password("password").build();
        Set<Role> roles = new HashSet<>();
        Cliente cliente = Cliente.builder()
                .guid("cliente-guid")
                .dni("12345678A")
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .user(user)
                .isDeleted(false)
                .build();
        Cuenta cuenta1 = Cuenta.builder().guid("cuenta1-guid").iban("ES123456789").saldo(BigDecimal.valueOf(100)).build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Cliente> clientePage = new PageImpl<>(List.of(cliente), pageable, 1);

        // Mock responses
        when(clienteRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(clientePage);
        when(userMapper.toUserResponse(any(User.class)))
                .thenReturn(new UserResponse("user-guid", "testuser", "password", roles, LocalDateTime.now(), LocalDateTime.now(), false));
        when(cuentaMapper.toCuentaResponse(any(Cuenta.class), any(), any(), any()))
                .thenReturn(new CuentaResponse(
                        "cuenta1-guid", "ES123456789", BigDecimal.valueOf(100),
                        new TipoCuentaResponse("tipo-guid", "Cuenta Corriente", LocalDateTime.now(), LocalDateTime.now(), false),
                        new TarjetaResponse("tarjeta-guid", "1234-5678-1234-5678", LocalDateTime.now(), LocalDateTime.now(), false),
                        new ClienteForCuentaResponse("cliente-guid", "Juan Perez"),
                        LocalDateTime.now(), LocalDateTime.now(), false));
        when(clienteMapper.toClienteResponse(any(Cliente.class), any(UserResponse.class), any(Set.class)))
                .thenReturn(new ClienteResponse(
                        "cliente-guid", "12345678A", "Juan", "Perez", "juan.perez@example.com", "123456789",
                        "fotoprfil.jpg", "fotodni.jpg",
                        Set.of(new CuentaResponse(
                                "cuenta1-guid", "ES123456789", BigDecimal.valueOf(100),
                                new TipoCuentaResponse("tipo-guid", "Cuenta Corriente", LocalDateTime.now(), LocalDateTime.now(), false),
                                new TarjetaResponse("tarjeta-guid", "1234-5678-1234-5678", LocalDateTime.now(), LocalDateTime.now(), false),
                                new ClienteForCuentaResponse("cliente-guid", "Juan Perez"),
                                LocalDateTime.now(), LocalDateTime.now(), false)),
                        new UserResponse("user-guid", "testuser", "password", roles, LocalDateTime.now(), LocalDateTime.now(), false),
                        LocalDateTime.now(), LocalDateTime.now(), false));

        // Ejecutar el método del servicio
        Page<ClienteResponse> result = clienteService.getAll(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), pageable);

        // Verificar resultados
        assertEquals(1, result.getTotalElements());
        assertEquals("cliente-guid", result.getContent().get(0).getGuid());
        assertEquals("Juan", result.getContent().get(0).getNombre());
        assertEquals("Perez", result.getContent().get(0).getApellidos());
        CuentaResponse cuentaResponse = result.getContent().get(0).getCuentas().iterator().next();
        assertEquals("cuenta1-guid", cuentaResponse.getGuid());
        assertEquals("ES123456789", cuentaResponse.getIban());
        assertEquals(0, BigDecimal.valueOf(100).compareTo(cuentaResponse.getSaldo()));
        assertEquals("Cuenta Corriente", cuentaResponse.getTipoCuenta().getNombre());
        assertEquals("1234-5678-1234-5678", cuentaResponse.getTarjeta());
        assertEquals("Juan Perez", cuentaResponse.getCliente());
    }

    @Test
    void GetById() {
        User user = User.builder().guid("user-guid").username("testuser").password("password").build();
        Set<Role> roles = new HashSet<>();
        Cliente cliente = Cliente.builder()
                .guid("cliente-guid")
                .dni("12345678A")
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .user(user)
                .isDeleted(false)
                .build();
        Cuenta cuenta1 = Cuenta.builder().guid("cuenta1-guid").iban("ES123456789").saldo(BigDecimal.valueOf(100)).build();

        when(clienteRepository.findByGuid("cliente-guid")).thenReturn(Optional.of(cliente));
        when(userMapper.toUserResponse(user))
                .thenReturn(new UserResponse("user-guid", "testuser", "password", roles, LocalDateTime.now(), LocalDateTime.now(), false));
        when(cuentaMapper.toCuentaResponse(any(Cuenta.class), any(), any(), any()))
                .thenReturn(new CuentaResponse(
                        "cuenta1-guid", "ES123456789", BigDecimal.valueOf(100),
                        new TipoCuentaResponse("tipo-guid", "Cuenta Corriente", LocalDateTime.now(), LocalDateTime.now(), false),
                        new TarjetaResponse("tarjeta-guid", "1234-5678-1234-5678", LocalDateTime.now(), LocalDateTime.now(), false),
                        new ClienteForCuentaResponse("cliente-guid", "Juan Perez"),
                        LocalDateTime.now(), LocalDateTime.now(), false));
        when(clienteMapper.toClienteResponse(any(Cliente.class), any(UserResponse.class), any(Set.class)))
                .thenReturn(new ClienteResponse(
                        "cliente-guid", "12345678A", "Juan", "Perez", "juan.perez@example.com", "123456789",
                        "fotoprfil.jpg", "fotodni.jpg",
                        Set.of(new CuentaResponse(
                                "cuenta1-guid", "ES123456789", BigDecimal.valueOf(100),
                                new TipoCuentaResponse("tipo-guid", "Cuenta Corriente", LocalDateTime.now(), LocalDateTime.now(), false),
                                new TarjetaResponse("tarjeta-guid", "1234-5678-1234-5678", LocalDateTime.now(), LocalDateTime.now(), false),
                                new ClienteForCuentaResponse("cliente-guid", "Juan Perez"),
                                LocalDateTime.now(), LocalDateTime.now(), false)),
                        new UserResponse("user-guid", "testuser", "password", roles, LocalDateTime.now(), LocalDateTime.now(), false),
                        LocalDateTime.now(), LocalDateTime.now(), false));

        // Ejecutar el método del servicio
        ClienteResponse result = clienteService.getById("cliente-guid");

        // Verificar resultados
        assertEquals("cliente-guid", result.getGuid());
        assertEquals("Juan", result.getNombre());
        CuentaResponse cuentaResponse = result.getCuentas().iterator().next();
        assertEquals("cuenta1-guid", cuentaResponse.getGuid());
        assertEquals("ES123456789", cuentaResponse.getIban());
        assertEquals(0, BigDecimal.valueOf(100).compareTo(cuentaResponse.getSaldo()));
        assertEquals("Cuenta Corriente", cuentaResponse.getTipoCuenta().getNombre());
    }

    @Test
    void testSave() {
        // Arrange
        ClienteRequestSave clienteRequestSave = ClienteRequestSave.builder()
                .dni("12345678A")
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .fotoPerfil("fotoprfil.jpg")
                .fotoDni("fotodni.jpg")
                .userId("user-guid")
                .build();

        User user = User.builder().guid("user-guid").username("testuser").password("password").build();
        Cliente cliente = Cliente.builder()
                .guid("unique-guid")
                .dni("12345678A")
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .user(user)
                .isDeleted(false)
                .build();

        when(clienteRepository.existsByUserGuid("user-guid")).thenReturn(false);
        when(userRepository.findByGuid("user-guid")).thenReturn(Optional.of(user));
        when(clienteMapper.toCliente(any(ClienteRequestSave.class), any(User.class), any(Set.class)))
                .thenReturn(cliente);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        // Act
        ClienteResponse result = clienteService.save(clienteRequestSave);

        // Assert
        assertEquals("unique-guid", result.getGuid());
        assertEquals("Juan", result.getNombre());
    }

    @Test
    void testDeleteById() {
        // Arrange
        Cliente cliente = Cliente.builder()
                .guid("unique-guid")
                .isDeleted(false)
                .build();

        when(clienteRepository.findByGuid("unique-guid")).thenReturn(Optional.of(cliente));

        // Act
        clienteService.deleteById("unique-guid");

        // Assert
        verify(clienteRepository, times(1)).save(cliente);
        assertTrue(cliente.getIsDeleted());
    }

    @Test
    void testValidarClienteExistente_Dni() {
        // Arrange
        Cliente cliente = Cliente.builder().dni("12345678A").build();
        when(clienteRepository.findByDni("12345678A")).thenReturn(Optional.of(cliente));

        // Act & Assert
        ClienteExistsByDni exception = assertThrows(ClienteExistsByDni.class, () -> {
            clienteService.validarClienteExistente(cliente);
        });
        assertEquals("12345678A", exception.getDni());
    }
}
