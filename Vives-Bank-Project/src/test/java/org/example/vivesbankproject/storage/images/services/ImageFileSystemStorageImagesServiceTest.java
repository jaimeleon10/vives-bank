package org.example.vivesbankproject.storage.images.services;

import org.example.vivesbankproject.storage.exceptions.StorageBadRequest;
import org.example.vivesbankproject.storage.exceptions.StorageNotFound;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class ImageFileSystemStorageImagesServiceTest {

    private static final String TEST_ROOT_LOCATION = "data/test";

    private ImageFileSystemStorageImagesService storageService;

    @BeforeEach
    void setUp() throws IOException {
        storageService = new ImageFileSystemStorageImagesService(TEST_ROOT_LOCATION);

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

    @AfterAll
    static void cleanupAfterTests() throws IOException {
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
    void testStoreValidFile() {
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        String storedFilename = storageService.store(multipartFile);

        assertNotNull(storedFilename);
        assertTrue(storedFilename.endsWith("test.jpg"));
        assertTrue(Files.exists(Paths.get(TEST_ROOT_LOCATION).resolve(storedFilename)));
    }

    @Test
    void testStoreEmptyFile() {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                new byte[0]
        );

        assertThrows(StorageBadRequest.class, () -> storageService.store(emptyFile));
    }

    @Test
    void testStoreFileWithRelativePath() {
        MockMultipartFile fileWithRelativePath = new MockMultipartFile(
                "file",
                "../test.jpg",
                "image/jpeg",
                "test content".getBytes()
        );

        assertThrows(StorageBadRequest.class, () -> storageService.store(fileWithRelativePath));
    }

    @Test
    void testLoadAll() {
        MockMultipartFile file1 = new MockMultipartFile(
                "file1",
                "test1.jpg",
                "image/jpeg",
                "test content 1".getBytes()
        );
        MockMultipartFile file2 = new MockMultipartFile(
                "file2",
                "test2.jpg",
                "image/jpeg",
                "test content 2".getBytes()
        );

        storageService.store(file1);
        storageService.store(file2);

        Stream<Path> loadedFiles = storageService.loadAll();
        assertEquals(2, loadedFiles.count());
    }

    @Test
    void testLoadAsResource() {
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        String storedFilename = storageService.store(multipartFile);
        Resource resource = storageService.loadAsResource(storedFilename);

        assertNotNull(resource);
        assertTrue(resource.exists());
        assertTrue(resource.isReadable());
    }

    @Test
    void testLoadAsResourceNotFound() {
        assertThrows(StorageNotFound.class, () -> storageService.loadAsResource("nonexistent.jpg"));
    }

    @Test
    void testDeleteFile() {
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        String storedFilename = storageService.store(multipartFile);
        Path path = Paths.get(TEST_ROOT_LOCATION);
        assertTrue(Files.exists(path.resolve(storedFilename)));

        storageService.delete(storedFilename);
        assertFalse(Files.exists(path.resolve(storedFilename)));
    }

    @Test
    void testDeleteAll() {
        MockMultipartFile file1 = new MockMultipartFile(
                "file1",
                "test1.jpg",
                "image/jpeg",
                "test content 1".getBytes()
        );
        MockMultipartFile file2 = new MockMultipartFile(
                "file2",
                "test2.jpg",
                "image/jpeg",
                "test content 2".getBytes()
        );

        storageService.store(file1);
        storageService.store(file2);

        storageService.deleteAll();
        assertFalse(Files.exists(Paths.get(TEST_ROOT_LOCATION)));
    }
}