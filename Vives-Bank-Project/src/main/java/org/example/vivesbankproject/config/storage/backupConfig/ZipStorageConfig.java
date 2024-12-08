package org.example.vivesbankproject.config.storage.backupConfig;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.rest.storage.backupZip.services.ZipStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración para el manejo de almacenamiento de archivos ZIP.
 * Esta clase inicializa el servicio de almacenamiento ZIP y, opcionalmente,
 * elimina archivos de almacenamiento existentes basándose en las propiedades configuradas.
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Configuration
@Slf4j
public class ZipStorageConfig {

    /**
     * Servicio de almacenamiento para operaciones relacionadas con archivos ZIP.
     */
    private final ZipStorageService zipStorageService;

    /**
     * Indica si se deben eliminar todos los archivos de almacenamiento al iniciar.
     * Configurado mediante la propiedad 'upload.delete' en el archivo de configuración.
     */
    @Value("${upload.delete}")
    private String deleteAll;

    /**
     * Constructor que inyecta el servicio de almacenamiento ZIP.
     *
     * @param zipStorageService servicio encargado de la gestión de almacenamiento ZIP.
     */
    @Autowired
    public ZipStorageConfig(ZipStorageService zipStorageService) {
        this.zipStorageService = zipStorageService;
    }

    /**
     * Método de inicialización llamado después de la construcción del bean.
     * Si la propiedad 'upload.delete' está configurada como "true",
     * elimina el archivo especificado. Posteriormente, inicializa el servicio
     * de almacenamiento ZIP.
     */
    @PostConstruct
    public void init() {
        if ("true".equals(deleteAll)) {
            log.info("Borrando ficheros de almacenamiento...");
            zipStorageService.delete("clientes.zip");
        }

        zipStorageService.init();
    }
}
