package org.example.vivesbankproject.storage.jsonClientes.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import org.example.vivesbankproject.rest.cliente.dto.ClienteJson;
import org.example.vivesbankproject.rest.cliente.models.Cliente;
import org.example.vivesbankproject.rest.cliente.models.Direccion;
import org.example.vivesbankproject.rest.cliente.repositories.ClienteRepository;
import org.example.vivesbankproject.rest.cuenta.models.Cuenta;
import org.example.vivesbankproject.rest.cuenta.models.TipoCuenta;
import org.example.vivesbankproject.rest.storage.exceptions.StorageInternal;
import org.example.vivesbankproject.rest.storage.exceptions.StorageNotFound;
import org.example.vivesbankproject.rest.storage.jsonClientes.services.JsonClientesFileSystemStorage;
import org.example.vivesbankproject.rest.storage.jsonClientes.services.JsonClientesStorageService;
import org.example.vivesbankproject.rest.tarjeta.models.Tarjeta;
import org.example.vivesbankproject.rest.cuenta.mappers.CuentaMapper;
import org.example.vivesbankproject.rest.tarjeta.mappers.TarjetaMapper;
import org.example.vivesbankproject.rest.users.mappers.UserMapper;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JsonClientesFileSystemStorageTest {

    private static final String TEST_ROOT_LOCATION = "data/test";

    private JsonClientesFileSystemStorage storageService;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private TarjetaMapper tarjetaMapper;

    @Mock
    private CuentaMapper cuentaMapper;

    @Mock
    private UserMapper userMapper;

    @BeforeEach
    void setUp() throws IOException {
        storageService = new JsonClientesFileSystemStorage(TEST_ROOT_LOCATION, clienteRepository, tarjetaMapper, cuentaMapper, userMapper);

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
    void store() throws IOException {
        Cuenta mockCuenta = Cuenta.builder()
                .guid("cuentaGuid")
                .iban("iban")
                .saldo(new BigDecimal("100.00"))
                .tarjeta(new Tarjeta())
                .tipoCuenta(new TipoCuenta())
                .cliente(new Cliente())
                .build();

        String guid = "test-guid";
        Cliente mockCliente = new Cliente();
        mockCliente.setGuid(guid);
        mockCliente.setDni("12345678A");
        mockCliente.setNombre("John");
        mockCliente.setApellidos("Doe");
        mockCliente.setDireccion(new Direccion());
        mockCliente.setEmail("john.doe@example.com");
        mockCliente.setTelefono("123456789");
        mockCliente.setFotoPerfil("foto-perfil.jpg");
        mockCliente.setFotoDni("foto-dni.jpg");
        mockCliente.setCuentas(Set.of(mockCuenta));
        mockCliente.setCreatedAt(LocalDateTime.now());
        mockCliente.setUpdatedAt(LocalDateTime.now());
        mockCliente.setIsDeleted(false);

        when(clienteRepository.findByGuid(guid)).thenReturn(Optional.of(mockCliente));

        String storedFilename = storageService.store(guid);

        assertNotNull(storedFilename);
        assertTrue(storedFilename.contains(guid));
        assertTrue(storedFilename.endsWith(".json"));

        Path storedFilePath = Paths.get(TEST_ROOT_LOCATION).resolve(storedFilename);
        assertTrue(Files.exists(storedFilePath));

        String fileContent = Files.readString(storedFilePath);

        ObjectMapper objectMapper = new ObjectMapper();
        JavaTimeModule module = new JavaTimeModule();
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        objectMapper.registerModule(module);

        List<ClienteJson> clienteJsonList = objectMapper.readValue(fileContent, new TypeReference<>(){});

        assertFalse(clienteJsonList.isEmpty(), "El archivo JSON debe contener al menos un cliente");

        ClienteJson clienteJson = clienteJsonList.get(0);

        assertEquals(guid, clienteJson.getGuid());
        assertEquals("John", clienteJson.getNombre());
        assertEquals("Doe", clienteJson.getApellidos());
        assertEquals("12345678A", clienteJson.getDni());
        assertEquals("john.doe@example.com", clienteJson.getEmail());
        assertEquals("123456789", clienteJson.getTelefono());
        assertEquals("foto-perfil.jpg", clienteJson.getFotoPerfil());
        assertEquals("foto-dni.jpg", clienteJson.getFotoDni());
        assertNotNull(clienteJson.getDireccion(), "La dirección no debe ser nula");
        assertFalse(clienteJson.getCuentas().isEmpty(), "El cliente debe tener cuentas asociadas");
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

        UrlResource mockResource = mock(UrlResource.class);

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