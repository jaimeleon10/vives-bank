package org.example.vivesbankproject.config.storage.jsonClientesAdminConfig;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.rest.storage.jsonClientes.services.JsonClientesStorageService;
import org.example.vivesbankproject.rest.storage.jsonClientesAdmin.services.JsonClientesAdminFileSystemStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Configuración para el manejo de almacenamiento de archivos JSON relacionados con clientes administrativos.
 * Esta clase inicializa el servicio de almacenamiento y, opcionalmente, elimina archivos JSON
 * antiguos según la configuración proporcionada.
 *
 * @author Jaime León, Natalia González, Germán Fernández, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Configuration
@Slf4j
public class JsonClientesAdminStorageConfig {

    /**
     * Servicio de almacenamiento para operaciones relacionadas con archivos JSON de clientes administrativos.
     */
    private final JsonClientesAdminFileSystemStorage jsonClientesAdminFileSystemStorage;

    /**
     * Indica si se deben eliminar todos los archivos JSON al iniciar.
     * Configurado mediante la propiedad 'upload.delete' en el archivo de configuración.
     */
    @Value("${upload.delete}")
    private String deleteAll;

    /**
     * Constructor que inyecta el servicio de almacenamiento de clientes administrativos.
     *
     * @param jsonClientesAdminFileSystemStorage servicio encargado de la gestión de almacenamiento JSON.
     */
    @Autowired
    public JsonClientesAdminStorageConfig(JsonClientesAdminFileSystemStorage jsonClientesAdminFileSystemStorage) {
        this.jsonClientesAdminFileSystemStorage = jsonClientesAdminFileSystemStorage;
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
            jsonClientesAdminFileSystemStorage.delete("admin_clientes_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".json");
        }

        jsonClientesAdminFileSystemStorage.init();
    }
}
