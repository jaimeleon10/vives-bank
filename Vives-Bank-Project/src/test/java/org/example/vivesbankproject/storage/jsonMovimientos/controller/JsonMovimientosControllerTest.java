package org.example.vivesbankproject.storage.jsonMovimientos.controller;

import org.example.vivesbankproject.storage.exceptions.StorageNotFound;
import org.example.vivesbankproject.storage.jsonClientes.controller.JsonClientesController;
import org.example.vivesbankproject.storage.jsonClientes.services.JsonClientesStorageService;
import org.example.vivesbankproject.storage.jsonMovimientos.services.JsonMovimientosFileSystemStorage;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin", password = "adminPassword123", roles = {"ADMIN", "USER"})
class JsonMovimientosControllerTest {

    private static final Path TEST_DIRECTORY = Paths.get("data", "test");

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JsonMovimientosFileSystemStorage jsonMovimientosFileSystemStorage;

    @InjectMocks
    private JsonMovimientosController jsonMovimientosController;

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
        String expectedFilename = "admin_movimientos_2024-12-03.json";

        when(jsonMovimientosFileSystemStorage.storeAll()).thenReturn(expectedFilename);

        mockMvc.perform(post("/storage/jsonMovimientos/generate"))
                .andExpect(status().isOk())
                .andExpect(content().string("Archivo JSON de movimientos generado con éxito: " + expectedFilename));
    }

    @Test
    void generateMovimientoJson() throws Exception {
        String guid = "54321";
        String expectedFilename = "movimientos_54321_2024-12-03.json";

        when(jsonMovimientosFileSystemStorage.store(guid)).thenReturn(expectedFilename);

        mockMvc.perform(post("/storage/jsonMovimientos/generate/{guid}", guid))
                .andExpect(status().isOk())
                .andExpect(content().string("Archivo JSON de movimientos de cliente generado con éxito: " + expectedFilename));

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
}