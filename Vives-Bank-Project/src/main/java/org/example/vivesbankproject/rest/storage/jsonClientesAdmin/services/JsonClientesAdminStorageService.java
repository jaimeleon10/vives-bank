package org.example.vivesbankproject.rest.storage.jsonClientesAdmin.services;

import org.springframework.core.io.Resource;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface JsonClientesAdminStorageService {
    void init();

    String storeAll();

    Stream<Path> loadAll();

    Path load(String filename);

    Resource loadAsResource(String filename);

    void delete(String filename);
}
