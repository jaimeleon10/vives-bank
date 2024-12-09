package org.example.vivesbankproject.config.storage.pdfMovimientosConfig;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.rest.storage.pdfMovimientos.services.PdfMovimientosStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Configuración para el manejo de almacenamiento de archivos PDF relacionados con movimientos.
 * Esta clase inicializa el servicio de almacenamiento y, opcionalmente, elimina archivos PDF
 * antiguos según la configuración proporcionada.
 *
 * @author Jaime León, Natalia González, Germán Fernández, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Configuration
@Slf4j
public class PdfMovimientosStorageConfig {

    /**
     * Servicio de almacenamiento para operaciones relacionadas con archivos PDF de movimientos.
     */
    private final PdfMovimientosStorageService pdfMovimientosStorageService;

    /**
     * Indica si se deben eliminar todos los archivos PDF al iniciar.
     * Configurado mediante la propiedad 'upload.delete' en el archivo de configuración.
     */
    @Value("${upload.delete}")
    private String deleteAll;

    /**
     * Constructor que inyecta el servicio de almacenamiento de movimientos.
     *
     * @param pdfMovimientosStorageService servicio encargado de la gestión de almacenamiento PDF.
     */
    @Autowired
    public PdfMovimientosStorageConfig(PdfMovimientosStorageService pdfMovimientosStorageService) {
        this.pdfMovimientosStorageService = pdfMovimientosStorageService;
    }

    /**
     * Método de inicialización llamado después de la construcción del bean.
     * Si la propiedad 'upload.delete' está configurada como "true",
     * elimina el archivo PDF de almacenamiento correspondiente a la fecha actual.
     * Posteriormente, inicializa el servicio de almacenamiento PDF.
     */
    @PostConstruct
    public void init() {
        if ("true".equals(deleteAll)) {
            log.info("Borrando ficheros de almacenamiento...");
            pdfMovimientosStorageService.delete("admin_movimientos_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".pdf");
        }

        pdfMovimientosStorageService.init();
    }
}
