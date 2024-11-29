package org.example.vivesbankproject.cliente.service;

import org.example.vivesbankproject.cliente.dto.ClienteResponse;
import org.example.vivesbankproject.cliente.exceptions.ClienteNotFound;
import org.example.vivesbankproject.cliente.mappers.ClienteMapper;
import org.example.vivesbankproject.cliente.models.Cliente;
import org.example.vivesbankproject.cliente.repositories.ClienteRepository;
import org.example.vivesbankproject.storage.images.services.StorageImagesService;
import org.example.vivesbankproject.users.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceImplFotoTest {

    @Mock private ClienteRepository clienteRepository;
    @Mock private StorageImagesService storageImagesService;
    @Mock private ClienteMapper clienteMapper;
    @InjectMocks private ClienteServiceImpl clienteService;

    private Cliente cliente;
    private User user;
    private MultipartFile mockFile;
    private ClienteResponse expectedResponse;

    @BeforeEach
    void setUp() {
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
    void updateDniFoto_ClienteNotFound() {
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
    @DisplayName("Update Profile Photo - Client Not Found")
    void testUpdateProfileFoto_ClientNotFound() {
        when(clienteRepository.findByGuid(cliente.getGuid())).thenReturn(Optional.empty());

        assertThrows(ClienteNotFound.class, () -> clienteService.updateProfileFoto(cliente.getGuid(), mockFile));

        verify(storageImagesService, never()).delete(any());
        verify(storageImagesService, never()).store(any());
        verify(clienteRepository, never()).save(any());
    }

    @Test
    void updateDniFoto_FotoNoExistente() {
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
}