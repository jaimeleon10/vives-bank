package org.example.vivesbankproject.storage.jsonMovimientos.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import org.example.vivesbankproject.rest.storage.backupZip.services.ZipStorageService;
import org.example.vivesbankproject.rest.storage.exceptions.StorageInternal;
import org.example.vivesbankproject.rest.storage.exceptions.StorageNotFound;
import org.example.vivesbankproject.rest.storage.jsonMovimientos.controller.JsonMovimientosController;
import org.example.vivesbankproject.rest.storage.jsonMovimientos.services.JsonMovimientosFileSystemStorage;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.bson.assertions.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin", password = "adminPassword123", roles = {"ADMIN", "USER"})
class JsonMovimientosControllerTest {

    private static final Path TEST_DIRECTORY = Paths.get("data", "test");

    @Mock
    private Resource mockResource;
    @Mock
    private HttpServletRequest request;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JsonMovimientosFileSystemStorage jsonMovimientosFileSystemStorage;

    @InjectMocks
    private JsonMovimientosController jsonMovimientosController;

    @Autowired
    private JsonMovimientosControllerTest(JsonMovimientosFileSystemStorage jsonMovimientosFileSystemStorage){
        this.jsonMovimientosFileSystemStorage = jsonMovimientosFileSystemStorage;
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
    void generateMovimientosJson() throws Exception {
        String storedFilename = "movimientos.json";
        when(jsonMovimientosFileSystemStorage.storeAll()).thenReturn(storedFilename);

        mockMvc.perform(post("/storage/jsonMovimientos/generate"))
                .andExpect(status().isOk())
                .andExpect(content().string("Archivo JSON de movimientos generado con éxito: " + storedFilename));

        verify(jsonMovimientosFileSystemStorage, times(1)).storeAll();
    }

    @Test
    void generateMovimientosJson_InternalServerError() throws Exception {
        when(jsonMovimientosFileSystemStorage.storeAll()).thenThrow(new StorageInternal("Error interno al generar el archivo"));

        mockMvc.perform(post("/storage/jsonMovimientos/generate"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error al generar el archivo JSON de movimientos."));

        verify(jsonMovimientosFileSystemStorage, times(1)).storeAll();
    }

    @Test
    void generateMovimientoJson() throws Exception {
        String guid = "1234-5678-abcd-efgh";
        String storedFilename = "movimientos_cliente.json";
        when(jsonMovimientosFileSystemStorage.store(guid)).thenReturn(storedFilename);

        mockMvc.perform(post("/storage/jsonMovimientos/generate/{guid}", guid))
                .andExpect(status().isOk())
                .andExpect(content().string("Archivo JSON de movimientos de cliente generado con éxito: " + storedFilename));

        verify(jsonMovimientosFileSystemStorage, times(1)).store(guid);
    }

    @Test
    void generateMovimientoJson_InternalServerError() throws Exception {
        String guid = "1234-5678-abcd-efgh";
        when(jsonMovimientosFileSystemStorage.store(guid)).thenThrow(new StorageInternal("Error interno al generar el archivo"));

        mockMvc.perform(post("/storage/jsonMovimientos/generate/{guid}", guid))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error al generar el archivo JSON de movimientos de cliente."));

        verify(jsonMovimientosFileSystemStorage, times(1)).store(guid);
    }

    @Test
    void serveFile() throws Exception {
        String filename = "movimientos_test_2024-12-01.json";

        byte[] fileContent = "Contenido de prueba".getBytes();
        Resource mockResource = new org.springframework.core.io.ByteArrayResource(fileContent);

        when(jsonMovimientosFileSystemStorage.loadAsResource(filename)).thenReturn(mockResource);

        String contentType = "application/octet-stream";

        mockMvc.perform(get("/storage/jsonMovimientos/{filename}", filename))
                .andExpect(status().isOk())
                .andExpect(content().bytes(fileContent))
                .andExpect(content().contentType(MediaType.parseMediaType(contentType)));
    }

    @Test
    void serveFileFileNotFound() throws Exception {
        String filename = "ficheroinexistente.json";
        when(jsonMovimientosFileSystemStorage.loadAsResource(filename)).thenThrow(new StorageNotFound("File not found"));

        mockMvc.perform(get("/storage/jsonMovimientos/{filename}", filename))
                .andExpect(status().isNotFound());
    }

    @Test
    void serveFileContenidoNull() throws Exception {
        String testFilename = "test.json";
        when(jsonMovimientosFileSystemStorage.loadAsResource(testFilename)).thenReturn(mockResource);

        when(mockResource.getFile()).thenReturn(new File("dummy-path"));

        when(request.getServletContext()).thenReturn(mock(ServletContext.class));
        when(request.getServletContext().getMimeType(anyString())).thenReturn(null);

        ResponseEntity<Resource> response = jsonMovimientosController.serveFile(testFilename, request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("application/octet-stream", response.getHeaders().getContentType().toString());
        assertEquals(mockResource, response.getBody());
    }

    @Test
    void listAllFiles() throws Exception {
        List<Path> mockFiles = new ArrayList<>();
        mockFiles.add(TEST_DIRECTORY.resolve("movimientos_test_2024-12-01.json"));
        mockFiles.add(TEST_DIRECTORY.resolve("movimientos_test_2024-12-02.json"));

        when(jsonMovimientosFileSystemStorage.loadAll()).thenReturn(mockFiles.stream());

        mockMvc.perform(get("/storage/jsonMovimientos/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").value("data\\test\\movimientos_test_2024-12-01.json"))
                .andExpect(jsonPath("$[1]").value("data\\test\\movimientos_test_2024-12-02.json"));
    }

    @Test
    void listAllFiles_error() throws Exception {
        when(jsonMovimientosFileSystemStorage.loadAll()).thenThrow(new StorageInternal("Error al obtener archivos"));

        mockMvc.perform(get("/storage/jsonMovimientos/list"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json("[]"));
    }
}