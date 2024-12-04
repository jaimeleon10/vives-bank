package org.example.vivesbankproject.storage.pdfMovimientos.controller;

import org.example.vivesbankproject.storage.exceptions.StorageInternal;
import org.example.vivesbankproject.storage.pdfMovimientos.services.PdfMovimientosStorageService;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin", password = "adminPassword123", roles = {"ADMIN", "USER"})
class PdfMovimientosControllerTest {

    private static final Path TEST_DIRECTORY = Paths.get("data", "test");

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PdfMovimientosStorageService storageService;

    @InjectMocks
    private PdfMovimientosController pdfMovimientosController;

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
    void generateMovimientosPdf() throws Exception {
        String filename = "movimientos.pdf";
        when(storageService.storeAll()).thenReturn(filename);

        mockMvc.perform(post("/storage/pdfMovimientos/generate"))
                .andExpect(status().isOk())
                .andExpect(content().string("Archivo PDF de movimientos generado con éxito: " + filename));
    }

    @Test
    void generateMovimientosPdfError() throws Exception {
        when(storageService.storeAll()).thenThrow(new StorageInternal("Error generating PDF"));

        mockMvc.perform(post("/storage/pdfMovimientos/generate"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error al generar el archivo PDF de movimientos."));
    }

    @Test
    void generateMovimientoPdf() throws Exception {
        String guid = "12345";
        String filename = "movimiento_12345.pdf";
        when(storageService.store(guid)).thenReturn(filename);

        mockMvc.perform(post("/storage/pdfMovimientos/generate/{guid}", guid))
                .andExpect(status().isOk())
                .andExpect(content().string("Archivo PDF de movimientos de cliente generado con éxito: " + filename));
    }

    @Test
    void generateMovimientoPdfError() throws Exception {
        when(storageService.store("guid123")).thenThrow(new StorageInternal("Error generating PDF"));

        mockMvc.perform(post("/storage/pdfMovimientos/generate/{guid}", "guid123"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error al generar el archivo PDF de movimientos de cliente."));
    }

    @Test
    void serveFile() throws Exception {
        String filename = "movimiento.pdf";
        File testFile = TEST_DIRECTORY.resolve(filename).toFile();

        if (!testFile.exists()) {
            testFile.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(testFile)) {
                writer.write("Contenido de prueba del archivo PDF");
            }
        }

        Resource fileResource = new FileSystemResource(testFile);
        when(storageService.loadAsResource(filename)).thenReturn(fileResource);

        mockMvc.perform(get("/storage/pdfMovimientos/{filename}", filename))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));

        if (testFile.exists()) {
            testFile.delete();
        }
    }

    @Test
    void listAllFiles() throws Exception {
        when(storageService.loadAll()).thenReturn(Stream.of(Paths.get("file1.pdf"), Paths.get("file2.pdf"), Paths.get("file3.pdf")));

        mockMvc.perform(get("/storage/pdfMovimientos/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").value("file1.pdf"))
                .andExpect(jsonPath("$[1]").value("file2.pdf"))
                .andExpect(jsonPath("$[2]").value("file3.pdf"));
    }

    @Test
    void listAllFilesError() throws Exception {
        when(storageService.loadAll()).thenThrow(new StorageInternal("Error al obtener archivos"));

        mockMvc.perform(get("/storage/pdfMovimientos/list"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json("[]"))
                .andExpect(content().string(containsString("[]")));
    }
}