package org.example.vivesbankproject.storage.backupZip.services;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface ZipStorageService {
    void init();
    String store();
    Path load(String filename);
    Resource loadAsResource(String filename);
    void delete(String filename);
}