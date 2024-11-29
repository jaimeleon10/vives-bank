package org.example.vivesbankproject.storage.jsonClientes.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.storage.jsonClientes.services.JsonClientesStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class JsonClientesStorageConfig {
    private final JsonClientesStorageService jsonClientesStorageService;

    @Value("${upload.delete}")
    private String deleteAll;

    @Autowired
    public JsonClientesStorageConfig(JsonClientesStorageService jsonClientesStorageService) {
        this.jsonClientesStorageService = jsonClientesStorageService;
    }

    @PostConstruct
    public void init() {
        if (deleteAll.equals("true")) {
            log.info("Borrando ficheros de almacenamiento...");
            jsonClientesStorageService.delete("clientes.json");
        }

        jsonClientesStorageService.init();
    }
}