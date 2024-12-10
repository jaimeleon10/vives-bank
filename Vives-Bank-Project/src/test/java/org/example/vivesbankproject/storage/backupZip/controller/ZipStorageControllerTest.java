package org.example.vivesbankproject.storage.backupZip.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import org.example.vivesbankproject.rest.storage.backupZip.controller.ZipStorageController;
import org.example.vivesbankproject.rest.storage.backupZip.services.ZipFileSystemStorage;
import org.example.vivesbankproject.rest.storage.backupZip.services.ZipStorageService;
import org.example.vivesbankproject.rest.storage.exceptions.StorageInternal;
import org.example.vivesbankproject.rest.storage.exceptions.StorageNotFound;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.bson.assertions.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin", password = "adminPassword123", roles = {"ADMIN", "USER"})
class ZipStorageControllerTest {

    private static final Path TEST_DIRECTORY = Paths.get("data", "test");
    @Mock
    private Resource mockResource;
    @Mock
    private HttpServletRequest request;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ZipFileSystemStorage storageService;

    @Autowired
    private ZipStorageService zipStorageService;

    @InjectMocks
    private ZipStorageController zipStorageController;

    @Autowired
    private ZipStorageControllerTest(ZipStorageService zipStorageService){
        this.zipStorageService = zipStorageService;
    }

    @BeforeAll
    static void setUp() throws IOException {
        if (!Files.exists(TEST_DIRECTORY)) {
            Files.createDirectory(TEST_DIRECTORY);
        }
    }

    @AfterEach
    void cleanUpTestFiles() throws IOException {
        if (Files.exists(TEST_DIRECTORY)) {
            Files.walk(TEST_DIRECTORY)
                    .sorted((path1, path2) -> path2.compareTo(path1))
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }
    }

    @AfterAll
    static void cleanUpAndTearDownTestDirectory() throws IOException {
        if (Files.exists(TEST_DIRECTORY)) {
            Files.deleteIfExists(TEST_DIRECTORY);
        }
    }

    @Test
    void generateZip() throws Exception {
        String storedFilename = "generated-file.zip";
        when(storageService.export()).thenReturn(storedFilename);

        mockMvc.perform(post("/storage/zip/generate"))
                .andExpect(status().isOk())
                .andExpect(content().string("Archivo ZIP generado con éxito: " + storedFilename));

        verify(storageService, times(1)).export();
    }

    @Test
    void generateZip_InternalServerError() throws Exception {
        when(storageService.export()).thenThrow(new StorageInternal("Error interno al generar el archivo"));

        mockMvc.perform(post("/storage/zip/generate"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error al generar el archivo ZIP."));

        verify(storageService, times(1)).export();
    }

    @Test
    void importFromZip() throws Exception {
        String filename = "test.zip";

        doNothing().when(storageService).loadFromZip(any(File.class));

        mockMvc.perform(get("/storage/zip/import/" + filename))
                .andExpect(status().isNoContent());

        verify(storageService, times(1)).loadFromZip(any(File.class));
    }

    @Test
    void importFromZipFileNotFound() throws Exception {
        String filename = "nonexistent.zip";

        doThrow(new StorageNotFound("Archivo no encontrado")).when(storageService).loadFromZip(any(File.class));

        mockMvc.perform(get("/storage/zip/import/" + filename))
                .andExpect(status().isNotFound());

        verify(storageService, times(1)).loadFromZip(any(File.class));
    }

    @Test
    void importFromZipInternalServerError() throws Exception {
        String filename = "test.zip";

        doThrow(new RuntimeException("Error desconocido")).when(storageService).loadFromZip(any(File.class));

        mockMvc.perform(get("/storage/zip/import/" + filename))
                .andExpect(status().isInternalServerError());

        verify(storageService, times(1)).loadFromZip(any(File.class));
    }

    @Test
    void serveFile() throws Exception {
        String filename = "test.zip";
        Path filePath = TEST_DIRECTORY.resolve(filename);

        Files.createDirectories(TEST_DIRECTORY);

        if (!Files.exists(filePath)) {
            Files.createFile(filePath);
        }

        when(storageService.loadAsResource(filename)).thenReturn(new org.springframework.core.io.FileSystemResource(filePath.toFile()));

        mockMvc.perform(get("/storage/zip/" + filename))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/zip"));

        verify(storageService, times(1)).loadAsResource(filename);

        Files.deleteIfExists(filePath);
    }


    @Test
    void serveFileFileNotFound() throws Exception {
        String filename = "nonexistent.zip";
        when(storageService.loadAsResource(filename)).thenThrow(new RuntimeException("Archivo no encontrado"));

        mockMvc.perform(get("/storage/zip/" + filename))
                .andExpect(status().isNotFound());

        verify(storageService, times(1)).loadAsResource(filename);
    }

    @Test
    void serveFileContenidoNull() throws Exception {
        String testFilename = "test.zip";
        when(zipStorageService.loadAsResource(testFilename)).thenReturn(mockResource);

        when(mockResource.getFile()).thenReturn(new File("dummy-path"));

        when(request.getServletContext()).thenReturn(mock(ServletContext.class));
        when(request.getServletContext().getMimeType(anyString())).thenReturn(null);

        ResponseEntity<Resource> response = zipStorageController.serveFile(testFilename, request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("application/octet-stream", response.getHeaders().getContentType().toString());
        assertEquals(mockResource, response.getBody());
    }

    @Test
    void deleteFile() throws Exception {
        String filename = "test.zip";
        doNothing().when(storageService).delete(filename);

        ResultActions response = mockMvc.perform(delete("/storage/zip/test.zip"));

        response.andExpect(status().isOk())
                .andExpect(content().string("Archivo ZIP eliminado: " + filename));

        verify(storageService, times(1)).delete(filename);
    }

    @Test
    void testDeleteFile_Error() throws Exception {
        String filename = "test.zip";
        doThrow(new RuntimeException("Fallo al eliminar")).when(storageService).delete(filename);

        ResultActions response = mockMvc.perform(delete("/storage/zip/test.zip"));

        response.andExpect(status().isInternalServerError())
                .andExpect(content().string("Error al eliminar el archivo ZIP: Fallo al eliminar"));

        verify(storageService, times(1)).delete(filename);
    }
}