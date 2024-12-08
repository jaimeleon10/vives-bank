package org.example.vivesbankproject.rest.storage.jsonMovimientos.services;

import org.springframework.core.io.Resource;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface JsonMovimientosStorageService {
    void init();

    String storeAll();

    String store(String guid);

    Stream<Path> loadAll();

    Path load(String filename);

    Resource loadAsResource(String filename);

    void delete(String filename);
}