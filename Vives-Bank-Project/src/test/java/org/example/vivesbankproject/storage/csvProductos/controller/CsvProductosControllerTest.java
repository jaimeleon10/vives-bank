package org.example.vivesbankproject.storage.csvProductos.controller;

import org.example.vivesbankproject.cuenta.models.TipoCuenta;
import org.example.vivesbankproject.storage.csvProductos.services.CsvProductosStorageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CsvProductosController.class)
class CsvProductosControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CsvProductosStorageService csvProductosStorageService;

    @Test
    void testImportTiposCuentaCsv_Success() throws Exception {
        // Preparar datos de prueba
        List<TipoCuenta> tiposCuenta = Arrays.asList(
                TipoCuenta.builder()
                        .nombre("Cuenta Corriente")
                        .interes(new BigDecimal("0.50"))
                        .build(),
                TipoCuenta.builder()
                        .nombre("Cuenta Ahorro")
                        .interes(new BigDecimal("2.75"))
                        .build()
        );

        // Configurar mocks
        when(csvProductosStorageService.storeImportedCsv(any())).thenReturn("tipos_cuenta_test.csv");
        when(csvProductosStorageService.importTiposCuentaFromCsv(any())).thenReturn(tiposCuenta);

        // Preparar archivo CSV de prueba
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "tipos_cuenta.csv",
                "text/csv",
                "nombre,interes\nCuenta Corriente,0.50\nCuenta Ahorro,2.75".getBytes()
        );

        // Realizar la solicitud y verificar la respuesta
        mockMvc.perform(multipart("/storage/csvProductos/import")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        "Importación exitosa. 2 tipos de cuenta importados. Archivo almacenado: tipos_cuenta_test.csv"
                ));

        // Verificar que se llamaron los métodos del servicio
        verify(csvProductosStorageService).storeImportedCsv(any());
        verify(csvProductosStorageService).importTiposCuentaFromCsv(any());
    }

    @Test
    void testImportTiposCuentaCsv_EmptyFile() throws Exception {
        // Preparar archivo CSV vacío
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.csv",
                "text/csv",
                "".getBytes()
        );

        // Realizar la solicitud y verificar la respuesta
        mockMvc.perform(multipart("/storage/csvProductos/import")
                        .file(emptyFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("El archivo CSV está vacío"));

        // Verificar que no se llamaron los métodos del servicio
        verify(csvProductosStorageService, never()).storeImportedCsv(any());
        verify(csvProductosStorageService, never()).importTiposCuentaFromCsv(any());
    }

    @Test
    void testImportTiposCuentaCsv_InvalidFileType() throws Exception {
        // Preparar archivo que no es CSV
        MockMultipartFile invalidFile = new MockMultipartFile(
                "file",
                "tipos_cuenta.txt",
                "text/plain",
                "Some content".getBytes()
        );

        // Realizar la solicitud y verificar la respuesta
        mockMvc.perform(multipart("/storage/csvProductos/import")
                        .file(invalidFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Por favor, suba un archivo CSV válido"));

        // Verificar que no se llamaron los métodos del servicio
        verify(csvProductosStorageService, never()).storeImportedCsv(any());
        verify(csvProductosStorageService, never()).importTiposCuentaFromCsv(any());
    }
}