package org.example.vivesbankproject.storage.jsonClientes.controller;

import org.example.vivesbankproject.storage.exceptions.StorageNotFound;
import org.example.vivesbankproject.storage.jsonClientes.services.JsonClientesStorageService;
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

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JsonClientesStorageService jsonClientesStorageService;

    @InjectMocks
    private JsonClientesController jsonClientesController;

    @Test
    void generateClienteJson() throws Exception {
        String guid = "test-guid";
        String expectedFilename = "clientes_" + guid + "_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".json";

        when(jsonClientesStorageService.store(guid)).thenReturn(expectedFilename);

        mockMvc.perform(post("/storage/jsonClientes/generate/{guid}", guid))
                .andExpect(status().isOk())
                .andExpect(content().string("Archivo JSON de clientes generado con éxito: " + expectedFilename));

        verify(jsonClientesStorageService, times(1)).store(guid);
    }

    @Test
    void serveFile() throws Exception {
        String filename = "clientes_test-guid_2024-12-01.json";
        Path filePath = Paths.get("data/" + filename);
        byte[] fileContent = Files.readAllBytes(filePath);

        Resource mockResource = new org.springframework.core.io.ByteArrayResource(fileContent);

        when(jsonClientesStorageService.loadAsResource(filename)).thenReturn(mockResource);

        String contentType = "application/octet-stream";

        mockMvc.perform(get("/storage/jsonClientes/{filename}", filename))
                .andExpect(status().isOk())
                .andExpect(content().bytes(fileContent))
                .andExpect(content().contentType(MediaType.parseMediaType(contentType)));
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
        mockFiles.add(Paths.get("clientes_test-guid_2024-12-01.json"));
        mockFiles.add(Paths.get("clientes_test-guid_2024-12-02.json"));

        when(jsonClientesStorageService.loadAll()).thenReturn(mockFiles.stream());

        mockMvc.perform(get("/storage/jsonClientes/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").value("clientes_test-guid_2024-12-01.json"))
                .andExpect(jsonPath("$[1]").value("clientes_test-guid_2024-12-02.json"));
    }
}