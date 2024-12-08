package org.example.vivesbankproject.storage.jsonMovimientos.services;

import org.example.vivesbankproject.rest.movimientos.models.Movimiento;
import org.example.vivesbankproject.rest.movimientos.repositories.MovimientosRepository;
import org.example.vivesbankproject.rest.storage.exceptions.StorageInternal;
import org.example.vivesbankproject.rest.storage.exceptions.StorageNotFound;
import org.example.vivesbankproject.rest.storage.jsonMovimientos.services.JsonMovimientosFileSystemStorage;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class JsonMovimientosFileSystemStorageTest {

    private static final String TEST_ROOT_LOCATION = "data/test";

    private JsonMovimientosFileSystemStorage storageService;

    @MockBean
    private MovimientosRepository movimientosRepository;

    @BeforeEach
    void setUp() throws IOException {
        storageService = new JsonMovimientosFileSystemStorage(TEST_ROOT_LOCATION, movimientosRepository);

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

        storageService.init();
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
    void storeAll() {
        Movimiento movimiento = new Movimiento();
        movimiento.setGuid("12345");
        movimiento.setClienteGuid("54321");
        movimiento.setCreatedAt(LocalDateTime.now());
        movimiento.setIsDeleted(false);

        when(movimientosRepository.findAll()).thenReturn(List.of(movimiento));

        String filename = storageService.storeAll();

        assertNotNull(filename, "El nombre del archivo generado es nulo");

        File fileInDataAdmin = new File("dataAdmin", filename);
        assertTrue(fileInDataAdmin.exists(), "El archivo no se creó en el directorio esperado: " + fileInDataAdmin.getAbsolutePath());

        if (fileInDataAdmin.exists()) {
            boolean deleted = fileInDataAdmin.delete();
            assertTrue(deleted, "No se pudo eliminar el archivo generado: " + fileInDataAdmin.getAbsolutePath());
        }
    }

    @Test
    void storeAllEmptyList() throws IOException {
        when(movimientosRepository.findAll()).thenReturn(List.of());

        String filename = storageService.storeAll();

        assertNotNull(filename);
        assertTrue(Files.exists(Path.of("dataAdmin").resolve(filename)));

        String fileContent = Files.readString(Path.of("dataAdmin").resolve(filename));
        assertEquals("[ ]", fileContent);
    }

    @Test
    void store() throws IOException {
        String guid = "12345";
        Movimiento movimiento = new Movimiento();
        movimiento.setGuid("123");
        movimiento.setClienteGuid(guid);
        movimiento.setCreatedAt(LocalDateTime.now());
        movimiento.setIsDeleted(false);

        when(movimientosRepository.findByClienteGuid(guid)).thenReturn(Optional.of(movimiento));

        String filename = storageService.store(guid);

        assertNotNull(filename);
        assertTrue(Files.exists(Path.of(TEST_ROOT_LOCATION).resolve(filename)));

        String fileContent = Files.readString(Path.of(TEST_ROOT_LOCATION).resolve(filename));
        assertTrue(fileContent.contains("123"));
        assertTrue(fileContent.contains(guid));
    }

    @Test
    void storeClienteSinMovimientos() throws IOException {
        String guid = "12345";
        when(movimientosRepository.findByClienteGuid(guid)).thenReturn(Optional.empty());

        String filename = storageService.store(guid);

        assertNotNull(filename);
        assertTrue(Files.exists(Path.of(TEST_ROOT_LOCATION).resolve(filename)));

        String fileContent = Files.readString(Path.of(TEST_ROOT_LOCATION).resolve(filename));
        assertEquals("[ ]", fileContent);
    }

    @Test
    void storeClienteNotFound() throws IOException {
        String guid = "not-found";
        when(movimientosRepository.findByClienteGuid(guid)).thenReturn(Optional.empty());

        String filename = storageService.store(guid);

        assertNotNull(filename);
        assertTrue(Files.exists(Path.of(TEST_ROOT_LOCATION).resolve(filename)));

        String fileContent = Files.readString(Path.of(TEST_ROOT_LOCATION).resolve(filename));
        assertEquals("[ ]", fileContent);
    }

    @Test
    void testStore_IOException() {
        String guid = "12345";
        Movimiento movimiento = new Movimiento();
        movimiento.setGuid("123");
        movimiento.setClienteGuid(guid);
        movimiento.setCreatedAt(LocalDateTime.now());
        movimiento.setIsDeleted(false);

        when(movimientosRepository.findByClienteGuid(guid)).thenReturn(Optional.of(movimiento));

        String invalidPath = "/invalid";
        storageService = new JsonMovimientosFileSystemStorage(invalidPath, movimientosRepository);

        StorageInternal exception = assertThrows(StorageInternal.class, () -> storageService.store(guid));
        assertTrue(exception.getMessage().contains("Fallo al almacenar el archivo JSON de movimientos del cliente"));
    }

    @Test
    void testStore_ValidateFilename() {
        String guid = "12345";
        Movimiento movimiento = new Movimiento();
        movimiento.setGuid("123");
        movimiento.setClienteGuid(guid);
        movimiento.setCreatedAt(LocalDateTime.now());
        movimiento.setIsDeleted(false);

        when(movimientosRepository.findByClienteGuid(guid)).thenReturn(Optional.of(movimiento));

        String filename = storageService.store(guid);

        assertTrue(filename.matches("movimientos_" + guid + "_\\d{4}-\\d{2}-\\d{2}\\.json"));
    }

    @Test
    void loadAll() throws IOException {
        Path testFile1 = Paths.get(TEST_ROOT_LOCATION).resolve("testFile1.json");
        Path testFile2 = Paths.get(TEST_ROOT_LOCATION).resolve("testFile2.json");

        Files.createDirectories(Paths.get(TEST_ROOT_LOCATION));
        Files.write(testFile1, "Contenido de prueba 1".getBytes());
        Files.write(testFile2, "Contenido de prueba 2".getBytes());

        Stream<Path> loadedFiles = storageService.loadAll();

        List<Path> fileList = loadedFiles.collect(Collectors.toList());

        assertEquals(2, fileList.size());
        assertTrue(fileList.contains(Paths.get("testFile1.json")));
        assertTrue(fileList.contains(Paths.get("testFile2.json")));

        Files.deleteIfExists(testFile1);
        Files.deleteIfExists(testFile2);
    }

    @Test
    void load() {
        String filename = "testFile.json";
        Path expectedPath = Paths.get(TEST_ROOT_LOCATION).resolve(filename);

        Path resolvedPath = storageService.load(filename);

        assertNotNull(resolvedPath);
        assertEquals(expectedPath, resolvedPath);
    }

    @Test
    void loadAsResource() {
        String filename = "testFile.json";
        Path filePath = Paths.get(TEST_ROOT_LOCATION).resolve(filename);

        try {
            Files.createDirectories(Paths.get(TEST_ROOT_LOCATION));
            Files.write(filePath, "Contenido de prueba".getBytes());
        } catch (IOException e) {
            fail("No se pudo crear el archivo de prueba");
        }

        Resource resource = storageService.loadAsResource(filename);

        assertNotNull(resource);
        assertTrue(resource.exists());
        assertTrue(resource.isReadable());

        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            fail("No se pudo eliminar el archivo de prueba");
        }
    }

    @Test
    void loadAsResourceFileNotExist() {
        String filename = "archivoInexistente.json";

        StorageNotFound exception = assertThrows(StorageNotFound.class, () -> {
            storageService.loadAsResource(filename);
        });

        assertTrue(exception.getMessage().contains("No se puede leer fichero: " + filename));
    }

    @Test
    void loadAsResourceMalformedUrl() {
        String filename = "../../etc/passwd";

        StorageNotFound exception = assertThrows(StorageNotFound.class, () -> {
            storageService.loadAsResource(filename);
        });

        assertTrue(exception.getMessage().contains("No se puede leer fichero: " + filename));
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

        storageService.init();

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
    void delete() throws IOException {
        String filename = "testFileToDelete.json";
        Path filePath = Paths.get(TEST_ROOT_LOCATION).resolve(filename);

        Files.createDirectories(Paths.get(TEST_ROOT_LOCATION));
        Files.write(filePath, "Contenido de prueba".getBytes());

        assertTrue(Files.exists(filePath), "El archivo debería existir antes de eliminarlo");

        storageService.delete(filename);

        assertFalse(Files.exists(filePath), "El archivo debería haber sido eliminado");
    }
}