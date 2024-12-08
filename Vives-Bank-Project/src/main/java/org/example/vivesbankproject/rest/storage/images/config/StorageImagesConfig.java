package org.example.vivesbankproject.rest.storage.images.config;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.rest.storage.images.services.StorageImagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Clase de configuración para la inicialización de operaciones de almacenamiento de imágenes.
 * Esta clase se encarga de ejecutar la configuración inicial al momento de iniciar la aplicación,
 * verificando configuraciones como la eliminación de archivos si así se indica en las propiedades de configuración.
 * <p>
 * Utiliza el servicio {@link StorageImagesService} para realizar operaciones de almacenamiento necesarias
 * al inicio de la aplicación.
 * </p>
 *
 * @author Jaime León, Natalia González, Germán Fernández, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Configuration
@Slf4j
public class StorageImagesConfig {

    private final StorageImagesService storageImagesService;

    @Value("${upload.delete}")
    private String deleteAll;

    /**
     * Constructor para la inyección de dependencias.
     *
     * @param storageImagesService Servicio para operaciones relacionadas con almacenamiento de imágenes.
     */
    @Autowired
    public StorageImagesConfig(StorageImagesService storageImagesService) {
        this.storageImagesService = storageImagesService;
    }

    /**
     * Método que se ejecuta después de que la clase haya sido inicializada.
     * Comprueba la configuración para determinar si se deben borrar todos los archivos de almacenamiento al iniciar.
     * Además, inicializa el almacenamiento a través del servicio {@link StorageImagesService}.
     *
     */
    @PostConstruct
    @Operation(
            summary = "Configuración inicial de almacenamiento de imágenes",
            description = "Elimina archivos de almacenamiento si la configuración 'upload.delete' es 'true' y ejecuta la inicialización.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Configuración e inicialización completada con éxito."),
                    @ApiResponse(responseCode = "500", description = "Error interno en la inicialización de almacenamiento")
            }
    )
    public void init() {
        if (deleteAll.equals("true")) {
            log.info("Borrando ficheros de almacenamiento...");
            storageImagesService.deleteAll();
        }

        storageImagesService.init();
    }
}