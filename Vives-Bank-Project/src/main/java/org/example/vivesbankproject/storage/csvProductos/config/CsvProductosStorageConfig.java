package org.example.vivesbankproject.storage.csvProductos.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.storage.csvProductos.services.CsvProductosStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Configuration
@Slf4j
public class CsvProductosStorageConfig {
    private final CsvProductosStorageService csvProductosStorageService;

    @Value("${upload.delete}")
    private String deleteAll;

    @Autowired
    public CsvProductosStorageConfig(CsvProductosStorageService csvProductosStorageService) {
        this.csvProductosStorageService = csvProductosStorageService;
    }

    @PostConstruct
    public void init() {
        if (deleteAll.equals("true")) {
            log.info("Borrando ficheros de almacenamiento...");
            csvProductosStorageService.delete("admin_productos_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".csv");
        }

        csvProductosStorageService.init();
    }
}