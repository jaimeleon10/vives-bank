package org.example.vivesbankproject.rest.storage.jsonMovimientos.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.rest.storage.exceptions.StorageInternal;
import org.example.vivesbankproject.rest.storage.jsonMovimientos.services.JsonMovimientosStorageService;
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
 * Controlador para gestionar operaciones relacionadas con el almacenamiento de archivos JSON
 * que contienen la información de movimientos de clientes.
 * Proporciona endpoints para la generación de archivos JSON y la recuperación de recursos.
 * @author Jaime León, Natalia González, Germán Fernández, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@RestController
@Slf4j
@RequestMapping("/storage/jsonMovimientos")
public class JsonMovimientosController {

    private final JsonMovimientosStorageService jsonMovimientosStorageService;

    @Autowired
    public JsonMovimientosController(JsonMovimientosStorageService jsonMovimientosStorageService) {
        this.jsonMovimientosStorageService = jsonMovimientosStorageService;
    }

    /**
     * Genera un archivo JSON con todos los movimientos de clientes.
     *
     * @return ResponseEntity con el nombre del archivo generado.
     */
    @Operation(
            summary = "Generar archivo JSON con movimientos",
            description = "Genera un archivo JSON con información de todos los movimientos de clientes.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Archivo generado correctamente."),
                    @ApiResponse(responseCode = "500", description = "Error interno al generar el archivo.")
            }
    )
    @PostMapping("/generate")
    public ResponseEntity<String> generateMovimientosJson() {
        try {
            String storedFilename = jsonMovimientosStorageService.storeAll();
            return ResponseEntity.ok("Archivo JSON de movimientos generado con éxito: " + storedFilename);
        } catch (StorageInternal e) {
            log.error("Error al generar el archivo JSON de movimientos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al generar el archivo JSON de movimientos.");
        }
    }

    /**
     * Genera un archivo JSON de movimientos para un cliente específico basado en su GUID.
     *
     * @param guid Identificador único del cliente.
     * @return ResponseEntity con el nombre del archivo generado.
     */
    @Operation(
            summary = "Generar archivo JSON de movimientos de un cliente",
            description = "Genera un archivo JSON con la información de movimientos para un cliente específico.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Archivo generado correctamente."),
                    @ApiResponse(responseCode = "500", description = "Error interno al generar el archivo.")
            }
    )
    @PostMapping("/generate/{guid}")
    public ResponseEntity<String> generateMovimientoJson(@PathVariable String guid) {
        try {
            String storedFilename = jsonMovimientosStorageService.store(guid);
            return ResponseEntity.ok("Archivo JSON de movimientos de cliente generado con éxito: " + storedFilename);
        } catch (StorageInternal e) {
            log.error("Error al generar el archivo JSON de movimientos de cliente: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al generar el archivo JSON de movimientos de cliente.");
        }
    }

    /**
     * Recupera un archivo JSON específico como recurso para su descarga.
     *
     * @param filename Nombre del archivo que se va a servir como recurso.
     * @param request Objeto HttpServletRequest para obtener el tipo MIME.
     * @return ResponseEntity con el recurso solicitado.
     */
    @Operation(
            summary = "Recuperar archivo JSON como recurso",
            description = "Devuelve el archivo JSON solicitado como recurso para su descarga.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso recuperado correctamente."),
                    @ApiResponse(responseCode = "404", description = "Recurso no encontrado."),
                    @ApiResponse(responseCode = "500", description = "Error interno al intentar recuperar el recurso.")
            }
    )
    @GetMapping(value = "/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename, HttpServletRequest request) {
        Resource file = jsonMovimientosStorageService.loadAsResource(filename);

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
     * Recupera la lista de todos los archivos almacenados en el almacenamiento de movimientos.
     *
     * @return Lista de nombres de archivos almacenados.
     */
    @Operation(
            summary = "Obtener lista de archivos almacenados",
            description = "Devuelve una lista con los nombres de todos los archivos JSON almacenados.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de archivos recuperada correctamente."),
                    @ApiResponse(responseCode = "500", description = "Error interno al recuperar la lista de archivos.")
            }
    )
    @GetMapping("/list")
    public ResponseEntity<List<String>> listAllFiles() {
        try {
            Stream<Path> files = jsonMovimientosStorageService.loadAll();
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