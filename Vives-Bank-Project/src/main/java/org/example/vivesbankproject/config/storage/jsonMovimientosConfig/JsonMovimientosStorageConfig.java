package org.example.vivesbankproject.config.storage.jsonMovimientosConfig;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.rest.storage.jsonMovimientos.services.JsonMovimientosStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Configuración para el manejo de almacenamiento de archivos JSON relacionados con movimientos.
 * Esta clase inicializa el servicio de almacenamiento y, opcionalmente, elimina archivos JSON
 * antiguos según la configuración proporcionada.
 *
 * @author Jaime León, Natalia González, Germán Fernández, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Configuration
@Slf4j
public class JsonMovimientosStorageConfig {

    /**
     * Servicio de almacenamiento para operaciones relacionadas con archivos JSON de movimientos.
     */
    private final JsonMovimientosStorageService jsonMovimientosStorageService;

    /**
     * Indica si se deben eliminar todos los archivos JSON al iniciar.
     * Configurado mediante la propiedad 'upload.delete' en el archivo de configuración.
     */
    @Value("${upload.delete}")
    private String deleteAll;

    /**
     * Constructor que inyecta el servicio de almacenamiento de movimientos.
     *
     * @param jsonMovimientosStorageService servicio encargado de la gestión de almacenamiento JSON.
     */
    @Autowired
    public JsonMovimientosStorageConfig(JsonMovimientosStorageService jsonMovimientosStorageService) {
        this.jsonMovimientosStorageService = jsonMovimientosStorageService;
    }

    /**
     * Método de inicialización llamado después de la construcción del bean.
     * Si la propiedad 'upload.delete' está configurada como "true",
     * elimina el archivo JSON de almacenamiento correspondiente a la fecha actual.
     * Posteriormente, inicializa el servicio de almacenamiento JSON.
     */
    @PostConstruct
    public void init() {
        if ("true".equals(deleteAll)) {
            log.info("Borrando ficheros de almacenamiento...");
            jsonMovimientosStorageService.delete("admin_movimientos_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".json");
        }

        jsonMovimientosStorageService.init();
    }
}
