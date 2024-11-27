package org.example.vivesbankproject.storage.service;

import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface ZipStorageService {
    void init();
    String store(MultipartFile file);
    Path load(String filename);
    void delete(String filename);
}