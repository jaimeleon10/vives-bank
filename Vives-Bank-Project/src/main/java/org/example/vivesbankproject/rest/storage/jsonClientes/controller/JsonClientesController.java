package org.example.vivesbankproject.rest.storage.jsonClientes.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.rest.storage.exceptions.StorageInternal;
import org.example.vivesbankproject.rest.storage.jsonClientes.services.JsonClientesStorageService;
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
 * Controlador para gestionar la lógica relacionada con la generación y acceso de archivos JSON de clientes.
 * Proporciona rutas para generar archivos, acceder a recursos individuales y listar todos los archivos en almacenamiento.
 *
 * @author Jaime León, Natalia González, Germán Fernández, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@RestController
@Slf4j
@RequestMapping("/storage/jsonClientes")
public class JsonClientesController {

    private final JsonClientesStorageService jsonClientesStorageService;

    /**
     * Constructor para inyección de dependencias de `JsonClientesStorageService`.
     *
     * @param jsonClientesStorageService Servicio encargado de manejar operaciones de almacenamiento de clientes.
     */
    @Autowired
    public JsonClientesController(JsonClientesStorageService jsonClientesStorageService) {
        this.jsonClientesStorageService = jsonClientesStorageService;
    }

    /**
     * Endpoint para generar un archivo JSON de clientes basado en el identificador GUID.
     *
     * @param guid Identificador único para la operación de generación.
     * @return Respuesta con el nombre del archivo generado o error en caso de fallo.
     */
    @PostMapping("/generate/{guid}")
    @Operation(
            summary = "Genera el archivo JSON de clientes",
            description = "Genera un archivo JSON con la información de clientes utilizando un identificador GUID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Archivo JSON generado exitosamente", content = @Content(mediaType = "text/plain")),
                    @ApiResponse(responseCode = "500", description = "Error interno al generar el archivo JSON")
            }
    )
    public ResponseEntity<String> generateClienteJson(@PathVariable String guid) {
        try {
            String storedFilename = jsonClientesStorageService.store(guid);
            return ResponseEntity.ok("Archivo JSON de clientes generado con éxito: " + storedFilename);
        } catch (StorageInternal e) {
            log.error("Error al generar el archivo JSON de clientes: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al generar el archivo JSON de clientes.");
        }
    }

    /**
     * Endpoint para recuperar un recurso almacenado como un archivo JSON.
     *
     * @param filename Nombre del archivo a recuperar.
     * @param request  Información del servidor HTTP para determinar el tipo de contenido.
     * @return Archivo como recurso para su descarga.
     */
    @GetMapping(value = "/{filename:.+}")
    @ResponseBody
    @Operation(
            summary = "Obtiene un archivo JSON para su descarga",
            description = "Devuelve un recurso para el archivo JSON almacenado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Archivo cargado exitosamente"),
                    @ApiResponse(responseCode = "500", description = "Error interno al intentar acceder al archivo")
            }
    )
    public ResponseEntity<Resource> serveFile(@PathVariable String filename, HttpServletRequest request) {
        Resource file = jsonClientesStorageService.loadAsResource(filename);

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
     * Endpoint para listar todos los archivos almacenados en el almacenamiento de clientes JSON.
     *
     * @return Lista de nombres de archivos JSON o error si no se puede acceder a ellos.
     */
    @GetMapping("/list")
    @Operation(
            summary = "Lista todos los archivos almacenados",
            description = "Devuelve una lista con los nombres de todos los archivos JSON almacenados.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
                    @ApiResponse(responseCode = "500", description = "Error interno al obtener la lista de archivos")
            }
    )
    public ResponseEntity<List<String>> listAllFiles() {
        try {
            Stream<Path> files = jsonClientesStorageService.loadAll();
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