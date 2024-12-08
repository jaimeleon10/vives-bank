package org.example.vivesbankproject.rest.storage.jsonClientes.services;

import org.springframework.core.io.Resource;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface JsonClientesStorageService {
    void init();

    String store(String guid);

    Stream<Path> loadAll();

    Path load(String filename);

    Resource loadAsResource(String filename);

    void delete(String filename);
}
