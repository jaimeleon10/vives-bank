package org.example.vivesbankproject.storage.jsonClientesAdmin.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.vivesbankproject.rest.cliente.dto.ClienteJsonZip;
import org.example.vivesbankproject.rest.cliente.models.Cliente;
import org.example.vivesbankproject.rest.cliente.models.Direccion;
import org.example.vivesbankproject.rest.cliente.repositories.ClienteRepository;
import org.example.vivesbankproject.rest.cuenta.models.Cuenta;
import org.example.vivesbankproject.rest.cuenta.models.TipoCuenta;
import org.example.vivesbankproject.rest.storage.exceptions.StorageNotFound;
import org.example.vivesbankproject.rest.storage.jsonClientesAdmin.services.JsonClientesAdminFileSystemStorage;
import org.example.vivesbankproject.rest.tarjeta.models.Tarjeta;
import org.example.vivesbankproject.rest.tarjeta.models.TipoTarjeta;
import org.example.vivesbankproject.rest.users.models.Role;
import org.example.vivesbankproject.rest.users.models.User;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class JsonClientesAdminFileSystemStorageTest {

    private static final String TEST_ROOT_LOCATION = "data/test";

    private JsonClientesAdminFileSystemStorage storageService;

    @MockBean
    private ClienteRepository clienteRepository;

    @BeforeEach
    void setUp() throws IOException {
        storageService = new JsonClientesAdminFileSystemStorage(TEST_ROOT_LOCATION, clienteRepository);

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
    void storeAll() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setGuid("guid123");
        cliente.setDni("12345678A");
        cliente.setNombre("John");
        cliente.setApellidos("Doe");
        cliente.setDireccion(new Direccion());
        cliente.setEmail("john.doe@example.com");
        cliente.setTelefono("123456789");

        when(clienteRepository.findAll()).thenReturn(Collections.singletonList(cliente));

        String expectedFilename = "admin_clientes_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".json";
        Path expectedFilePath = Path.of(TEST_ROOT_LOCATION).resolve(expectedFilename);

        if (Files.exists(expectedFilePath)) {
            Files.delete(expectedFilePath);
        }
        assertFalse(Files.exists(expectedFilePath), "El archivo debería no existir antes de la ejecución.");

        String result = storageService.storeAll();

        assertEquals(expectedFilename, result, "El nombre del archivo generado no es el esperado.");
        assertTrue(Files.exists(expectedFilePath), "El archivo JSON debería haberse creado.");

        verify(clienteRepository, times(1)).findAll();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        String jsonData = Files.readString(expectedFilePath);
        assertNotNull(jsonData, "El contenido del archivo no debería ser nulo.");
        assertFalse(jsonData.isEmpty(), "El contenido del archivo no debería estar vacío.");

        ClienteJsonZip[] clientes = objectMapper.readValue(jsonData, ClienteJsonZip[].class);
        assertEquals(1, clientes.length, "Debería haber un cliente en el archivo JSON.");
        assertEquals("guid123", clientes[0].getGuid(), "El GUID del cliente no es el esperado.");
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