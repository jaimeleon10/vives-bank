package org.example.vivesbankproject.storage.csvProductos.services;

import org.example.vivesbankproject.cuenta.dto.tipoCuenta.TipoCuentaRequest;
import org.example.vivesbankproject.cuenta.exceptions.tipoCuenta.TipoCuentaExists;
import org.example.vivesbankproject.cuenta.services.TipoCuentaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CsvStorageServiceImplTest {

    @Mock
    private TipoCuentaService tipoCuentaService;

    @InjectMocks
    private CsvStorageServiceImpl csvStorageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void importCsv_validFile_shouldImportSuccessfully() throws IOException {
        // Arrange
        String csvContent = "nombre,interes\nCuenta A,1.5\nCuenta B,2.0";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                csvContent.getBytes()
        );

        // Act
        csvStorageService.importCsv(file);

        // Assert
        verify(tipoCuentaService, times(1)).save(
                TipoCuentaRequest.builder().nombre("Cuenta A").interes(BigDecimal.valueOf(1.5)).build()
        );
        verify(tipoCuentaService, times(1)).save(
                TipoCuentaRequest.builder().nombre("Cuenta B").interes(BigDecimal.valueOf(2.0)).build()
        );
    }

    @Test
    void importCsv_fileWithInvalidExtension_shouldThrowIllegalArgumentException() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "nombre,interes\nCuenta A,1.5".getBytes()
        );

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> csvStorageService.importCsv(file));
        assertEquals("El archivo debe tener formato CSV.", exception.getMessage());
        verifyNoInteractions(tipoCuentaService);
    }

    @Test
    void importCsv_invalidRowFormat_shouldSkipInvalidLines() throws IOException {
        // Arrange
        String csvContent = "nombre,interes\nCuenta A,1.5\nInvalidLine\nCuenta B,2.0";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                csvContent.getBytes()
        );

        // Act
        csvStorageService.importCsv(file);

        // Assert
        verify(tipoCuentaService, times(1)).save(
                TipoCuentaRequest.builder().nombre("Cuenta A").interes(BigDecimal.valueOf(1.5)).build()
        );
        verify(tipoCuentaService, times(1)).save(
                TipoCuentaRequest.builder().nombre("Cuenta B").interes(BigDecimal.valueOf(2.0)).build()
        );
        verifyNoMoreInteractions(tipoCuentaService);
    }

    @Test
    void importCsv_tipoCuentaAlreadyExists_shouldLogWarning() throws IOException {
        // Arrange
        String csvContent = "nombre,interes\nCuenta A,1.5";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                csvContent.getBytes()
        );

        doThrow(new TipoCuentaExists("El tipo de cuenta ya existe."))
                .when(tipoCuentaService)
                .save(any(TipoCuentaRequest.class));

        // Act
        csvStorageService.importCsv(file);

        // Assert
        verify(tipoCuentaService, times(1)).save(any(TipoCuentaRequest.class));
    }

    @Test
    void importCsv_invalidNumberFormat_shouldLogError() throws IOException {
        // Arrange
        String csvContent = "nombre,interes\nCuenta A,abc";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                csvContent.getBytes()
        );

        // Act
        csvStorageService.importCsv(file);

        // Assert
        verifyNoInteractions(tipoCuentaService);
    }

    @Test
    void importCsv_ioException_shouldThrowIOException() throws IOException {
        // Arrange
        MockMultipartFile file = mock(MockMultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("test.csv");
        when(file.getInputStream()).thenThrow(new IOException("Simulated IO error"));

        // Act & Assert
        Exception exception = assertThrows(IOException.class, () -> csvStorageService.importCsv(file));
        assertEquals("Error al leer el archivo CSV.", exception.getMessage());
        verifyNoInteractions(tipoCuentaService);
    }
}
