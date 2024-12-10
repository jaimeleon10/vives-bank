package org.example.vivesbankproject.storage.pdfMovimientos.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import org.example.vivesbankproject.rest.storage.backupZip.services.ZipStorageService;
import org.example.vivesbankproject.rest.storage.exceptions.StorageInternal;
import org.example.vivesbankproject.rest.storage.pdfMovimientos.controller.PdfMovimientosController;
import org.example.vivesbankproject.rest.storage.pdfMovimientos.services.PdfMovimientosStorageService;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.bson.assertions.Assertions.assertNotNull;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Mock
    private Resource mockResource;
    @Mock
    private HttpServletRequest request;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PdfMovimientosStorageService storageService;

    @InjectMocks
    private PdfMovimientosController pdfMovimientosController;

    @Autowired
    private PdfMovimientosControllerTest(PdfMovimientosStorageService storageService){
        this.storageService = storageService;
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
    void serveFile_HandleIOException() throws Exception {
        String testFilename = "test.pdf";
        Resource mockResource = mock(Resource.class);
        when(storageService.loadAsResource(testFilename)).thenReturn(mockResource);

        when(mockResource.getFile()).thenThrow(new IOException("Test IOException"));

        ServletContext servletContext = mock(ServletContext.class);
        when(request.getServletContext()).thenReturn(servletContext);

        ResponseEntity<Resource> response = pdfMovimientosController.serveFile(testFilename, request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("application/pdf", response.getHeaders().getContentType().toString());
        assertEquals(mockResource, response.getBody());
    }

    @Test
    void serveFileContenidoNull() throws Exception {
        String testFilename = "test.zip";
        when(storageService.loadAsResource(testFilename)).thenReturn(mockResource);

        when(mockResource.getFile()).thenReturn(new File("dummy-path"));

        when(request.getServletContext()).thenReturn(mock(ServletContext.class));
        when(request.getServletContext().getMimeType(anyString())).thenReturn(null);

        ResponseEntity<Resource> response = pdfMovimientosController.serveFile(testFilename, request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("application/pdf", response.getHeaders().getContentType().toString());
        assertEquals(mockResource, response.getBody());
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