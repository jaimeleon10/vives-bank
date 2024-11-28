package org.example.vivesbankproject.storage.backupZip.services;

import org.springframework.core.io.Resource;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public interface ZipStorageService {
    void init();
    String export();
    Path load(String filename);
    Resource loadAsResource(String filename);
    void delete(String filename);
    List<Object> loadFromZip(File fileToUnzip);
    List<Object> loadJson(File jsonFile);
}