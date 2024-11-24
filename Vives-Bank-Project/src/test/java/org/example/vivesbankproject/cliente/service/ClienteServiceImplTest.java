package org.example.vivesbankproject.cliente.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.example.vivesbankproject.cliente.dto.ClienteResponse;
import org.example.vivesbankproject.cliente.mappers.ClienteMapper;
import org.example.vivesbankproject.cliente.models.Cliente;
import org.example.vivesbankproject.cliente.repositories.ClienteRepository;
import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaResponse;
import org.example.vivesbankproject.cuenta.dto.tipoCuenta.TipoCuentaResponse;
import org.example.vivesbankproject.cuenta.mappers.CuentaMapper;
import org.example.vivesbankproject.cuenta.models.Cuenta;
import org.example.vivesbankproject.cuenta.repositories.CuentaRepository;
import org.example.vivesbankproject.tarjeta.dto.TarjetaResponse;
import org.example.vivesbankproject.tarjeta.models.TipoTarjeta;
import org.example.vivesbankproject.users.dto.UserResponse;
import org.example.vivesbankproject.users.mappers.UserMapper;
import org.example.vivesbankproject.users.models.Role;
import org.example.vivesbankproject.users.models.User;
import org.example.vivesbankproject.users.repositories.UserRepository;
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
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class ClienteServiceImplTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ClienteMapper clienteMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CuentaMapper cuentaMapper;

    @Mock
    private CuentaRepository cuentaRepository;

    @InjectMocks
    private ClienteServiceImpl clienteService;

    private Cliente cliente;
    private User user;
    private Cuenta cuenta1;
    private Cuenta cuenta2;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .guid(UUID.randomUUID().toString())
                .username("testuser")
                .password("password")
                .roles(Set.of(Role.USER))
                .build();

        cuenta1 = Cuenta.builder().guid(UUID.randomUUID().toString()).build();
        cuenta2 = Cuenta.builder().guid(UUID.randomUUID().toString()).build();

        cliente = Cliente.builder()
                .guid(UUID.randomUUID().toString())
                .dni("12345678A")
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .user(user)
                .cuentas(Set.of(cuenta1, cuenta2))
                .isDeleted(false)
                .build();
    }

    @Test
    void GetAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Cliente> clientePage = new PageImpl<>(List.of(cliente));

        when(clienteRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(clientePage);

        UserResponse userResponse = UserResponse.builder()
                .guid(user.getGuid())
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRoles())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .isDeleted(user.getIsDeleted())
                .build();

        TipoCuentaResponse tipoCuentaResponse = new TipoCuentaResponse();
        tipoCuentaResponse.setNombre("normal");
        tipoCuentaResponse.setInteres(BigDecimal.valueOf(2.0));

        TarjetaResponse tarjetaResponse = TarjetaResponse.builder()
                .guid("GUID")
                .numeroTarjeta("1234567890123456")
                .fechaCaducidad(LocalDate.now().plusYears(10))
                .limiteDiario(new BigDecimal("1000.00"))
                .limiteSemanal(new BigDecimal("5000.00"))
                .limiteMensual(new BigDecimal("20000.00"))
                .tipoTarjeta(TipoTarjeta.DEBITO)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        Set<CuentaResponse> cuentasResponse = Stream.of(cuenta1, cuenta2)
                .map(cuenta -> CuentaResponse.builder().guid(cuenta.getGuid()).build())
                .collect(Collectors.toSet());

        when(userMapper.toUserResponse(user)).thenReturn(userResponse);
        when(cuentaMapper.toCuentaResponse(cuenta1, tipoCuentaResponse, tarjetaResponse)).thenReturn(CuentaResponse.builder().guid(cuenta1.getGuid()).build());
        when(cuentaMapper.toCuentaResponse(cuenta2, tipoCuentaResponse, tarjetaResponse)).thenReturn(CuentaResponse.builder().guid(cuenta2.getGuid()).build());
        when(clienteMapper.toClienteResponse(cliente, userResponse, cuentasResponse)).thenReturn(
                ClienteResponse.builder()
                        .guid(cliente.getGuid())
                        .dni(cliente.getDni())
                        .nombre(cliente.getNombre())
                        .apellidos(cliente.getApellidos())
                        .email(cliente.getEmail())
                        .telefono(cliente.getTelefono())
                        .fotoPerfil(cliente.getFotoPerfil())
                        .fotoDni(cliente.getFotoDni())
                        .cuentas(cuentasResponse)
                        .user(userResponse)
                        .createdAt(cliente.getCreatedAt())
                        .updatedAt(cliente.getUpdatedAt())
                        .isDeleted(cliente.getIsDeleted())
                        .build()
        );

        Page<ClienteResponse> result = clienteService.getAll(
                Optional.of("12345678A"),
                Optional.of("Juan"),
                Optional.of("Perez"),
                Optional.of("juan.perez@example.com"),
                Optional.of("123456789"),
                pageable
        );

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        ClienteResponse clienteResponse = result.getContent().get(0);
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
        assertEquals(cuentasResponse, clienteResponse.getCuentas());
    }



    @Test
    void getById() {
    }

    @Test
    void save() {
    }

    @Test
    void update() {
    }

    @Test
    void addCuentas() {
    }

    @Test
    void removeCuentas() {
    }

    @Test
    void deleteById() {
    }
}