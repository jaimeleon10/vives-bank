package org.example.vivesbankproject.storage.csvProductos.services;

import org.example.vivesbankproject.cuenta.models.TipoCuenta;
import org.example.vivesbankproject.cuenta.repositories.TipoCuentaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class CsvProductosStorageServiceImplTest {

    @Mock
    private TipoCuentaRepository tipoCuentaRepository;

    @InjectMocks
    private CsvProductosStorageService csvProductosStorageService;

    private Path tempDirectory;

    @BeforeEach
    void setUp() throws IOException {
        // Crear un directorio temporal para pruebas
        tempDirectory = Files.createTempDirectory("test-storage");
        ReflectionTestUtils.setField(csvProductosStorageService, "rootLocation", tempDirectory);
    }

    @Test
    void testImportTiposCuentaFromCsv() throws IOException {
        // Preparar archivo CSV de prueba
        String csvContent = "nombre,interes\n" +
                "Cuenta Corriente,0.50\n" +
                "Cuenta Ahorro,2.75\n";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "tipos_cuenta.csv",
                "text/csv",
                csvContent.getBytes()
        );

        // Configurar el mock para guardar los tipos de cuenta
        when(tipoCuentaRepository.saveAll(anyList()))
                .thenAnswer(invocation -> {
                    List<TipoCuenta> tiposCuenta = invocation.getArgument(0);
                    return tiposCuenta;
                });

        // Ejecutar el método de importación
        List<TipoCuenta> importedTiposCuenta = csvProductosStorageService.importTiposCuentaFromCsv(file);

        // Verificaciones
        assertThat(importedTiposCuenta).hasSize(2);

        // Verificar los detalles del primer tipo de cuenta
        TipoCuenta primerTipoCuenta = importedTiposCuenta.get(0);
        assertEquals("Cuenta Corriente", primerTipoCuenta.getNombre());
        assertEquals(new BigDecimal("0.50"), primerTipoCuenta.getInteres());

        // Verificar los detalles del segundo tipo de cuenta
        TipoCuenta segundoTipoCuenta = importedTiposCuenta.get(1);
        assertEquals("Cuenta Ahorro", segundoTipoCuenta.getNombre());
        assertEquals(new BigDecimal("2.75"), segundoTipoCuenta.getInteres());

        // Verificar que se llamó al repositorio para guardar
        verify(tipoCuentaRepository).saveAll(anyList());
    }

    @Test
    void testStoreImportedCsv() throws IOException {
        // Preparar archivo CSV de prueba
        String csvContent = "nombre,interes\n" +
                "Cuenta Corriente,0.50\n";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "tipos_cuenta.csv",
                "text/csv",
                csvContent.getBytes()
        );

        // Ejecutar el método de almacenamiento
        String storedFilename = csvProductosStorageService.storeImportedCsv(file);

        // Verificaciones
        assertNotNull(storedFilename);
        assertTrue(storedFilename.startsWith("tipos_cuenta_"));
        assertTrue(storedFilename.endsWith(".csv"));

        // Verificar que el archivo se ha guardado correctamente
        Path storedFilePath = tempDirectory.resolve(storedFilename);
        assertTrue(Files.exists(storedFilePath));

        // Verificar el contenido del archivo
        String storedContent = new String(Files.readAllBytes(storedFilePath));
        assertEquals(csvContent, storedContent);
    }

    @Test
    void testInit() {
        // Método de inicialización
        assertDoesNotThrow(() -> csvProductosStorageService.init());

        // Verificar que el directorio existe después de la inicialización
        assertTrue(Files.exists(tempDirectory));
    }
}