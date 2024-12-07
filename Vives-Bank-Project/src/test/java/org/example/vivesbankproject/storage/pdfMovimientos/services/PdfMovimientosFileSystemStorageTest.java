package org.example.vivesbankproject.storage.pdfMovimientos.services;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import org.bson.types.ObjectId;
import org.example.vivesbankproject.movimientos.models.*;
import org.example.vivesbankproject.movimientos.repositories.MovimientosRepository;
import org.example.vivesbankproject.storage.exceptions.StorageNotFound;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class PdfMovimientosFileSystemStorageTest {

    private static final String TEST_ROOT_LOCATION = "data/test";

    private PdfMovimientosFileSystemStorage storageService;

    @MockBean
    private MovimientosRepository movimientosRepository;

    @BeforeEach
    void setUp() throws IOException {
        storageService = new PdfMovimientosFileSystemStorage(TEST_ROOT_LOCATION, movimientosRepository);

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
    void storeAll() throws IOException {
        List<Movimiento> movimientos = List.of(
                new Movimiento().builder()
                        .id(new ObjectId())
                        .guid("guid1")
                        .clienteGuid("cliente1")
                        .domiciliacion(new Domiciliacion())
                        .ingresoDeNomina(new IngresoDeNomina())
                        .pagoConTarjeta(new PagoConTarjeta())
                        .transferencia(new Transferencia())
                        .build(),
                new Movimiento().builder()
                        .id(new ObjectId())
                        .guid("guid2")
                        .clienteGuid("cliente2")
                        .domiciliacion(new Domiciliacion())
                        .ingresoDeNomina(new IngresoDeNomina())
                        .pagoConTarjeta(new PagoConTarjeta())
                        .transferencia(new Transferencia())
                        .build()
        );

        when(movimientosRepository.findAll()).thenReturn(movimientos);

        String filename = storageService.storeAll();

        Path pdfPath = Path.of("dataAdmin").resolve(filename);

        System.out.println("Verificando existencia del archivo en: " + pdfPath.toAbsolutePath());

        assertTrue(Files.exists(pdfPath), "El archivo PDF debería existir.");

        long fileSize = Files.size(pdfPath);
        assertTrue(fileSize > 0, "El archivo PDF no debería estar vacío.");

        try (PdfReader reader = new PdfReader(pdfPath.toString())) {
            PdfDocument pdfDoc = new PdfDocument(reader);
            assertTrue(pdfDoc.getNumberOfPages() > 0, "El PDF debería tener al menos una página.");
        }

        try {
            Files.deleteIfExists(pdfPath);
        } catch (IOException e) {
            fail("No se pudo eliminar el archivo de prueba");
        }
    }

    @Test
    void store() throws IOException {
        String guid = "1234";
        Movimiento mockMovimiento = new Movimiento();
        mockMovimiento.setGuid(guid);
        mockMovimiento.setClienteGuid("cliente123");
        mockMovimiento.setDomiciliacion(new Domiciliacion());
        mockMovimiento.setIngresoDeNomina(new IngresoDeNomina());
        mockMovimiento.setPagoConTarjeta(new PagoConTarjeta());
        mockMovimiento.setTransferencia(new Transferencia());

        when(movimientosRepository.findMovimientosByClienteGuid(guid))
                .thenReturn(Optional.of(mockMovimiento));

        String storedFilename = storageService.store(guid);

        assertNotNull(storedFilename);
        assertTrue(storedFilename.contains(guid));
        assertTrue(storedFilename.endsWith(".pdf"));

        Path pdfPath = Path.of(TEST_ROOT_LOCATION).resolve(storedFilename);
        assertTrue(pdfPath.toFile().exists(), "El archivo PDF debería haberse creado.");

        try {
            Files.deleteIfExists(pdfPath);
        } catch (IOException e) {
            fail("No se pudo eliminar el archivo de prueba");
        }
    }

    @Test
    void loadAll() throws IOException {
        Path testFile1 = Paths.get(TEST_ROOT_LOCATION).resolve("testFile1.pdf");
        Path testFile2 = Paths.get(TEST_ROOT_LOCATION).resolve("testFile2.pdf");

        Files.createDirectories(Paths.get(TEST_ROOT_LOCATION));
        Files.write(testFile1, "Contenido de prueba 1".getBytes());
        Files.write(testFile2, "Contenido de prueba 2".getBytes());

        Stream<Path> loadedFiles = storageService.loadAll();

        List<Path> fileList = loadedFiles.collect(Collectors.toList());

        assertEquals(2, fileList.size());
        assertTrue(fileList.contains(Paths.get("testFile1.pdf")));
        assertTrue(fileList.contains(Paths.get("testFile2.pdf")));

        Files.deleteIfExists(testFile1);
        Files.deleteIfExists(testFile2);
    }

    @Test
    void load() {
        String filename = "testFile.pdf";
        Path expectedPath = Paths.get(TEST_ROOT_LOCATION).resolve(filename);

        Path resolvedPath = storageService.load(filename);

        assertNotNull(resolvedPath);
        assertEquals(expectedPath, resolvedPath);
    }

    @Test
    void loadAsResource() {
        String filename = "testFile.pdf";
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
        String filename = "archivoInexistente.pdf";

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
        String filename = "testFileToDelete.pdf";
        Path filePath = Paths.get(TEST_ROOT_LOCATION).resolve(filename);

        Files.createDirectories(Paths.get(TEST_ROOT_LOCATION));
        Files.write(filePath, "Contenido de prueba".getBytes());

        assertTrue(Files.exists(filePath), "El archivo debería existir antes de eliminarlo");

        storageService.delete(filename);

        assertFalse(Files.exists(filePath), "El archivo debería haber sido eliminado");
    }
}