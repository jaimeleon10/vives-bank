package org.example.vivesbankproject.storage.jsonClientesAdmin.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.storage.jsonClientes.services.JsonClientesStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Configuration
@Slf4j
public class JsonClientesAdminStorageConfig {
    private final JsonClientesStorageService jsonClientesStorageService;

    @Value("${upload.delete}")
    private String deleteAll;

    @Autowired
    public JsonClientesAdminStorageConfig(JsonClientesStorageService jsonClientesStorageService) {
        this.jsonClientesStorageService = jsonClientesStorageService;
    }

    @PostConstruct
    public void init() {
        if (deleteAll.equals("true")) {
            log.info("Borrando ficheros de almacenamiento...");
            jsonClientesStorageService.delete("admin_clientes_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".json");
        }

        jsonClientesStorageService.init();
    }
}