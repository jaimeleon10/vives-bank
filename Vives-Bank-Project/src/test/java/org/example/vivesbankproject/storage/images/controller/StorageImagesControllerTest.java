package org.example.vivesbankproject.storage.images.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import org.example.vivesbankproject.rest.storage.images.controller.StorageImagesController;
import org.example.vivesbankproject.rest.storage.images.services.StorageImagesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StorageImagesControllerTest {

    @Mock
    private StorageImagesService storageImagesService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private ServletContext servletContext;

    @Mock
    private Resource mockResource;

    @InjectMocks
    private StorageImagesController storageImagesController;

    private File mockFile;

    @BeforeEach
    void setUp() throws IOException {
        mockFile = mock(File.class);
        when(mockFile.getAbsolutePath()).thenReturn("/data/image.jpg");
    }

    @Test
    void uploadFotoDni() throws IOException {
        String filename = "test-dni.jpg";

        when(storageImagesService.loadAsResource(filename)).thenReturn(mockResource);
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getMimeType(anyString())).thenReturn("image/jpeg");
        when(mockResource.getFile()).thenReturn(mockFile);

        ResponseEntity<Resource> response = storageImagesController.UploadFotoDni(filename, request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(MediaType.IMAGE_JPEG, response.getHeaders().getContentType());
        assertEquals(mockResource, response.getBody());

        verify(storageImagesService).loadAsResource(filename);
    }

    @Test
    void uploadFotoPerfil() throws IOException {
        String filename = "test-profile.jpg";

        when(storageImagesService.loadAsResource(filename)).thenReturn(mockResource);
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getMimeType(anyString())).thenReturn("image/jpeg");
        when(mockResource.getFile()).thenReturn(mockFile);

        ResponseEntity<Resource> response = storageImagesController.UploadFotoPerfil(filename, request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(MediaType.IMAGE_JPEG, response.getHeaders().getContentType());
        assertEquals(mockResource, response.getBody());

        verify(storageImagesService).loadAsResource(filename);
    }

    @Test
    void uploadFotoDniSinContenido() throws Exception {
        String filename = "test-dni.jpg";

        when(storageImagesService.loadAsResource(filename)).thenReturn(mockResource);
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getMimeType(anyString())).thenReturn(null);
        when(mockResource.getFile()).thenReturn(mockFile);

        ResponseEntity<Resource> response = storageImagesController.UploadFotoDni(filename, request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(MediaType.APPLICATION_OCTET_STREAM, response.getHeaders().getContentType());
        assertEquals(mockResource, response.getBody());
    }

    @Test
    void uploadFotoPerfilSinContenido() throws IOException {
        String filename = "test-profile.jpg";

        when(storageImagesService.loadAsResource(filename)).thenReturn(mockResource);
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getMimeType(anyString())).thenReturn(null);
        when(mockResource.getFile()).thenReturn(mockFile);

        ResponseEntity<Resource> response = storageImagesController.UploadFotoPerfil(filename, request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(MediaType.APPLICATION_OCTET_STREAM, response.getHeaders().getContentType());
        assertEquals(mockResource, response.getBody());
    }

    @Test
    @DisplayName("Private Method getResourceResponseEntity - Happy Path")
    void testGetResourceResponseEntity_HappyPath() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String filename = "test-image.jpg";

        when(storageImagesService.loadAsResource(filename)).thenReturn(mockResource);
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getMimeType(anyString())).thenReturn("image/jpeg");
        when(mockResource.getFile()).thenReturn(mockFile);

        java.lang.reflect.Method method = StorageImagesController.class.getDeclaredMethod(
                "getResourceResponseEntity", String.class, HttpServletRequest.class
        );
        method.setAccessible(true);

        ResponseEntity<Resource> response = (ResponseEntity<Resource>) method.invoke(
                storageImagesController, filename, request
        );

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(MediaType.IMAGE_JPEG, response.getHeaders().getContentType());
        assertEquals(mockResource, response.getBody());
    }
}