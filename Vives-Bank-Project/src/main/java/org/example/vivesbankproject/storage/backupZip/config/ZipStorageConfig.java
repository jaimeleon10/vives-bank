package org.example.vivesbankproject.storage.backupZip.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.storage.backupZip.services.ZipStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class ZipStorageConfig {
    private final ZipStorageService zipStorageService;

    @Value("${upload.delete}")
    private String deleteAll;

    @Autowired
    public ZipStorageConfig(ZipStorageService zipStorageService) {
        this.zipStorageService = zipStorageService;
    }

    @PostConstruct
    public void init() {
        if (deleteAll.equals("true")) {
            log.info("Borrando ficheros de almacenamiento...");
            zipStorageService.delete("clientes.zip");
        }

        zipStorageService.init();
    }
}