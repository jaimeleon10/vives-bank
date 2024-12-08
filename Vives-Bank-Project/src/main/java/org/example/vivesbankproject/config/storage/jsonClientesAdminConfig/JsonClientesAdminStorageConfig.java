package org.example.vivesbankproject.config.storage.jsonClientesAdminConfig;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.rest.storage.jsonClientes.services.JsonClientesStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Configuration
@Slf4j
public class JsonClientesAdminStorageConfig {
    private final JsonClientesAdminFileSystemStorage jsonClientesAdminFileSystemStorage;

    @Value("${upload.delete}")
    private String deleteAll;

    @Autowired
    public JsonClientesAdminStorageConfig(JsonClientesAdminFileSystemStorage jsonClientesAdminFileSystemStorage) {
        this.jsonClientesAdminFileSystemStorage = jsonClientesAdminFileSystemStorage;
    }

    @PostConstruct
    public void init() {
        if (deleteAll.equals("true")) {
            log.info("Borrando ficheros de almacenamiento...");
            jsonClientesAdminFileSystemStorage.delete("admin_clientes_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".json");
        }

        jsonClientesAdminFileSystemStorage.init();
    }
}