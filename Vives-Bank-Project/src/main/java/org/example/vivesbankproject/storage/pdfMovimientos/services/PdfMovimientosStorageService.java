package org.example.vivesbankproject.storage.pdfMovimientos.services;

import org.springframework.core.io.Resource;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface PdfMovimientosStorageService {
    void init();

    String storeAll();

    String store(String guid);

    Stream<Path> loadAll();

    Path load(String filename);

    Resource loadAsResource(String filename);

    void delete(String filename);
}