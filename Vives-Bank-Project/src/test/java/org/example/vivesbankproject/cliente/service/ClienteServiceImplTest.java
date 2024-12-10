package org.example.vivesbankproject.cliente.service;

import org.example.vivesbankproject.rest.cliente.dto.ClienteProducto;
import org.example.vivesbankproject.rest.cliente.dto.ClienteRequestSave;
import org.example.vivesbankproject.rest.cliente.dto.ClienteRequestUpdate;
import org.example.vivesbankproject.rest.cliente.dto.ClienteResponse;
import org.example.vivesbankproject.rest.cliente.exceptions.*;
import org.example.vivesbankproject.rest.cliente.mappers.ClienteMapper;
import org.example.vivesbankproject.rest.cliente.models.Cliente;
import org.example.vivesbankproject.rest.cliente.models.Direccion;
import org.example.vivesbankproject.rest.cliente.repositories.ClienteRepository;
import org.example.vivesbankproject.rest.cliente.service.ClienteServiceImpl;
import org.example.vivesbankproject.rest.cuenta.dto.tipoCuenta.TipoCuentaResponseCatalogo;
import org.example.vivesbankproject.rest.cuenta.mappers.TipoCuentaMapper;
import org.example.vivesbankproject.rest.cuenta.models.TipoCuenta;
import org.example.vivesbankproject.rest.cuenta.repositories.CuentaRepository;
import org.example.vivesbankproject.rest.cuenta.repositories.TipoCuentaRepository;
import org.example.vivesbankproject.rest.storage.images.services.StorageImagesService;
import org.example.vivesbankproject.rest.tarjeta.models.TipoTarjeta;
import org.example.vivesbankproject.rest.tarjeta.repositories.TarjetaRepository;
import org.example.vivesbankproject.rest.users.exceptions.UserNotFoundById;
import org.example.vivesbankproject.rest.users.models.User;
import org.example.vivesbankproject.rest.users.repositories.UserRepository;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceImplTest {

    @Mock private ClienteRepository clienteRepository;
    @Mock private StorageImagesService storageImagesService;
    @Mock private ClienteMapper clienteMapper;
    @Mock private TarjetaRepository tarjetaRepository;
    @Mock private UserRepository userRepository;
    @Mock private CuentaRepository cuentaRepository;
    @Mock private RedisTemplate<String, String> redisTemplate;
    @Mock private TipoCuentaRepository tipoCuentaRepository;
    @Mock private TipoCuentaMapper tipoCuentaMapper;
    @InjectMocks private ClienteServiceImpl clienteService;

    private Cliente cliente;
    private User user;
    private Cliente cliente2;
    private User user2;
    private MultipartFile mockFile;
    private ClienteResponse expectedResponse;


    @BeforeEach
    void setUp() {
        user = new User();
        user.setGuid("user-guid");

        cliente = new Cliente();
        cliente.setGuid("cliente-guid");
        cliente.setDni("12345678A");
        cliente.setEmail("test@example.com");
        cliente.setTelefono("987654321");
        cliente.setUser(user);
        cliente.setFotoDni("foto_dni_1.jpg");
        cliente.setFotoPerfil("foto_perfil_1.jpg");

        expectedResponse = new ClienteResponse();
        expectedResponse.setGuid(cliente.getGuid());

        user2 = new User();
        user2.setGuid("user-guid2");

        cliente2 = new Cliente();
        cliente2.setGuid("cliente-guid2");
        cliente2.setDni("12345678B");
        cliente2.setEmail("test2@example.com");
        cliente2.setTelefono("123456789");
        cliente2.setUser(user2);
        cliente2.setFotoDni("foto_dni_2.jpg");
        cliente2.setFotoPerfil("foto_perfil_2.jpg");

        mockFile = new MockMultipartFile(
                "file",
                "test-photo.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        expectedResponse = new ClienteResponse();
        expectedResponse.setGuid(cliente2.getGuid());
    }

    @Test
    void GetAll() {
        Pageable pageable = PageRequest.of(0, 10);

        when(clienteRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(Collections.singletonList(cliente)));

        when(clienteMapper.toClienteResponse(eq(cliente), eq(user.getGuid())))
                .thenReturn(expectedResponse);

        Page<ClienteResponse> result = clienteService.getAll(
                Optional.of("12345678A"),
                Optional.of("Test"),
                Optional.of("User"),
                Optional.of("test@example.com"),
                Optional.of("123456789"),
                pageable
        );

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(expectedResponse, result.getContent().get(0));

        verify(clienteRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getById() {
        when(clienteRepository.findByGuid(cliente.getGuid()))
                .thenReturn(Optional.of(cliente));

        when(clienteMapper.toClienteResponse(eq(cliente), eq(user.getGuid())))
                .thenReturn(expectedResponse);

        ClienteResponse result = clienteService.getById(cliente.getGuid());

        assertNotNull(result);
        assertEquals(expectedResponse, result);

        verify(clienteRepository).findByGuid(cliente.getGuid());
    }

    @Test
    void getByIdNotFound() {
        when(clienteRepository.findByGuid(cliente.getGuid()))
                .thenReturn(Optional.empty());

        assertThrows(ClienteNotFound.class, () ->
                clienteService.getById(cliente.getGuid())
        );
    }

    @Test
    void getUserByGuid() {
        when(clienteRepository.findByUserGuid(user.getGuid())).thenReturn(Optional.of(cliente));
        when(clienteMapper.toClienteResponse(eq(cliente), eq(user.getGuid()))).thenReturn(expectedResponse);

        ClienteResponse result = clienteService.getUserAuthenticatedByGuid(user.getGuid());

        assertNotNull(result);
        assertEquals(expectedResponse, result);

        verify(clienteRepository).findByUserGuid(user.getGuid());
    }
    @Test
    void getUserByGuidNotFound() {
        when(clienteRepository.findByUserGuid(user.getGuid())).thenReturn(Optional.empty());

        assertThrows(ClienteNotFoundByUser.class, () -> clienteService.getUserAuthenticatedByGuid(user.getGuid()));
    }

    @Test
    void getByDni() {
        when(clienteRepository.findByDni(cliente.getDni()))
                .thenReturn(Optional.of(cliente));

        when(clienteMapper.toClienteResponse(eq(cliente), eq(user.getGuid())))
                .thenReturn(expectedResponse);

        ClienteResponse result = clienteService.getByDni(cliente.getDni());

        assertNotNull(result);
        assertEquals(expectedResponse, result);

        verify(clienteRepository).findByDni(cliente.getDni());
    }

    @Test
    void getByDniNotFound() {
        when(clienteRepository.findByDni(cliente.getDni()))
                .thenReturn(Optional.empty());

        assertThrows(ClienteNotFoundByDni.class, () ->
                clienteService.getByDni(cliente.getDni())
        );
    }



    @Test
    void Save() {
        ClienteRequestSave requestSave = new ClienteRequestSave();
        requestSave.setUserId(user.getGuid());

        when(clienteRepository.existsByUserGuid(user.getGuid())).thenReturn(false);
        when(userRepository.findByGuid(user.getGuid())).thenReturn(Optional.of(user));
        when(clienteMapper.toCliente(eq(requestSave), eq(user), any())).thenReturn(cliente);
        when(clienteRepository.save(cliente)).thenReturn(cliente);
        when(clienteMapper.toClienteResponse(eq(cliente), eq(user.getGuid()))).thenReturn(expectedResponse);

        ClienteResponse result = clienteService.save(requestSave);

        assertNotNull(result);
        assertEquals(expectedResponse, result);

        verify(clienteRepository).save(cliente);
    }

    @Test
    void saveUserExistente() {
        ClienteRequestSave requestSave = new ClienteRequestSave();
        requestSave.setUserId(user.getGuid());

        when(clienteRepository.existsByUserGuid(user.getGuid()))
                .thenReturn(true);

        assertThrows(ClienteUserAlreadyAssigned.class, () -> clienteService.save(requestSave));
    }

    @Test
    void deleteById() {
        when(clienteRepository.findByGuid(cliente.getGuid())).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(cliente)).thenReturn(cliente);

        clienteService.deleteById(cliente.getGuid());

        assertTrue(cliente.getIsDeleted());
        verify(clienteRepository).save(cliente);
    }

    @Test
    void Update() {

        String clienteId = "12345";
        ClienteRequestUpdate clienteRequestUpdate = ClienteRequestUpdate.builder()
                .nombre("Juan")
                .apellidos("Pérez García")
                .calle("Calle Falsa")
                .numero("10")
                .codigoPostal("28001")
                .piso("2")
                .letra("B")
                .email("newemail@example.com")
                .telefono("123456789")
                .fotoPerfil("https://example.com/foto-perfil.jpg")
                .fotoDni("https://example.com/foto-dni.jpg")
                .build();

        Cliente clienteExistente = new Cliente();
        clienteExistente.setTelefono("987654321");
        clienteExistente.setEmail("oldemail@example.com");

        User mockUser = new User();
        mockUser.setGuid("user-guid");
        clienteExistente.setUser(mockUser);

        Cliente clienteSave = new Cliente();
        ClienteResponse clienteResponse = new ClienteResponse();

        when(clienteRepository.findByGuid(clienteId)).thenReturn(Optional.of(clienteExistente));
        when(clienteRepository.findByTelefono(clienteRequestUpdate.getTelefono())).thenReturn(Optional.empty());
        when(clienteRepository.findByEmail(clienteRequestUpdate.getEmail())).thenReturn(Optional.empty());
        when(clienteMapper.toClienteUpdate(any(), any(), any(), any())).thenReturn(clienteSave);
        when(clienteRepository.save(any())).thenReturn(clienteSave);
        when(clienteMapper.toClienteResponse(any(), any())).thenReturn(clienteResponse);

        ClienteResponse result = clienteService.update(clienteId, clienteRequestUpdate);

        assertNotNull(result);
        verify(clienteRepository).findByGuid(clienteId);
        verify(clienteRepository).save(any(Cliente.class));
        verify(clienteMapper).toClienteResponse(any(Cliente.class), any());
    }

    @Test
    void UpdateNotFound() {

        String clienteId = "12345";
        ClienteRequestUpdate clienteRequestUpdate = ClienteRequestUpdate.builder()
                .nombre("Juan")
                .apellidos("Pérez García")
                .calle("Calle Falsa")
                .numero("10")
                .codigoPostal("28001")
                .piso("2")
                .letra("B")
                .email("newemail@example.com")
                .telefono("123456789")
                .fotoPerfil("https://example.com/foto-perfil.jpg")
                .fotoDni("https://example.com/foto-dni.jpg")
                .build();

        when(clienteRepository.findByGuid(clienteId)).thenReturn(Optional.empty());

        ClienteNotFound exception = assertThrows(
                ClienteNotFound.class,
                () -> clienteService.update(clienteId, clienteRequestUpdate)
        );

        assertEquals("Cliente con id '" + clienteId + "' no encontrado", exception.getMessage());
        verify(clienteRepository).findByGuid(clienteId);
        verifyNoMoreInteractions(clienteRepository);
    }

    @Test
    void updateDniFoto() {
        when(clienteRepository.findByGuid(cliente.getGuid())).thenReturn(Optional.of(cliente));
        when(storageImagesService.store(any(MultipartFile.class))).thenReturn("foto_dni_2.jpg");
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);
        when(clienteMapper.toClienteResponse(any(Cliente.class), any(String.class))).thenReturn(expectedResponse);

        ClienteResponse response = clienteService.updateDniFoto(cliente.getGuid(), mockFile);

        assertNotNull(response);

        verify(clienteRepository).findByGuid(cliente.getGuid());
        verify(storageImagesService).delete("foto_dni_1.jpg");
        verify(storageImagesService).store(mockFile);
        verify(clienteRepository).save(cliente);
        verify(clienteMapper).toClienteResponse(cliente, user.getGuid());
    }

    @Test
    void updateDniFotoClienteNotFound() {
        when(clienteRepository.findByGuid(cliente.getGuid())).thenReturn(Optional.empty());

        assertThrows(ClienteNotFound.class, () -> clienteService.updateDniFoto(cliente.getGuid(), mockFile));

        verify(storageImagesService, never()).delete(any());
        verify(storageImagesService, never()).store(any());
        verify(clienteRepository, never()).save(any());
    }

    @Test
    void updateProfileFoto() {
        when(clienteRepository.findByGuid(cliente.getGuid())).thenReturn(Optional.of(cliente));
        when(storageImagesService.store(any(MultipartFile.class))).thenReturn("foto_perfil_2.jpg");
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);
        when(clienteMapper.toClienteResponse(any(Cliente.class), any(String.class))).thenReturn(expectedResponse);

        ClienteResponse response = clienteService.updateProfileFoto(cliente.getGuid(), mockFile);

        assertNotNull(response);

        verify(clienteRepository).findByGuid(cliente.getGuid());
        verify(storageImagesService).delete("foto_perfil_1.jpg");
        verify(storageImagesService).store(mockFile);
        verify(clienteRepository).save(cliente);
        verify(clienteMapper).toClienteResponse(cliente, user.getGuid());
    }

    @Test
    void updateProfileFotoClientNotFound() {
        when(clienteRepository.findByGuid(cliente.getGuid())).thenReturn(Optional.empty());

        assertThrows(ClienteNotFound.class, () -> clienteService.updateProfileFoto(cliente.getGuid(), mockFile));

        verify(storageImagesService, never()).delete(any());
        verify(storageImagesService, never()).store(any());
        verify(clienteRepository, never()).save(any());
    }

    @Test
    void updateDniFotoFotoNoExistente() {
        cliente.setFotoDni(null);

        when(clienteRepository.findByGuid(cliente.getGuid())).thenReturn(Optional.of(cliente));
        when(storageImagesService.store(any(MultipartFile.class))).thenReturn("foto_dni_2.jpg");
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);
        when(clienteMapper.toClienteResponse(any(Cliente.class), any(String.class))).thenReturn(expectedResponse);

        ClienteResponse response = clienteService.updateDniFoto(cliente.getGuid(), mockFile);

        assertNotNull(response);

        verify(clienteRepository).findByGuid(cliente.getGuid());
        verify(storageImagesService, never()).delete(any());
        verify(storageImagesService).store(mockFile);
        verify(clienteRepository).save(cliente);
    }




    @Test
    void shouldUpdateUserSuccessfully() {
        String guid = "12345";
        User user = new User();
        user.setGuid("1");

        ClienteRequestUpdate request = new ClienteRequestUpdate();
        request.setTelefono("987654321");
        request.setEmail("newuser@example.com");

        Cliente clienteAutenticado = new Cliente();
        clienteAutenticado.setTelefono("123456789");
        clienteAutenticado.setUser(user);
        clienteAutenticado.setEmail("user@example.com");

        Direccion direccion = Direccion.builder()
                .calle(request.getCalle())
                .numero(request.getNumero())
                .codigoPostal(request.getCodigoPostal())
                .piso(request.getPiso())
                .letra(request.getLetra())
                .build();

        Cliente clienteActualizado = new Cliente();
        clienteActualizado.setTelefono("987654321");
        clienteActualizado.setUser(user);
        clienteActualizado.setEmail("newuser@example.com");

        ClienteResponse clienteResponse = new ClienteResponse();
        clienteResponse.setTelefono("987654321");
        clienteResponse.setUserId(user.getGuid());
        clienteResponse.setEmail("newuser@example.com");

        when(clienteRepository.findByUserGuid(guid)).thenReturn(Optional.of(clienteAutenticado));
        when(clienteRepository.findByTelefono(request.getTelefono())).thenReturn(Optional.empty());
        when(clienteRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(clienteMapper.toClienteUpdate(request, clienteAutenticado, clienteAutenticado.getUser(), direccion)).thenReturn(clienteActualizado);
        when(clienteRepository.save(clienteActualizado)).thenReturn(clienteActualizado);
        when(clienteMapper.toClienteResponse(clienteActualizado, clienteActualizado.getUser().getGuid())).thenReturn(clienteResponse);

        ClienteResponse response = clienteService.updateUserAuthenticated(guid, request);

        verify(clienteRepository, times(1)).findByUserGuid(guid);
        verify(clienteRepository, times(1)).findByTelefono(request.getTelefono());
        verify(clienteRepository, times(1)).findByEmail(request.getEmail());
        verify(clienteRepository, times(1)).save(clienteActualizado);
        verify(clienteMapper, times(1)).toClienteUpdate(request, clienteAutenticado, clienteAutenticado.getUser(), direccion);
        verify(clienteMapper, times(1)).toClienteResponse(clienteActualizado, clienteActualizado.getUser().getGuid());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        String guid = "12345";
        ClienteRequestUpdate request = new ClienteRequestUpdate();

        when(clienteRepository.findByUserGuid(guid)).thenReturn(Optional.empty());

        assertThrows(ClienteNotFound.class, () -> clienteService.updateUserAuthenticated(guid, request));

        verify(clienteRepository, times(1)).findByUserGuid(guid);
    }

    @Test
    void shouldThrowExceptionWhenTelefonoExists() {
        String guid = "12345";
        ClienteRequestUpdate request = new ClienteRequestUpdate();
        request.setTelefono("11");
        Cliente clienteAutenticado = new Cliente();

        when(clienteRepository.findByUserGuid(guid)).thenReturn(Optional.of(clienteAutenticado));
        when(clienteRepository.findByTelefono(request.getTelefono())).thenThrow(new ClienteExistsByTelefono(request.getTelefono()));

        assertThrows(ClienteExistsByTelefono.class, () -> clienteService.updateUserAuthenticated(guid, request));

        verify(clienteRepository, times(1)).findByUserGuid(guid);
        verify(clienteRepository, times(1)).findByTelefono(request.getTelefono());
    }

    @Test
    void shouldThrowExceptionWhenEmailExists() {
        String guid = "12345";
        ClienteRequestUpdate request = new ClienteRequestUpdate();
        request.setEmail("email@example.com");
        Cliente clienteAutenticado = new Cliente();

        when(clienteRepository.findByUserGuid(guid)).thenReturn(Optional.of(clienteAutenticado));
        when(clienteRepository.findByEmail(request.getEmail())).thenThrow(new ClienteExistsByEmail(request.getEmail()));

        assertThrows(ClienteExistsByEmail.class, () -> clienteService.updateUserAuthenticated(guid, request));

        verify(clienteRepository, times(1)).findByUserGuid(guid);
        verify(clienteRepository, times(1)).findByEmail(request.getEmail());
    }


    @Test
    void derechoAlOlvido() {
        String userGuid = "user-guid";
        Set<String> mockKeys = Set.of("clientes:1", "clientes:2");

        when(userRepository.findByGuid(userGuid)).thenReturn(Optional.of(user));
        when(clienteRepository.findByUserGuid(userGuid)).thenReturn(Optional.of(cliente));
        when(redisTemplate.keys("clientes::*")).thenReturn(mockKeys);

        String result = clienteService.derechoAlOlvido(userGuid);

        assertNotNull(result);
        assertEquals("El cliente con guid 'cliente-guid' ejerció su derecho al olvido borrando todos sus datos personales", result);

        verify(clienteRepository).delete(cliente);
        verify(userRepository).delete(user);
        verify(redisTemplate).keys("clientes::*");
        verify(redisTemplate).delete(mockKeys);
    }

    @Test
    void DerechoAlOlvidoUserNotFound() {
        String userGuid = "user-guid-not-found";

        when(userRepository.findByGuid(userGuid)).thenReturn(Optional.empty());

        UserNotFoundById exception = assertThrows(UserNotFoundById.class, () -> clienteService.derechoAlOlvido(userGuid));
        assertEquals("Usuario con id 'user-guid-not-found' no encontrado", exception.getMessage());

        verify(userRepository).findByGuid(userGuid);
    }

    @Test
    void DerechoAlOlvidoClienteNotFound() {
        String userGuid = "user-guid";

        when(userRepository.findByGuid(userGuid)).thenReturn(Optional.of(user));
        when(clienteRepository.findByUserGuid(userGuid)).thenReturn(Optional.empty());

        ClienteNotFoundByUser exception = assertThrows(ClienteNotFoundByUser.class, () -> clienteService.derechoAlOlvido(userGuid));
        assertEquals("No existe ningún cliente asignado al usuario con id: user-guid", exception.getMessage());

        verify(userRepository).findByGuid(userGuid);
        verify(clienteRepository).findByUserGuid(userGuid);
    }

    @Test
    void DerechoAlOlvidoDeleteException() {
        String userGuid = "user-guid";

        when(userRepository.findByGuid(userGuid)).thenReturn(Optional.of(user));
        when(clienteRepository.findByUserGuid(userGuid)).thenReturn(Optional.of(cliente));
        doThrow(new RuntimeException()).when(clienteRepository).delete(cliente);

        ClienteNotDeleted exception = assertThrows(ClienteNotDeleted.class, () -> clienteService.derechoAlOlvido(userGuid));
        assertEquals("Cliente con guid 'cliente-guid' no se pudo borrar", exception.getMessage());

        verify(clienteRepository).delete(cliente);
    }

    @Test
    void validarClienteExistente() {
        // Simula que no existen clientes con el mismo DNI, teléfono o email
        when(clienteRepository.findByDni(cliente.getDni())).thenReturn(Optional.empty());
        when(clienteRepository.findByTelefono(cliente.getTelefono())).thenReturn(Optional.empty());
        when(clienteRepository.findByEmail(cliente.getEmail())).thenReturn(Optional.empty());

        // Ejecuta el método y verifica que no lanza ninguna excepción
        assertDoesNotThrow(() -> clienteService.validarClienteExistente(cliente));

        verify(clienteRepository).findByDni(cliente.getDni());
        verify(clienteRepository).findByTelefono(cliente.getTelefono());
        verify(clienteRepository).findByEmail(cliente.getEmail());
    }

    @Test
    void validarClienteExistenteDniExists() {
        // Simula que ya existe un cliente con el mismo DNI
        when(clienteRepository.findByDni(cliente.getDni())).thenReturn(Optional.of(cliente));

        // Verifica que se lanza la excepción correcta
        ClienteExistsByDni exception = assertThrows(
                ClienteExistsByDni.class,
                () -> clienteService.validarClienteExistente(cliente)
        );

        assertEquals("Cliente con dni '12345678A' ya existente", exception.getMessage());
        verify(clienteRepository).findByDni(cliente.getDni());
    }

    @Test
    void validarClienteExistenteTelefonoExists() {
        // Simula que ya existe un cliente con el mismo teléfono
        when(clienteRepository.findByDni(cliente.getDni())).thenReturn(Optional.empty());
        when(clienteRepository.findByTelefono(cliente.getTelefono())).thenReturn(Optional.of(cliente));

        // Verifica que se lanza la excepción correcta
        ClienteExistsByTelefono exception = assertThrows(
                ClienteExistsByTelefono.class,
                () -> clienteService.validarClienteExistente(cliente)
        );

        assertEquals("Cliente con telefono '987654321' ya existente", exception.getMessage());
        verify(clienteRepository).findByTelefono(cliente.getTelefono());
    }

    @Test
    void validarClienteExistenteEmailExists() {
        // Simula que ya existe un cliente con el mismo email
        when(clienteRepository.findByDni(cliente.getDni())).thenReturn(Optional.empty());
        when(clienteRepository.findByTelefono(cliente.getTelefono())).thenReturn(Optional.empty());
        when(clienteRepository.findByEmail(cliente.getEmail())).thenReturn(Optional.of(cliente));

        // Verifica que se lanza la excepción correcta
        ClienteExistsByEmail exception = assertThrows(
                ClienteExistsByEmail.class,
                () -> clienteService.validarClienteExistente(cliente)
        );

        assertEquals("Cliente con email 'test@example.com' ya existente", exception.getMessage());
        verify(clienteRepository).findByEmail(cliente.getEmail());
    }

    @Test
    void GetCatalogo() {

        List<TipoCuenta> tiposCuentas = List.of(
                TipoCuenta.builder()
                        .id(1L)
                        .nombre("Cuenta Corriente")
                        .interes(BigDecimal.valueOf(1.5))
                        .build(),
                TipoCuenta.builder()
                        .id(2L)
                        .nombre("Cuenta de Ahorros")
                        .interes(BigDecimal.valueOf(2.0))
                        .build()
        );

        List<TipoCuentaResponseCatalogo> tiposCuentasResponse = List.of(
                TipoCuentaResponseCatalogo.builder()
                        .nombre("Cuenta Corriente")
                        .interes("1.5")
                        .build(),
                TipoCuentaResponseCatalogo.builder()
                        .nombre("Cuenta de Ahorros")
                        .interes("2.0")
                        .build()
        );

        when(tipoCuentaRepository.findAll()).thenReturn(tiposCuentas);
        when(tipoCuentaMapper.toTipoCuentaResponseCatalogo(tiposCuentas.get(0))).thenReturn(tiposCuentasResponse.get(0));
        when(tipoCuentaMapper.toTipoCuentaResponseCatalogo(tiposCuentas.get(1))).thenReturn(tiposCuentasResponse.get(1));

        List<TipoTarjeta> tiposTarjetas = Arrays.asList(TipoTarjeta.values());

        ClienteProducto result = clienteService.getCatalogue();

        assertNotNull(result);
        assertEquals(tiposCuentasResponse, result.getTiposCuentas());
        assertEquals(tiposTarjetas, result.getTiposTarjetas());

        verify(tipoCuentaRepository).findAll();
        verify(tipoCuentaMapper).toTipoCuentaResponseCatalogo(tiposCuentas.get(0));
        verify(tipoCuentaMapper).toTipoCuentaResponseCatalogo(tiposCuentas.get(1));
    }

    @Test
    void GetCatalogoEmpty() {

        when(tipoCuentaRepository.findAll()).thenReturn(Collections.emptyList());

        List<TipoTarjeta> tiposTarjetas = Arrays.asList(TipoTarjeta.values());

        ClienteProducto result = clienteService.getCatalogue();

        assertNotNull(result);
        assertTrue(result.getTiposCuentas().isEmpty());
        assertEquals(tiposTarjetas, result.getTiposTarjetas());

        verify(tipoCuentaRepository).findAll();
        verifyNoInteractions(tipoCuentaMapper);
    }
}