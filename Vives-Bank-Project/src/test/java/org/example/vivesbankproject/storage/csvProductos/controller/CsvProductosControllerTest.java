package org.example.vivesbankproject.storage.csvProductos.controller;

import org.example.vivesbankproject.cuenta.models.TipoCuenta;
import org.example.vivesbankproject.storage.csvProductos.services.CsvProductosStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CsvProductosControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CsvProductosStorageService csvProductosStorageService;

    @InjectMocks
    private CsvProductosController csvProductosController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(csvProductosController).build();
    }

    @Test
    void testImportTiposCuentaCsv_Success() throws Exception {
        // Prepare test data
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.csv",
                MediaType.TEXT_PLAIN_VALUE,
                "Cuenta Corriente,3.5".getBytes()
        );

        // Prepare mocked service response
        List<TipoCuenta> mockTiposCuenta = List.of(
                createMockTipoCuenta("Cuenta Corriente", new BigDecimal("3.5"))
        );

        when(csvProductosStorageService.storeImportedCsv(any())).thenReturn("tipos_cuenta_stored.csv");
        when(csvProductosStorageService.importTiposCuentaFromCsv(any())).thenReturn(mockTiposCuenta);

        mockMvc.perform(multipart("/storage/csvProductos/import")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Importación exitosa. 1 tipos de cuenta importados."))
                .andExpect(jsonPath("$.storedFilename").value("tipos_cuenta_stored.csv"))
                .andExpect(jsonPath("$.importedTiposCuenta[0].nombre").value("Cuenta Corriente"));

        verify(csvProductosStorageService).storeImportedCsv(any());
        verify(csvProductosStorageService).importTiposCuentaFromCsv(any());
    }

    @Test
    void testImportTiposCuentaCsv_EmptyFile() throws Exception {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.csv",
                MediaType.TEXT_PLAIN_VALUE,
                "".getBytes()
        );

        mockMvc.perform(multipart("/storage/csvProductos/import")
                        .file(emptyFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("El archivo CSV está vacío"));
    }

    @Test
    void testImportTiposCuentaCsv_InvalidFileType() throws Exception {
        MockMultipartFile notCsvFile = new MockMultipartFile(
                "file",
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Some content".getBytes()
        );

        mockMvc.perform(multipart("/storage/csvProductos/import")
                        .file(notCsvFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Por favor, suba un archivo CSV válido"));
    }

    @Test
    void testImportTiposCuentaCsv_InternalServerError() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.csv",
                MediaType.TEXT_PLAIN_VALUE,
                "Cuenta Corriente,3.5".getBytes()
        );

        when(csvProductosStorageService.storeImportedCsv(any())).thenThrow(new RuntimeException("Storage error"));

        mockMvc.perform(multipart("/storage/csvProductos/import")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isInternalServerError());
    }

    // Utility method to create mock TipoCuenta
    private TipoCuenta createMockTipoCuenta(String nombre, BigDecimal interes) {
        TipoCuenta tipoCuenta = new TipoCuenta();
        tipoCuenta.setNombre(nombre);
        tipoCuenta.setInteres(interes);
        return tipoCuenta;
    }
}