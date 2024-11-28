package org.example.vivesbankproject.storage.images.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.storage.images.services.StorageImagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class StorageImagesConfig {
    private final StorageImagesService storageImagesService;

    @Value("${upload.delete}")
    private String deleteAll;

    @Autowired
    public StorageImagesConfig(StorageImagesService storageImagesService) {
        this.storageImagesService = storageImagesService;
    }

    @PostConstruct
    public void init() {
        if (deleteAll.equals("true")) {
            log.info("Borrando ficheros de almacenamiento...");
            storageImagesService.deleteAll();
        }

        storageImagesService.init();
    }
}
