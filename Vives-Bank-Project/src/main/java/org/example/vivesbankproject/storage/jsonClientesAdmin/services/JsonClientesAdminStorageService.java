package org.example.vivesbankproject.storage.jsonClientesAdmin.services;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface JsonClientesAdminStorageService {
    void init();

    String storeAll();

    String store(String guid);

    Stream<Path> loadAll();

    Path load(String filename);

    Resource loadAsResource(String filename);

    void delete(String filename);
}
