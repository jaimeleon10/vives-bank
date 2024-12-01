package org.example.vivesbankproject.storage.pdfMovimientos.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.storage.jsonMovimientos.services.JsonMovimientosStorageService;
import org.example.vivesbankproject.storage.pdfMovimientos.services.PdfMovimientosStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Configuration
@Slf4j
public class PdfMovimientosStorageConfig {
    private final PdfMovimientosStorageService pdfMovimientosStorageService;

    @Value("${upload.delete}")
    private String deleteAll;

    @Autowired
    public PdfMovimientosStorageConfig(PdfMovimientosStorageService pdfMovimientosStorageService) {
        this.pdfMovimientosStorageService = pdfMovimientosStorageService;
    }

    @PostConstruct
    public void init() {
        if (deleteAll.equals("true")) {
            log.info("Borrando ficheros de almacenamiento...");
            pdfMovimientosStorageService.delete("admin_movimientos_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".pdf");
        }

        pdfMovimientosStorageService.init();
    }
}