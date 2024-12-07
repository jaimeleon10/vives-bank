package org.example.vivesbankproject.storage.csvProductos.services;

import org.example.vivesbankproject.cuenta.dto.tipoCuenta.TipoCuentaRequest;
import org.example.vivesbankproject.cuenta.mappers.TipoCuentaMapper;
import org.example.vivesbankproject.cuenta.models.TipoCuenta;
import org.example.vivesbankproject.cuenta.repositories.TipoCuentaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CsvProductosStorageServiceImplTest {

    @Mock
    private TipoCuentaRepository tipoCuentaRepository;

    @Mock
    private TipoCuentaMapper tipoCuentaMapper;

    private CsvProductosStorageServiceImpl csvProductosStorageService;

    private Path tempDirectory;

    @BeforeEach
    void setUp() throws IOException {
        tempDirectory = Files.createTempDirectory("test-storage");
        csvProductosStorageService = new CsvProductosStorageServiceImpl(tempDirectory.toString(), tipoCuentaRepository, tipoCuentaMapper);
    }

    @Test
    void testConvertToTipoCuentaRequest() {
        String[] data = {"Cuenta Corriente", "3.5"};
        TipoCuentaRequest request = csvProductosStorageService.convertToTipoCuentaRequest(data);

        assertNotNull(request);
        assertEquals("Cuenta Corriente", request.getNombre());
        assertEquals(new BigDecimal("3.5"), request.getInteres());
    }

    @Test
    void testStoreImportedCsv() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.csv", "text/csv", "content".getBytes()
        );

        String storedFilename = csvProductosStorageService.storeImportedCsv(file);

        assertNotNull(storedFilename);
        assertTrue(storedFilename.startsWith("tipos_cuenta_"));
        assertTrue(storedFilename.endsWith(".csv"));
        assertTrue(Files.exists(tempDirectory.resolve(storedFilename)));
    }

    @Test
    void testConvertToTipoCuentaRequest_InvalidData() {
        assertThrows(IllegalArgumentException.class, () -> csvProductosStorageService.convertToTipoCuentaRequest(null));
        assertThrows(IllegalArgumentException.class, () -> csvProductosStorageService.convertToTipoCuentaRequest(new String[]{}));
        assertThrows(IllegalArgumentException.class, () -> csvProductosStorageService.convertToTipoCuentaRequest(new String[]{"Cuenta Corriente"}));
    }

    @Test
    void testDeleteExistingFile() throws IOException {
        String filename = "test_delete.csv";
        Files.createFile(tempDirectory.resolve(filename));

        csvProductosStorageService.delete(filename);

        assertFalse(Files.exists(tempDirectory.resolve(filename)));
    }

    @Test
    void testDeleteNonExistingFile() {
        assertDoesNotThrow(() -> csvProductosStorageService.delete("non_existing_file.csv"));
    }

    @Test
    void testDeleteThrowsIOException() {
        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.deleteIfExists(any(Path.class))).thenThrow(IOException.class);

            CsvProductosStorageServiceImpl service = new CsvProductosStorageServiceImpl(tempDirectory.toString(), tipoCuentaRepository, tipoCuentaMapper);

            assertThrows(RuntimeException.class, () -> service.delete("test.csv"));
        }
    }

    @Test
    void testImportTiposCuentaFromCsv_ValidFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                "Nombre,Interés\nCuenta Corriente,3.5\nCuenta Ahorro,1.2".getBytes()
        );

        when(tipoCuentaRepository.findByNombre(any())).thenReturn(Optional.empty());
        when(tipoCuentaMapper.toTipoCuenta(any())).thenAnswer(invocation -> {
            String nombre = invocation.getArgument(0);
            TipoCuenta tipoCuenta = new TipoCuenta();
            tipoCuenta.setNombre(nombre);
            tipoCuenta.setInteres(new BigDecimal("3.5"));
            return tipoCuenta;
        });
        when(tipoCuentaRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        var tiposCuenta = csvProductosStorageService.importTiposCuentaFromCsv(file);

        assertNotNull(tiposCuenta);
        assertEquals(2, tiposCuenta.size());
        verify(tipoCuentaRepository, times(2)).findByNombre(anyString());
        verify(tipoCuentaRepository, times(1)).saveAll(anyList());
    }


    @Test
    void testImportTiposCuentaFromCsv_InvalidFile() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                "Invalid content".getBytes()
        );

        assertThrows(RuntimeException.class, () -> csvProductosStorageService.importTiposCuentaFromCsv(file));
    }

    @Test
    void testInitCreatesDirectory() {
        csvProductosStorageService.init();

        assertTrue(Files.exists(tempDirectory));
    }

    @Test
    void testInitThrowsException() {
        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.createDirectories(Paths.get(tempDirectory.toString())))
                    .thenThrow(IOException.class);

            CsvProductosStorageServiceImpl serviceWithError = new CsvProductosStorageServiceImpl(tempDirectory.toString(), tipoCuentaRepository, tipoCuentaMapper);

            assertThrows(RuntimeException.class, serviceWithError::init);
        }
    }
}
