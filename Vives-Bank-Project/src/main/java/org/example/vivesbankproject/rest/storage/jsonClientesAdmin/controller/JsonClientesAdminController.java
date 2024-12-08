package org.example.vivesbankproject.rest.storage.jsonClientesAdmin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.rest.storage.exceptions.StorageInternal;
import org.example.vivesbankproject.rest.storage.jsonClientesAdmin.services.JsonClientesAdminStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Jaime León, Natalia González, Germán Fernández, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@RestController
@Slf4j
@RequestMapping("/storage/jsonClientesAdmin")
public class JsonClientesAdminController {

    private final JsonClientesAdminStorageService jsonClientesAdminStorageService;

    @Autowired
    public JsonClientesAdminController(JsonClientesAdminStorageService jsonClientesAdminStorageService) {
        this.jsonClientesAdminStorageService = jsonClientesAdminStorageService;
    }

    /**
     * Genera un archivo JSON con la información de todos los clientes.
     *
     * @return ResponseEntity con un mensaje de éxito si la operación se realiza correctamente,
     * o un error en caso contrario.
     */
    @PostMapping("/generate")
    @Operation(
            summary = "Generar JSON de clientes",
            description = "Genera un archivo JSON con la información de todos los clientes.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "El archivo JSON fue generado correctamente."),
                    @ApiResponse(responseCode = "500", description = "Error interno al generar el archivo JSON.")
            }
    )
    public ResponseEntity<String> generateClientesJson() {
        try {
            String storedFilename = jsonClientesAdminStorageService.storeAll();
            return ResponseEntity.ok("Archivo JSON de clientes generado con éxito: " + storedFilename);
        } catch (StorageInternal e) {
            log.error("Error al generar el archivo JSON de clientes: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al generar el archivo JSON de clientes.");
        }
    }

    /**
     * Proporciona acceso a un archivo JSON en la ruta especificada.
     *
     * @param filename Nombre del archivo JSON a recuperar.
     * @param request  Objeto HttpServletRequest para determinar el tipo de contenido.
     * @return ResponseEntity con el recurso solicitado y el tipo de contenido adecuado.
     */
    @GetMapping(value = "/{filename:.+}")
    @ResponseBody
    @Operation(
            summary = "Obtener archivo como recurso",
            description = "Permite acceder al archivo JSON almacenado en el sistema.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "El archivo fue recuperado correctamente."),
                    @ApiResponse(responseCode = "404", description = "El archivo no existe en el almacenamiento."),
                    @ApiResponse(responseCode = "500", description = "Error al recuperar el recurso.")
            }
    )
    public ResponseEntity<Resource> serveFile(@PathVariable String filename, HttpServletRequest request) {
        Resource file = jsonClientesAdminStorageService.loadAsResource(filename);

        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(file.getFile().getAbsolutePath());
        } catch (IOException ex) {
            log.info("No se puede determinar el tipo de contenido del fichero");
        }

        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(file);
    }

    /**
     * Recupera una lista de todos los archivos JSON almacenados en el sistema.
     *
     * @return ResponseEntity con una lista de nombres de archivos o un error en caso de fallo.
     */
    @GetMapping("/list")
    @Operation(
            summary = "Obtener lista de archivos JSON",
            description = "Obtiene la lista de todos los archivos JSON almacenados en el sistema.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de archivos recuperada con éxito."),
                    @ApiResponse(responseCode = "500", description = "Error interno al recuperar la lista de archivos.")
            }
    )
    public ResponseEntity<List<String>> listAllFiles() {
        try {
            Stream<Path> files = jsonClientesAdminStorageService.loadAll();
            List<String> filenames = files.map(Path::toString)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(filenames);
        } catch (StorageInternal e) {
            log.error("Error al obtener la lista de archivos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }
}