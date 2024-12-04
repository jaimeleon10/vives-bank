package org.example.vivesbankproject.storage.jsonClientes.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import org.example.vivesbankproject.storage.exceptions.StorageInternal;
import org.example.vivesbankproject.storage.exceptions.StorageNotFound;
import org.example.vivesbankproject.storage.jsonClientes.services.JsonClientesStorageService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin", password = "adminPassword123", roles = {"ADMIN", "USER"})
class JsonClientesControllerTest {

    private static final Path TEST_DIRECTORY = Paths.get("data", "test");

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JsonClientesStorageService jsonClientesStorageService;

    @InjectMocks
    private JsonClientesController jsonClientesController;

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
    void generateClienteJson() throws Exception {
        String guid = "test-guid";
        String expectedFilename = "clientes_" + guid + "_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".json";
        Path expectedFilePath = TEST_DIRECTORY.resolve(expectedFilename);

        when(jsonClientesStorageService.store(guid)).thenReturn(expectedFilename);

        mockMvc.perform(post("/storage/jsonClientes/generate/{guid}", guid))
                .andExpect(status().isOk())
                .andExpect(content().string("Archivo JSON de clientes generado con éxito: " + expectedFilename));

        verify(jsonClientesStorageService, times(1)).store(guid);
    }

    @Test
    void generateClienteJsonStorageInternalError() throws Exception {
        String guid = "test-guid";

        when(jsonClientesStorageService.store(guid)).thenThrow(new StorageInternal("Error interno al generar el archivo"));

        mockMvc.perform(post("/storage/jsonClientes/generate/{guid}", guid))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error al generar el archivo JSON de clientes."));

        verify(jsonClientesStorageService, times(1)).store(guid);
    }

    @Test
    void serveFile() throws Exception {
        String filename = "clientes_test-guid_2024-12-01.json";

        byte[] fileContent = "Contenido de prueba".getBytes();
        Resource mockResource = new org.springframework.core.io.ByteArrayResource(fileContent);

        when(jsonClientesStorageService.loadAsResource(filename)).thenReturn(mockResource);

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        ServletContext mockServletContext = mock(ServletContext.class);

        when(mockRequest.getServletContext()).thenReturn(mockServletContext);
        when(mockServletContext.getMimeType(anyString())).thenReturn("application/json");

        mockMvc.perform(get("/storage/jsonClientes/{filename}", filename)
                        .requestAttr("javax.servlet.request", mockRequest))
                .andExpect(status().isOk())
                .andExpect(content().bytes(fileContent))
                .andExpect(content().contentType("application/octet-stream"));

        verify(jsonClientesStorageService, times(1)).loadAsResource(filename);
    }

    @Test
    void serveFileFileNotFound() throws Exception {
        String filename = "ficheroinexistente.json";
        when(jsonClientesStorageService.loadAsResource(filename)).thenThrow(new StorageNotFound("File not found"));

        mockMvc.perform(get("/storage/jsonClientes/{filename}", filename))
                .andExpect(status().isNotFound());
    }

    @Test
    void listAllFiles() throws Exception {
        List<Path> mockFiles = new ArrayList<>();
        mockFiles.add(TEST_DIRECTORY.resolve("clientes_test-guid_2024-12-01.json"));
        mockFiles.add(TEST_DIRECTORY.resolve("clientes_test-guid_2024-12-02.json"));

        when(jsonClientesStorageService.loadAll()).thenReturn(mockFiles.stream());

        mockMvc.perform(get("/storage/jsonClientes/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").value("data\\test\\clientes_test-guid_2024-12-01.json"))
                .andExpect(jsonPath("$[1]").value("data\\test\\clientes_test-guid_2024-12-02.json"));
    }

    @Test
    void listAllFiles_storageInternalError() throws Exception {
        when(jsonClientesStorageService.loadAll()).thenThrow(new StorageInternal("Error interno al obtener los archivos"));

        mockMvc.perform(get("/storage/jsonClientes/list"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json("[]"));

        verify(jsonClientesStorageService, times(1)).loadAll();
    }
}