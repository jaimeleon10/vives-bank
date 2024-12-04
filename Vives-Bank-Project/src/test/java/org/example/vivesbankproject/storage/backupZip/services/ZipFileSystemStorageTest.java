package org.example.vivesbankproject.storage.backupZip.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.vivesbankproject.cliente.repositories.ClienteRepository;
import org.example.vivesbankproject.cuenta.repositories.CuentaRepository;
import org.example.vivesbankproject.movimientos.repositories.MovimientosRepository;
import org.example.vivesbankproject.storage.exceptions.StorageNotFound;
import org.example.vivesbankproject.tarjeta.repositories.TarjetaRepository;
import org.example.vivesbankproject.users.repositories.UserRepository;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class ZipFileSystemStorageTest {

    private static final String TEST_ROOT_LOCATION = "data/test";

    @MockBean
    private ZipFileSystemStorage zipFileSystemStorage;

    @MockBean
    private ClienteRepository clienteRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private TarjetaRepository tarjetaRepository;

    @MockBean
    private CuentaRepository cuentaRepository;

    @MockBean
    private MovimientosRepository movimientosRepository;

    @BeforeEach
    void setUp() throws IOException {
        zipFileSystemStorage = new ZipFileSystemStorage(TEST_ROOT_LOCATION, clienteRepository, movimientosRepository, userRepository, tarjetaRepository, cuentaRepository);

        Path testPath = Paths.get(TEST_ROOT_LOCATION);
        if (Files.exists(testPath)) {
            Files.walkFileTree(testPath, new SimpleFileVisitor<>() {
                @NotNull
                @Override
                public FileVisitResult visitFile(Path file, @NotNull BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @NotNull
                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        }

        zipFileSystemStorage.init();
    }

    @AfterEach
    void tearDown() throws IOException {
        Path testPath = Paths.get(TEST_ROOT_LOCATION);
        if (Files.exists(testPath)) {
            Files.walkFileTree(testPath, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }

    @Test
    void init() throws IOException {
        Path testDirectory = Paths.get(TEST_ROOT_LOCATION);

        if (Files.exists(testDirectory)) {
            Files.walk(testDirectory)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            fail("No se pudo limpiar el directorio de prueba");
                        }
                    });
        }

        zipFileSystemStorage.init();

        assertTrue(Files.exists(testDirectory), "El directorio no fue creado");
        assertTrue(Files.isDirectory(testDirectory), "La ruta no es un directorio");

        Files.walk(testDirectory)
                .sorted(Comparator.reverseOrder())
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        fail("No se pudo eliminar el directorio de prueba");
                    }
                });
    }

    @Test
    void export() {
    }

    @Test
    void loadFromZip() {
    }

    @Test
    void loadJson() throws IOException {
        String jsonContent = "[{\"name\":\"John Doe\"}, {\"name\":\"Jane Doe\"}]";

        File tempFile = File.createTempFile("test", ".json");
        Files.write(tempFile.toPath(), jsonContent.getBytes());

        try {
            List<Object> result = zipFileSystemStorage.loadJson(tempFile);

            assertNotNull(result, "La lista deserializada no debería ser nula");
            assertEquals(2, result.size(), "La lista deserializada debería tener 2 elementos");

        } finally {
            tempFile.delete();
        }
    }

//    @Test
//    void loadJsonStorageNotFound() throws IOException {
//        File mockJsonFile = mock(File.class);
//
//        ObjectMapper objectMapper = mock(ObjectMapper.class);
//        when(objectMapper.readValue(mockJsonFile, List.class)).thenThrow(new IOException("Error al leer el archivo JSON"));
//
//        StorageNotFound exception = assertThrows(StorageNotFound.class, () -> {
//            zipFileSystemStorage.loadJson(mockJsonFile);
//        });
//
//        assertTrue(exception.getMessage().contains("Error al deserializar el archivo JSON"));
//    }

    @Test
    void load() {
        String filename = "test.zip";

        Path expectedPath = Paths.get(TEST_ROOT_LOCATION).resolve(filename);
        Path actualPath = zipFileSystemStorage.load(filename);

        assertNotNull(actualPath);
        assertEquals(expectedPath, actualPath, "La ruta resuelta no es la esperada");
    }

    @Test
    void loadAsResource() throws Exception {
        String filename = "test.zip";
        Path filePath = Paths.get(TEST_ROOT_LOCATION).resolve(filename);

        Files.createDirectories(filePath.getParent());
        Files.write(filePath, "Contenido de prueba".getBytes());

        Resource resource = zipFileSystemStorage.loadAsResource(filename);

        assertNotNull(resource, "El recurso no debería ser nulo");
        assertTrue(resource.exists(), "El recurso debería existir");
        assertTrue(resource.isReadable(), "El recurso debería ser legible");
    }

    @Test
    void loadAsResourceFileNotExist() {
        String filename = "archivoInexistente.zip";

        StorageNotFound exception = assertThrows(StorageNotFound.class, () -> {
            zipFileSystemStorage.loadAsResource(filename);
        });

        assertTrue(exception.getMessage().contains("No se puede leer fichero ZIP: " + filename));
    }


    @Test
    void loadAsResourceMalformedUrl() {
        String filename = "../../etc/passwd";

        StorageNotFound exception = assertThrows(StorageNotFound.class, () -> {
            zipFileSystemStorage.loadAsResource(filename);
        });

        assertTrue(exception.getMessage().contains("No se puede leer fichero ZIP: " + filename));
    }

    @Test
    void delete() throws IOException {
        String filename = "testFileToDelete.zip";
        Path filePath = Paths.get(TEST_ROOT_LOCATION).resolve(filename);

        Files.createDirectories(Paths.get(TEST_ROOT_LOCATION));
        Files.write(filePath, "Contenido de prueba".getBytes());

        assertTrue(Files.exists(filePath), "El archivo debería existir antes de eliminarlo");

        zipFileSystemStorage.delete(filename);

        assertFalse(Files.exists(filePath), "El archivo debería haber sido eliminado");
    }
}