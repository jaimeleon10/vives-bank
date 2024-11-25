package org.example.vivesbankproject.cliente.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.example.vivesbankproject.cliente.dto.*;
import org.example.vivesbankproject.cliente.exceptions.ClienteExistsByDni;
import org.example.vivesbankproject.cliente.exceptions.ClienteExistsByEmail;
import org.example.vivesbankproject.cliente.exceptions.ClienteExistsByTelefono;
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

public class ClienteServiceImplTest {

    private ClienteServiceImpl clienteService;

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

    @Mock
    private TipoCuentaMapper tipoCuentaMapper;

    @Mock
    private TarjetaMapper tarjetaMapper;

    @Mock
    private TarjetaRepository tarjetaRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        clienteService = new ClienteServiceImpl(clienteRepository, clienteMapper, userMapper, userRepository, cuentaMapper, cuentaRepository, tipoCuentaMapper, tarjetaMapper, tarjetaRepository);
    }

    @Test
    void GetAll() {

        User user = User.builder().guid("user-guid").username("testuser").password("password").build();
        Cuenta cuenta1 = Cuenta.builder().guid("cuenta1-guid").build();
        Cuenta cuenta2 = Cuenta.builder().guid("cuenta2-guid").build();
        Cliente cliente = Cliente.builder()
                .guid("unique-guid")
                .dni("12345678A")
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .user(user)
                .isDeleted(false)
                .cuentas(Set.of(cuenta1, cuenta2))
                .build();


        Pageable pageable = PageRequest.of(0, 10);
        Page<Cliente> clientePage = new PageImpl<>(List.of(cliente), pageable, 1);

        when(clienteRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(clientePage);
        when(userMapper.toUserResponse(any(User.class))).thenReturn(new UserResponse("user-guid", "testuser"));
        when(cuentaMapper.toCuentaResponse(any(Cuenta.class), any(), any(), any())).thenReturn(new CuentaResponse("cuenta1-guid", "iban", BigDecimal.valueOf(100)));
        when(clienteMapper.toClienteResponse(any(Cliente.class), any(UserResponse.class), any(Set.class)))
                .thenReturn(new ClienteResponse("unique-guid", "12345678A", "Juan", "Perez", "juan.perez@example.com", "123456789", "fotoprfil.jpg", "fotodni.jpg", Set.of(new CuentaResponse("cuenta1-guid", "iban", BigDecimal.valueOf(100))), new UserResponse("user-guid", "testuser"), LocalDateTime.now(), LocalDateTime.now(), false));


        Page<ClienteResponse> result = clienteService.getAll(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), pageable);


        assertEquals(1, result.getTotalElements());
        assertEquals("unique-guid", result.getContent().get(0).getGuid());
        assertEquals("12345678A", result.getContent().get(0).getDni());
        assertEquals("Juan", result.getContent().get(0).getNombre());
        assertEquals("Perez", result.getContent().get(0).getApellidos());
    }


        @Test
        void GetById() {

            User user = User.builder().guid("user-guid").username("testuser").password("password").build();
            Cuenta cuenta1 = Cuenta.builder().guid("cuenta1-guid").build();
            Cuenta cuenta2 = Cuenta.builder().guid("cuenta2-guid").build();
            Cliente cliente = Cliente.builder()
                    .guid("unique-guid")
                    .dni("12345678A")
                    .nombre("Juan")
                    .apellidos("Perez")
                    .email("juan.perez@example.com")
                    .telefono("123456789")
                    .user(user)
                    .isDeleted(false)
                    .cuentas(Set.of(cuenta1, cuenta2))
                    .build();

            when(clienteRepository.findByGuid("unique-guid")).thenReturn(Optional.of(cliente));
            when(userMapper.toUserResponse(user)).thenReturn(new UserResponse("user-guid", "testuser"));
            when(cuentaMapper.toCuentaResponse(any(Cuenta.class), any(), any(), any())).thenReturn(new CuentaResponse("cuenta1-guid", "iban", BigDecimal.valueOf(100)));
            when(clienteMapper.toClienteResponse(any(Cliente.class), any(UserResponse.class), any(Set.class)))
                    .thenReturn(new ClienteResponse("unique-guid", "12345678A", "Juan", "Perez", "juan.perez@example.com", "123456789", "fotoprfil.jpg", "fotodni.jpg", Set.of(new CuentaResponse("cuenta1-guid", "iban", BigDecimal.valueOf(100))), new UserResponse("user-guid", "testuser"), LocalDateTime.now(), LocalDateTime.now(), false));


            ClienteResponse result = clienteService.getById("unique-guid");


            assertEquals("unique-guid", result.getGuid());
            assertEquals("12345678A", result.getDni());
            assertEquals("Juan", result.getNombre());
            assertEquals("Perez", result.getApellidos());
        }

        @Test
        void Save() {

            ClienteRequestSave clienteRequestSave = ClienteRequestSave.builder()
                    .dni("12345678A")
                    .nombre("Juan")
                    .apellidos("Perez")
                    .email("juan.perez@example.com")
                    .telefono("123456789")
                    .fotoPerfil("fotoprfil.jpg")
                    .fotoDni("fotodni.jpg")
                    .cuentasIds(Set.of("cuenta1-guid", "cuenta2-guid"))
                    .userId("user-guid")
                    .build();

            User user = User.builder().guid("user-guid").username("testuser").password("password").build();
            Cuenta cuenta1 = Cuenta.builder().guid("cuenta1-guid").build();
            Cuenta cuenta2 = Cuenta.builder().guid("cuenta2-guid").build();
            Cliente cliente = Cliente.builder()
                    .guid("unique-guid")
                    .dni("12345678A")
                    .nombre("Juan")
                    .apellidos("Perez")
                    .email("juan.perez@example.com")
                    .telefono("123456789")
                    .user(user)
                    .isDeleted(false)
                    .cuentas(Set.of(cuenta1, cuenta2))
                    .build();

            when(clienteRepository.existsByUserGuid("user-guid")).thenReturn(false);
            when(cuentaRepository.findByGuid("cuenta1-guid")).thenReturn(Optional.of(cuenta1));
            when(cuentaRepository.findByGuid("cuenta2-guid")).thenReturn(Optional.of(cuenta2));
            when(clienteRepository.findCuentasAsignadas(any(Set.class))).thenReturn(List.of());
            when(userRepository.findByGuid("user-guid")).thenReturn(Optional.of(user));
            when(clienteMapper.toCliente(any(ClienteRequestSave.class), any(User.class), any(Set.class))).thenReturn(cliente);
            when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);
            when(userMapper.toUserResponse(user)).thenReturn(new UserResponse("user-guid", "testuser"));
            when(cuentaMapper.toCuentaResponse(any(Cuenta.class), any(), any(), any())).thenReturn(new CuentaResponse("cuenta1-guid", "iban", BigDecimal.valueOf(100)));
            when(clienteMapper.toClienteResponse(any(Cliente.class), any(UserResponse.class), any(Set.class)))
                    .thenReturn(new ClienteResponse("unique-guid", "12345678A", "Juan", "Perez", "juan.perez@example.com", "123456789", "fotoprfil.jpg", "fotodni.jpg", Set.of(new CuentaResponse("cuenta1-guid", "iban", BigDecimal.valueOf(100))), new UserResponse("user-guid", "testuser"), LocalDateTime.now(), LocalDateTime.now(), false));


            ClienteResponse result = clienteService.save(clienteRequestSave);


            assertEquals("unique-guid", result.getGuid());
            assertEquals("12345678A", result.getDni());
            assertEquals("Juan", result.getNombre());
            assertEquals("Perez", result.getApellidos());
        }


        @Test
        void Update() {

            ClienteRequestUpdate clienteRequestUpdate = ClienteRequestUpdate.builder()
                    .nombre("Juan")
                    .apellidos("Perez")
                    .email("juan.perez@example.com")
                    .telefono("123456789")
                    .fotoPerfil("fotoprfil.jpg")
                    .fotoDni("fotodni.jpg")
                    .userId("user-guid")
                    .build();

            User user = User.builder().guid("user-guid").username("testuser").password("password").build();
            Cuenta cuenta1 = Cuenta.builder().guid("cuenta1-guid").build();
            Cliente cliente = Cliente.builder()
                    .guid("unique-guid")
                    .dni("12345678A")
                    .nombre("Juan")
                    .apellidos("Perez")
                    .email("juan.perez@example.com")
                    .telefono("123456789")
                    .user(user)
                    .isDeleted(false)
                    .cuentas(Set.of(cuenta1))
                    .build();

            when(clienteRepository.findByGuid("unique-guid")).thenReturn(Optional.of(cliente));
            when(userRepository.findByGuid("user-guid")).thenReturn(Optional.of(user));
            when(clienteRepository.existsByUserGuid("user-guid")).thenReturn(false);
            when(clienteRepository.findByTelefono(anyString())).thenReturn(Optional.empty());
            when(clienteRepository.findByEmail(anyString())).thenReturn(Optional.empty());
            when(clienteMapper.toClienteUpdate(any(ClienteRequestUpdate.class), any(Cliente.class), any(User.class))).thenReturn(cliente);
            when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);
            when(userMapper.toUserResponse(user)).thenReturn(new UserResponse("user-guid", "testuser"));
            when(cuentaMapper.toCuentaResponse(any(Cuenta.class), any(), any(), any())).thenReturn(new CuentaResponse("cuenta1-guid", "iban", BigDecimal.valueOf(100)));
            when(clienteMapper.toClienteResponse(any(Cliente.class), any(UserResponse.class), any(Set.class)))
                    .thenReturn(new ClienteResponse("unique-guid", "12345678A", "Juan", "Perez", "juan.perez@example.com", "123456789", "fotoprfil.jpg", "fotodni.jpg", Set.of(new CuentaResponse("cuenta1-guid", "iban", BigDecimal.valueOf(100))), new UserResponse("user-guid", "testuser"), LocalDateTime.now(), LocalDateTime.now(), false));


            ClienteResponse result = clienteService.update("unique-guid", clienteRequestUpdate);


            assertEquals("unique-guid", result.getGuid());
            assertEquals("12345678A", result.getDni());
            assertEquals("Juan", result.getNombre());
            assertEquals("Perez", result.getApellidos());
        }

        @Test
        void DeleteById() {

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

            when(clienteRepository.findByGuid("unique-guid")).thenReturn(Optional.of(cliente));


            clienteService.deleteById("unique-guid");


            verify(clienteRepository, times(1)).save(cliente);
            assertTrue(cliente.getIsDeleted());
        }

        @Test
        void GetProductos() {

            User user = User.builder().guid("user-guid").username("testuser").password("password").build();
            Cuenta cuenta1 = Cuenta.builder().guid("cuenta1-guid").build();
            Cliente cliente = Cliente.builder()
                    .guid("unique-guid")
                    .dni("12345678A")
                    .nombre("Juan")
                    .apellidos("Perez")
                    .email("juan.perez@example.com")
                    .telefono("123456789")
                    .user(user)
                    .isDeleted(false)
                    .cuentas(Set.of(cuenta1))
                    .build();

            when(clienteRepository.findByGuid("unique-guid")).thenReturn(Optional.of(cliente));
            when(cuentaMapper.toCuentaResponse(any(Cuenta.class), any(), any(), any())).thenReturn(new CuentaResponse("cuenta1-guid", "iban", BigDecimal.valueOf(100)));
            when(clienteMapper.toClienteResponseProductos(any(Cliente.class), any(Set.class)))
                    .thenReturn(new ClienteResponseProductos("unique-guid", "Juan", Set.of(new CuentaResponse("cuenta1-guid", "iban", BigDecimal.valueOf(100)))));


            ClienteResponseProductos result = clienteService.getProductos("unique-guid");


            assertEquals("unique-guid", result.getGuid());
            assertEquals("Juan", result.getNombre());
            assertEquals(1, result.getCuentas().size());
        }

        @Test
        void GetProductosById() {

            User user = User.builder().guid("user-guid").username("testuser").password("password").build();
            Cuenta cuenta1 = Cuenta.builder().guid("cuenta1-guid").build();
            Cliente cliente = Cliente.builder()
                    .guid("unique-guid")
                    .dni("12345678A")
                    .nombre("Juan")
                    .apellidos("Perez")
                    .email("juan.perez@example.com")
                    .telefono("123456789")
                    .user(user)
                    .isDeleted(false)
                    .cuentas(Set.of(cuenta1))
                    .build();

            when(clienteRepository.findByGuid("unique-guid")).thenReturn(Optional.of(cliente));
            when(cuentaRepository.findByGuid("cuenta1-guid")).thenReturn(Optional.of(cuenta1));
            when(tarjetaRepository.findByGuid(anyString())).thenReturn(Optional.empty());
            when(tipoCuentaMapper.toTipoCuentaResponse(any())).thenReturn(new TipoCuentaResponse("tipo-cuenta"));
            when(tarjetaMapper.toTarjetaResponse(any())).thenReturn(new TarjetaResponse("tarjeta"));
            when(clienteMapper.toClienteResponseProductoById(any(Cliente.class), any(CuentaResponse.class), any(TarjetaResponse.class)))
                    .thenReturn(new ClienteResponseProductosById("unique-guid", "Juan", new CuentaResponse("cuenta1-guid", "iban", BigDecimal.valueOf(100)), null));


            ClienteResponseProductosById result = clienteService.getProductosById("unique-guid", "cuenta1-guid");


            assertEquals("unique-guid", result.getGuid());
            assertEquals("Juan", result.getNombre());
            assertNotNull(result.getCuenta());
            assertNull(result.getTarjeta());
        }


    @Test
    void ValidarClienteExistenteDni() {

        Cliente cliente = Cliente.builder()
                .dni("12345678A")
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .build();

        when(clienteRepository.findByDni("12345678A")).thenReturn(Optional.of(cliente));

        // Ejecutar el método y verificar la excepción
        ClienteExistsByDni exception = assertThrows(ClienteExistsByDni.class, () -> {
            clienteService.validarClienteExistente(cliente);
        });

        assertEquals("12345678A", exception.getDni());
    }

    @Test
    void ValidarClienteExistenteTelefono() {

        Cliente cliente = Cliente.builder()
                .dni("12345678A")
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .build();

        when(clienteRepository.findByDni("12345678A")).thenReturn(Optional.empty());
        when(clienteRepository.findByTelefono("123456789")).thenReturn(Optional.of(cliente));


        ClienteExistsByTelefono exception = assertThrows(ClienteExistsByTelefono.class, () -> {
            clienteService.validarClienteExistente(cliente);
        });

        assertEquals("123456789", exception.getTelefono());
    }

    @Test
    void ValidarClienteExistenteEmail() {

        Cliente cliente = Cliente.builder()
                .dni("12345678A")
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .build();

        when(clienteRepository.findByDni("12345678A")).thenReturn(Optional.empty());
        when(clienteRepository.findByTelefono("123456789")).thenReturn(Optional.empty());
        when(clienteRepository.findByEmail("juan.perez@example.com")).thenReturn(Optional.of(cliente));

        // Ejecutar el método y verificar la excepción
        ClienteExistsByEmail exception = assertThrows(ClienteExistsByEmail.class, () -> {
            clienteService.validarClienteExistente(cliente);
        });

        assertEquals("juan.perez@example.com", exception.getEmail());
    }

    @Test
    void ValidarClienteNoExistente() {

        Cliente cliente = Cliente.builder()
                .dni("12345678A")
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .build();

        when(clienteRepository.findByDni("12345678A")).thenReturn(Optional.empty());
        when(clienteRepository.findByTelefono("123456789")).thenReturn(Optional.empty());
        when(clienteRepository.findByEmail("juan.perez@example.com")).thenReturn(Optional.empty());

        
        assertDoesNotThrow(() -> {
            clienteService.validarClienteExistente(cliente);
        });
    }
}
