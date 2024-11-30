package org.example.vivesbankproject.cliente.service;

import org.example.vivesbankproject.cliente.dto.ClienteRequestSave;
import org.example.vivesbankproject.cliente.dto.ClienteResponse;
import org.example.vivesbankproject.cliente.exceptions.ClienteNotFound;
import org.example.vivesbankproject.cliente.exceptions.ClienteNotFoundByDni;
import org.example.vivesbankproject.cliente.exceptions.ClienteNotFoundByUser;
import org.example.vivesbankproject.cliente.exceptions.ClienteUserAlreadyAssigned;
import org.example.vivesbankproject.cliente.mappers.ClienteMapper;
import org.example.vivesbankproject.cliente.models.Cliente;
import org.example.vivesbankproject.cliente.repositories.ClienteRepository;
import org.example.vivesbankproject.storage.images.services.StorageImagesService;
import org.example.vivesbankproject.users.models.User;
import org.example.vivesbankproject.users.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceImplTest {

    @Mock private ClienteRepository clienteRepository;
    @Mock private StorageImagesService storageImagesService;
    @Mock private ClienteMapper clienteMapper;
    @InjectMocks private ClienteServiceImpl clienteService;
    @Mock private UserRepository userRepository;

    private Cliente cliente;
    private User user;
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
        cliente.setUser(user);

        expectedResponse = new ClienteResponse();
        expectedResponse.setGuid(cliente.getGuid());

        user = new User();
        user.setGuid("Guid-User");

        cliente = new Cliente();
        cliente.setGuid("Guid-cliente");
        cliente.setUser(user);
        cliente.setFotoDni("foto_dni_1.jpg");
        cliente.setFotoPerfil("foto_perfil_1.jpg");

        mockFile = new MockMultipartFile(
                "file",
                "test-photo.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        expectedResponse = new ClienteResponse();
        expectedResponse.setGuid(cliente.getGuid());
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
    void GetAllCliente() {
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
    void testSaveSuccess() {
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
    void getUserByGuid() {
        when(clienteRepository.findByUserGuid(user.getGuid())).thenReturn(Optional.of(cliente));
        when(clienteMapper.toClienteResponse(eq(cliente), eq(user.getGuid()))).thenReturn(expectedResponse);

        ClienteResponse result = clienteService.getUserByGuid(user.getGuid());

        assertNotNull(result);
        assertEquals(expectedResponse, result);

        verify(clienteRepository).findByUserGuid(user.getGuid());
    }

    @Test
    void getUserByGuidNotFound() {
        when(clienteRepository.findByUserGuid(user.getGuid())).thenReturn(Optional.empty());

        assertThrows(ClienteNotFoundByUser.class, () -> clienteService.getUserByGuid(user.getGuid()));
    }
}