package org.example.vivesbankproject.storage.csvProductos.controller;

import org.example.vivesbankproject.storage.csvProductos.services.CsvStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CsvStorageController.class)
class CsvStorageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CsvStorageService csvStorageService;

    private MockMultipartFile validCsvFile;
    private MockMultipartFile invalidFile;

    @BeforeEach
    void setUp() {
        validCsvFile = new MockMultipartFile(
                "file",
                "test.csv",
                MediaType.TEXT_PLAIN_VALUE,
                "nombre,interes\nCuenta A,1.5\nCuenta B,2.0".getBytes()
        );

        invalidFile = new MockMultipartFile(
                "file",
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "nombre,interes\nCuenta A,1.5".getBytes()
        );
    }

    @Test
    void importCsv_validFile_shouldReturnOk() throws Exception {
        // Act & Assert
        mockMvc.perform(multipart("/storage/csvProductos/import").file(validCsvFile))
                .andExpect(status().isOk())
                .andExpect(content().string("Archivo CSV importado exitosamente."));

        verify(csvStorageService).importCsv(validCsvFile);
    }

    @Test
    void importCsv_invalidFileExtension_shouldReturnBadRequest() throws Exception {
        // Arrange
        doThrow(new IllegalArgumentException("El archivo debe tener formato CSV."))
                .when(csvStorageService).importCsv(invalidFile);

        // Act & Assert
        mockMvc.perform(multipart("/storage/csvProductos/import").file(invalidFile))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error en el archivo CSV: El archivo debe tener formato CSV."));

        verify(csvStorageService).importCsv(invalidFile);
    }

    @Test
    void importCsv_ioException_shouldReturnInternalServerError() throws Exception {
        // Arrange
        doThrow(new IOException("Error al leer el archivo."))
                .when(csvStorageService).importCsv(validCsvFile);

        // Act & Assert
        mockMvc.perform(multipart("/storage/csvProductos/import").file(validCsvFile))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error al procesar el archivo: Error al leer el archivo."));

        verify(csvStorageService).importCsv(validCsvFile);
    }
}
