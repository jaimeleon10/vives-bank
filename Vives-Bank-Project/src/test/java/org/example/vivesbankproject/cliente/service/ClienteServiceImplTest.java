package org.example.vivesbankproject.cliente.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
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
import org.example.vivesbankproject.cuenta.models.TipoCuenta;
import org.example.vivesbankproject.cuenta.repositories.CuentaRepository;
import org.example.vivesbankproject.tarjeta.dto.TarjetaResponse;
import org.example.vivesbankproject.tarjeta.mappers.TarjetaMapper;
import org.example.vivesbankproject.tarjeta.models.Tarjeta;
import org.example.vivesbankproject.tarjeta.models.TipoTarjeta;
import org.example.vivesbankproject.tarjeta.repositories.TarjetaRepository;
import org.example.vivesbankproject.users.dto.UserResponse;
import org.example.vivesbankproject.users.mappers.UserMapper;

import org.example.vivesbankproject.users.models.Role;
import org.example.vivesbankproject.users.models.User;
import org.example.vivesbankproject.users.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;


@ExtendWith(MockitoExtension.class)
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


        when(clienteRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(clientePage);
        when(userMapper.toUserResponse(any(User.class)))
                .thenReturn(new UserResponse("user-guid", "testuser", "password", roles, LocalDateTime.now(), LocalDateTime.now(), false));
        when(cuentaMapper.toCuentaResponse(any(Cuenta.class), any(), any(), any()))
                .thenReturn(new CuentaResponse(
                        "cuenta1-guid", "ES123456789", BigDecimal.valueOf(100),
                        new TipoCuentaResponse(
                                "tipo-guid", "Cuenta Corriente", BigDecimal.valueOf(1.5),
                                LocalDateTime.now(), LocalDateTime.now(), false),
                        new TarjetaResponse(
                                "tarjeta-guid", "1234-5678-1234-5678", LocalDate.now().plusYears(3),
                                BigDecimal.valueOf(500), BigDecimal.valueOf(2000), BigDecimal.valueOf(5000),
                                TipoTarjeta.CREDITO, LocalDateTime.now(), LocalDateTime.now(), false),
                        new ClienteForCuentaResponse(
                                "cliente-guid", "12345678A", "Juan", "Perez", "juan.perez@example.com",
                                "123456789", "perfil.jpg", "dni.jpg"),
                        LocalDateTime.now(), LocalDateTime.now(), false));
        when(clienteMapper.toClienteResponse(any(Cliente.class), any(UserResponse.class), any(Set.class)))
                .thenReturn(new ClienteResponse(
                        "cliente-guid", "12345678A", "Juan", "Perez", "juan.perez@example.com", "123456789",
                        "fotoprfil.jpg", "fotodni.jpg",
                        Set.of(new CuentaResponse(
                                "cuenta1-guid", "ES123456789", BigDecimal.valueOf(100),
                                new TipoCuentaResponse(
                                        "tipo-guid", "Cuenta Corriente", BigDecimal.valueOf(1.5),
                                        LocalDateTime.now(), LocalDateTime.now(), false),
                                new TarjetaResponse(
                                        "tarjeta-guid", "1234-5678-1234-5678", LocalDate.now().plusYears(3),
                                        BigDecimal.valueOf(500), BigDecimal.valueOf(2000), BigDecimal.valueOf(5000),
                                        TipoTarjeta.CREDITO, LocalDateTime.now(), LocalDateTime.now(), false),
                                new ClienteForCuentaResponse(
                                        "cliente-guid", "12345678A", "Juan", "Perez", "juan.perez@example.com",
                                        "123456789", "perfil.jpg", "dni.jpg"),
                                LocalDateTime.now(), LocalDateTime.now(), false)),
                        new UserResponse("user-guid", "testuser", "password", roles, LocalDateTime.now(), LocalDateTime.now(), false),
                        LocalDateTime.now(), LocalDateTime.now(), false));


        Page<ClienteResponse> result = clienteService.getAll(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), pageable);


        assertEquals(1, result.getTotalElements());
        assertEquals("cliente-guid", result.getContent().get(0).getGuid());
        assertEquals("Juan", result.getContent().get(0).getNombre());
        assertEquals("Perez", result.getContent().get(0).getApellidos());
        CuentaResponse cuentaResponse = result.getContent().get(0).getCuentas().iterator().next();
        assertEquals("cuenta1-guid", cuentaResponse.getGuid());
        assertEquals("ES123456789", cuentaResponse.getIban());
        assertEquals(0, BigDecimal.valueOf(100).compareTo(cuentaResponse.getSaldo()));
        ClienteForCuentaResponse clienteForCuentaResponse = cuentaResponse.getCliente();
        assertEquals("cliente-guid", clienteForCuentaResponse.getGuid());
        assertEquals("12345678A", clienteForCuentaResponse.getDni());
        assertEquals("Juan", clienteForCuentaResponse.getNombre());
        assertEquals("Perez", clienteForCuentaResponse.getApellidos());
        assertEquals("juan.perez@example.com", clienteForCuentaResponse.getEmail());
        assertEquals("123456789", clienteForCuentaResponse.getTelefono());
        assertEquals("perfil.jpg", clienteForCuentaResponse.getFotoPerfil());
        assertEquals("dni.jpg", clienteForCuentaResponse.getFotoDni());
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

        Cuenta cuenta1 = Cuenta.builder()
                .guid("cuenta1-guid")
                .iban("ES123456789")
                .saldo(BigDecimal.valueOf(100))
                .build();

        when(clienteRepository.findByGuid("cliente-guid")).thenReturn(Optional.of(cliente));
        when(userMapper.toUserResponse(user))
                .thenReturn(new UserResponse(
                        "user-guid", "testuser", "password", roles, LocalDateTime.now(), LocalDateTime.now(), false));
        when(cuentaMapper.toCuentaResponse(any(Cuenta.class), any(), any(), any()))
                .thenReturn(new CuentaResponse(
                        "cuenta1-guid", "ES123456789", BigDecimal.valueOf(100),
                        new TipoCuentaResponse(
                                "tipo-guid", "Cuenta Corriente", BigDecimal.valueOf(1.5),
                                LocalDateTime.now(), LocalDateTime.now(), false),
                        new TarjetaResponse(
                                "tarjeta-guid", "1234-5678-1234-5678", LocalDate.now().plusYears(3),
                                BigDecimal.valueOf(500), BigDecimal.valueOf(2000), BigDecimal.valueOf(5000),
                                TipoTarjeta.CREDITO, LocalDateTime.now(), LocalDateTime.now(), false),
                        new ClienteForCuentaResponse(
                                "cliente-guid", "12345678A", "Juan", "Perez", "juan.perez@example.com",
                                "123456789", "perfil.jpg", "dni.jpg"),
                        LocalDateTime.now(), LocalDateTime.now(), false));
        when(clienteMapper.toClienteResponse(any(Cliente.class), any(UserResponse.class), any(Set.class)))
                .thenReturn(new ClienteResponse(
                        "cliente-guid", "12345678A", "Juan", "Perez", "juan.perez@example.com", "123456789",
                        "perfil.jpg", "dni.jpg",
                        Set.of(new CuentaResponse(
                                "cuenta1-guid", "ES123456789", BigDecimal.valueOf(100),
                                new TipoCuentaResponse(
                                        "tipo-guid", "Cuenta Corriente", BigDecimal.valueOf(1.5),
                                        LocalDateTime.now(), LocalDateTime.now(), false),
                                new TarjetaResponse(
                                        "tarjeta-guid", "1234-5678-1234-5678", LocalDate.now().plusYears(3),
                                        BigDecimal.valueOf(500), BigDecimal.valueOf(2000), BigDecimal.valueOf(5000),
                                        TipoTarjeta.CREDITO, LocalDateTime.now(), LocalDateTime.now(), false),
                                new ClienteForCuentaResponse(
                                        "cliente-guid", "12345678A", "Juan", "Perez", "juan.perez@example.com",
                                        "123456789", "perfil.jpg", "dni.jpg"),
                                LocalDateTime.now(), LocalDateTime.now(), false)),
                        new UserResponse("user-guid", "testuser", "password", roles, LocalDateTime.now(), LocalDateTime.now(), false),
                        LocalDateTime.now(), LocalDateTime.now(), false));


        ClienteResponse result = clienteService.getById("cliente-guid");


        assertEquals("cliente-guid", result.getGuid());
        assertEquals("12345678A", result.getDni());
        assertEquals("Juan", result.getNombre());
        assertEquals("Perez", result.getApellidos());
        assertEquals("juan.perez@example.com", result.getEmail());
        assertEquals("123456789", result.getTelefono());
        assertEquals("perfil.jpg", result.getFotoPerfil());
        assertEquals("dni.jpg", result.getFotoDni());


        CuentaResponse cuentaResponse = result.getCuentas().iterator().next();
        assertEquals("cuenta1-guid", cuentaResponse.getGuid());
        assertEquals("ES123456789", cuentaResponse.getIban());
        assertEquals(0, BigDecimal.valueOf(100).compareTo(cuentaResponse.getSaldo()));
        assertEquals("Cuenta Corriente", cuentaResponse.getTipoCuenta().getNombre());
        assertEquals(0, BigDecimal.valueOf(1.5).compareTo(cuentaResponse.getTipoCuenta().getInteres()));


        ClienteForCuentaResponse clienteForCuentaResponse = cuentaResponse.getCliente();
        assertEquals("cliente-guid", clienteForCuentaResponse.getGuid());
        assertEquals("12345678A", clienteForCuentaResponse.getDni());
        assertEquals("Juan", clienteForCuentaResponse.getNombre());
        assertEquals("Perez", clienteForCuentaResponse.getApellidos());
        assertEquals("juan.perez@example.com", clienteForCuentaResponse.getEmail());
        assertEquals("123456789", clienteForCuentaResponse.getTelefono());
        assertEquals("perfil.jpg", clienteForCuentaResponse.getFotoPerfil());
        assertEquals("dni.jpg", clienteForCuentaResponse.getFotoDni());
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


        when(clienteMapper.toClienteResponse(any(Cliente.class), any(UserResponse.class), any(Set.class)))
                .thenReturn(new ClienteResponse(
                        "unique-guid", "12345678A", "Juan", "Perez", "juan.perez@example.com", "123456789",
                        "fotoprfil.jpg", "fotodni.jpg",
                        Set.of(),
                        new UserResponse("user-guid", "testuser", "password", Set.of(), LocalDateTime.now(), LocalDateTime.now(), false),
                        LocalDateTime.now(), LocalDateTime.now(), false
                ));


        ClienteResponse result = clienteService.save(clienteRequestSave);


        assertEquals("unique-guid", result.getGuid());
        assertEquals("Juan", result.getNombre());
    }

    @Test
    void update() {
        // Datos de entrada
        String clienteId = "cliente-guid";
        ClienteRequestUpdate clienteRequestUpdate = ClienteRequestUpdate.builder()
                .userId("user-guid")
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perez@update.com")
                .telefono("987654321")
                .build();

        // Cliente existente en la base de datos
        Cliente clienteExistente = Cliente.builder()
                .guid(clienteId)
                .dni("12345678A")
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .user(User.builder().guid("user-guid").build())
                .build();

        // Usuario asociado al cliente
        User usuarioExistente = User.builder().guid("user-guid").username("testuser").password("password").build();

        // Mocks
        when(clienteRepository.findByGuid(clienteId)).thenReturn(Optional.of(clienteExistente));
        when(userRepository.findByGuid(clienteRequestUpdate.getUserId())).thenReturn(Optional.of(usuarioExistente));
        when(clienteRepository.existsByUserGuid(clienteRequestUpdate.getUserId())).thenReturn(false);  // No existe otro cliente con ese usuario
        when(clienteRepository.findByTelefono(clienteRequestUpdate.getTelefono())).thenReturn(Optional.empty());  // El teléfono no existe
        when(clienteRepository.findByEmail(clienteRequestUpdate.getEmail())).thenReturn(Optional.empty());  // El email no existe
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteExistente);  // El cliente guardado es el mismo por simplicidad


        Set<CuentaResponse> cuentasResponse = Set.of(new CuentaResponse("cuenta-guid", "ES123456789", BigDecimal.valueOf(100), new TipoCuentaResponse("tipo-guid", "Cuenta Corriente", LocalDateTime.now(), LocalDateTime.now(), false), new TarjetaResponse("tarjeta-guid", "1234-5678-1234-5678", LocalDateTime.now(), LocalDateTime.now(), false), new ClienteDataResponse(clienteId, "Juan", "Perez"), LocalDateTime.now(), LocalDateTime.now(), false));

        when(clienteMapper.toClienteUpdate(clienteRequestUpdate, clienteExistente, usuarioExistente)).thenReturn(clienteExistente);
        when(clienteMapper.toClienteResponse(clienteExistente, userMapper.toUserResponse(usuarioExistente), cuentasResponse)).thenReturn(new ClienteResponse(clienteId, "Juan", "Perez", "juan.perez@update.com", "987654321", cuentasResponse, LocalDateTime.now(), LocalDateTime.now(), false));


        ClienteResponse result = clienteService.update(clienteId, clienteRequestUpdate);

        
        assertNotNull(result);
        assertEquals(clienteId, result.getGuid());
        assertEquals("juan.perez@update.com", result.getEmail());
        assertEquals("987654321", result.getTelefono());
        assertEquals(1, result.getCuentas().size());
        assertEquals("cuenta-guid", result.getCuentas().iterator().next().getGuid());
        assertEquals("ES123456789", result.getCuentas().iterator().next().getIban());
    }



    @Test
    void DeleteById() {

        Cliente cliente = Cliente.builder()
                .guid("unique-guid")
                .isDeleted(false)
                .build();

        when(clienteRepository.findByGuid("unique-guid")).thenReturn(Optional.of(cliente));

        clienteService.deleteById("unique-guid");

        verify(clienteRepository, times(1)).save(cliente);
        assertTrue(cliente.getIsDeleted());
    }

    @Test
    void getProductos() {
        String clienteId = "cliente-guid";
        Cliente cliente = Cliente.builder()
                .guid(clienteId)
                .dni("12345678A")
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .build();

        Cuenta cuenta = Cuenta.builder()
                .guid("cuenta-guid")
                .iban("ES123456789")
                .saldo(BigDecimal.valueOf(100))
                .build();

        cliente.setCuentas(Set.of(cuenta));

        TipoCuentaResponse tipoCuentaResponse = new TipoCuentaResponse("tipo-guid", "Cuenta Corriente", BigDecimal.ZERO, LocalDateTime.now(), LocalDateTime.now(), false);
        TarjetaResponse tarjetaResponse = new TarjetaResponse("tarjeta-guid", "1234-5678-1234-5678", LocalDate.now(), BigDecimal.valueOf(200), BigDecimal.valueOf(500), BigDecimal.valueOf(1000), TipoTarjeta.CREDITO, LocalDateTime.now(), LocalDateTime.now(), false);

        when(clienteRepository.findByGuid(clienteId)).thenReturn(Optional.of(cliente));
        when(cuentaMapper.toCuentaResponse(any(Cuenta.class), any(TipoCuentaResponse.class), any(TarjetaResponse.class), any(ClienteDataResponse.class)))
                .thenReturn(new CuentaResponse("cuenta-guid", "ES123456789", BigDecimal.valueOf(100), tipoCuentaResponse, tarjetaResponse, null, LocalDateTime.now(), LocalDateTime.now(), false));
        when(tipoCuentaMapper.toTipoCuentaResponse(any(TipoCuenta.class)))
                .thenReturn(tipoCuentaResponse);
        when(tarjetaMapper.toTarjetaResponse(any(Tarjeta.class)))
                .thenReturn(tarjetaResponse);
        when(clienteMapper.toClienteDataResponse(cliente)).thenReturn(new ClienteDataResponse(clienteId, "Juan", "Perez"));

        ClienteResponseProductos result = clienteService.getProductos(clienteId);

        assertNotNull(result);
        assertEquals(clienteId, result.getGuid());
        assertEquals(1, result.getCuentas().size());
        assertEquals("cuenta-guid", result.getCuentas().iterator().next().getGuid());
        assertEquals("ES123456789", result.getCuentas().iterator().next().getIban());
    }

    @Test
    void getProductosById() {
        // Preparación de los mocks
        String clienteId = "cliente-guid";
        String productoId = "cuenta-guid";  // Puede ser cuenta o tarjeta

        Cliente cliente = Cliente.builder()
                .guid(clienteId)
                .dni("12345678A")
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .build();

        Cuenta cuenta = Cuenta.builder()
                .guid(productoId)
                .iban("ES123456789")
                .saldo(BigDecimal.valueOf(100))
                .build();

        cliente.setCuentas(Set.of(cuenta));

        Tarjeta tarjeta = new Tarjeta("tarjeta-guid", "1234-5678-1234-5678", LocalDate.now(), BigDecimal.valueOf(200), BigDecimal.valueOf(500), BigDecimal.valueOf(1000));

        TipoCuentaResponse tipoCuentaResponse = new TipoCuentaResponse("tipo-guid", "Cuenta Corriente", BigDecimal.ZERO, LocalDateTime.now(), LocalDateTime.now(), false);
        TarjetaResponse tarjetaResponse = new TarjetaResponse("tarjeta-guid", "1234-5678-1234-5678", LocalDate.now(), BigDecimal.valueOf(200), BigDecimal.valueOf(500), BigDecimal.valueOf(1000), TipoTarjeta.CREDITO, LocalDateTime.now(), LocalDateTime.now(), false);

        when(clienteRepository.findByGuid(clienteId)).thenReturn(Optional.of(cliente));
        when(cuentaRepository.findByGuid(productoId)).thenReturn(Optional.of(cuenta));
        when(tarjetaRepository.findByGuid(productoId)).thenReturn(Optional.empty());  // No se encontró tarjeta

        when(cuentaMapper.toCuentaResponse(any(Cuenta.class), any(TipoCuentaResponse.class), any(TarjetaResponse.class), any(ClienteDataResponse.class)))
                .thenReturn(new CuentaResponse("cuenta-guid", "ES123456789", BigDecimal.valueOf(100), tipoCuentaResponse, tarjetaResponse, null, LocalDateTime.now(), LocalDateTime.now(), false));
        when(tipoCuentaMapper.toTipoCuentaResponse(any(TipoCuenta.class)))
                .thenReturn(tipoCuentaResponse);
        when(tarjetaMapper.toTarjetaResponse(any(Tarjeta.class)))
                .thenReturn(tarjetaResponse);
        when(clienteMapper.toClienteDataResponse(cliente)).thenReturn(new ClienteDataResponse(clienteId, "Juan", "Perez"));

        // Llamar al método que se está probando
        ClienteResponseProductosById result = clienteService.getProductosById(clienteId, productoId);

        // Verificar el resultado
        assertNotNull(result);
        assertEquals(clienteId, result.getGuid());
        assertEquals("cuenta-guid", result.getCuenta().getGuid());
        assertNull(result.getTarjeta());  // Como no se encontró la tarjeta
    }



   /* @Test
    void ValidarClienteExistente_Dni() {
        Cliente cliente = Cliente.builder().dni("12345678A").build();
        when(clienteRepository.findByDni("12345678A")).thenReturn(Optional.of(cliente));
        ClienteExistsByDni exception = assertThrows(ClienteExistsByDni.class, () -> {
            clienteService.validarClienteExistente(cliente);
        });
        assertEquals("Cliente con dni '12345678A' ya existente", exception.getMessage());
    }*/
}
