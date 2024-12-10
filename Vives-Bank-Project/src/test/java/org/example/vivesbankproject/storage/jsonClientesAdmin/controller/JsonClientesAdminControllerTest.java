package org.example.vivesbankproject.storage.jsonClientesAdmin.controller;

import org.example.vivesbankproject.rest.storage.exceptions.StorageInternal;
import org.example.vivesbankproject.rest.storage.exceptions.StorageNotFound;
import org.example.vivesbankproject.rest.storage.jsonClientesAdmin.controller.JsonClientesAdminController;
import org.example.vivesbankproject.rest.storage.jsonClientesAdmin.services.JsonClientesAdminFileSystemStorage;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin", password = "adminPassword123", roles = {"ADMIN", "USER"})
class JsonClientesAdminControllerTest {

    private static final Path TEST_DIRECTORY = Paths.get("data", "test");

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JsonClientesAdminFileSystemStorage jsonClientesAdminFileSystemStorage;

    @InjectMocks
    private JsonClientesAdminController jsonClientesAdminController;

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
        String expectedFilename = "clientes_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".json";
        Path expectedFilePath = TEST_DIRECTORY.resolve(expectedFilename);

        when(jsonClientesAdminFileSystemStorage.storeAll()).thenReturn(expectedFilename);

        mockMvc.perform(post("/storage/jsonClientesAdmin/generate"))
                .andExpect(status().isOk())
                .andExpect(content().string("Archivo JSON de clientes generado con éxito: " + expectedFilename));

        verify(jsonClientesAdminFileSystemStorage, times(1)).storeAll();
    }

    @Test
    void generateClienteJsonStorageInternalError() throws Exception {
        when(jsonClientesAdminFileSystemStorage.storeAll()).thenThrow(new StorageInternal("Error interno al guardar el archivo"));

        mockMvc.perform(post("/storage/jsonClientesAdmin/generate"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error al generar el archivo JSON de clientes."));

        verify(jsonClientesAdminFileSystemStorage, times(1)).storeAll();
    }

    @Test
    void serveFile() throws Exception {
        String filename = "clientes_test_2024-12-02.json";

        byte[] fileContent = "Contenido de prueba".getBytes();
        Resource mockResource = new org.springframework.core.io.ByteArrayResource(fileContent);

        when(jsonClientesAdminFileSystemStorage.loadAsResource(filename)).thenReturn(mockResource);

        String contentType = "application/octet-stream";

        mockMvc.perform(get("/storage/jsonClientesAdmin/{filename}", filename))
                .andExpect(status().isOk())
                .andExpect(content().bytes(fileContent))
                .andExpect(content().contentType(MediaType.parseMediaType(contentType)));
    }

    @Test
    void serveFileFileNotFound() throws Exception {
        String filename = "ficheroinexistente.json";
        when(jsonClientesAdminFileSystemStorage.loadAsResource(filename)).thenThrow(new StorageNotFound("File not found"));

        mockMvc.perform(get("/storage/jsonClientesAdmin/{filename}", filename))
                .andExpect(status().isNotFound());
    }

    @Test
    void serveFile_FileFound_TypeDetermined_Success() throws Exception {
        String filename = "testfile.json";
        Path path = Paths.get("data", "test", filename);
        Files.createDirectories(path.getParent());
        Files.createFile(path);

        Resource fileMock = mock(Resource.class);
        when(fileMock.getFile()).thenReturn(path.toFile());
        when(jsonClientesAdminFileSystemStorage.loadAsResource(filename)).thenReturn(fileMock);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("javax.servlet.context.tempdir", "/");

        mockMvc.perform(get("/storage/jsonClientesAdmin/" + filename).requestAttr("javax.servlet.context.tempdir", "/"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"));

        Files.deleteIfExists(path);
    }

    @Test
    void listAllFiles() throws Exception {
        List<Path> mockFiles = new ArrayList<>();
        mockFiles.add(TEST_DIRECTORY.resolve("clientes_test_2024-12-01.json"));
        mockFiles.add(TEST_DIRECTORY.resolve("clientes_test_2024-12-02.json"));

        when(jsonClientesAdminFileSystemStorage.loadAll()).thenReturn(mockFiles.stream());

        mockMvc.perform(get("/storage/jsonClientesAdmin/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").value("data\\test\\clientes_test_2024-12-01.json"))
                .andExpect(jsonPath("$[1]").value("data\\test\\clientes_test_2024-12-02.json"));
    }

    @Test
    void listAllFilesStorageInternalError() throws Exception {
        when(jsonClientesAdminFileSystemStorage.loadAll()).thenThrow(new StorageInternal("Error interno al cargar los archivos"));

        mockMvc.perform(get("/storage/jsonClientesAdmin/list"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json("[]"));

        verify(jsonClientesAdminFileSystemStorage, times(1)).loadAll();
    }
}