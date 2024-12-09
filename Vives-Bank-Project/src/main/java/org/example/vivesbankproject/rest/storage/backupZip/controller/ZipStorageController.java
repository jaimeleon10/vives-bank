package org.example.vivesbankproject.rest.storage.backupZip.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.rest.storage.exceptions.StorageInternal;
import org.example.vivesbankproject.rest.storage.backupZip.services.ZipStorageService;
import org.example.vivesbankproject.rest.storage.exceptions.StorageNotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;

/**
 * Controlador REST para gestionar operaciones relacionadas con el almacenamiento de archivos ZIP.
 * Proporciona endpoints para generar, importar, servir y eliminar archivos ZIP.
 *
 * @author Jaime León, Natalia González, Germán Fernández, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@RestController
@Slf4j
@RequestMapping("/storage/zip")
public class ZipStorageController {

    /**
     * Servicio para operaciones de almacenamiento relacionadas con archivos ZIP.
     */
    private final ZipStorageService zipStorageService;

    /**
     * Constructor que inyecta el servicio de almacenamiento ZIP.
     *
     * @param zipStorageService servicio de almacenamiento ZIP.
     */
    @Autowired
    public ZipStorageController(ZipStorageService zipStorageService) {
        this.zipStorageService = zipStorageService;
    }

    /**
     * Genera un archivo ZIP con los datos almacenados.
     *
     * @return respuesta con el nombre del archivo ZIP generado.
     */
    @PostMapping("/generate")
    @Operation(summary = "Generar un archivo ZIP", description = "Genera un archivo ZIP con los datos actuales del sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Archivo ZIP generado con éxito"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor al generar el archivo ZIP")
    })
    public ResponseEntity<String> generateZip() {
        try {
            String storedFilename = zipStorageService.export();
            return ResponseEntity.ok("Archivo ZIP generado con éxito: " + storedFilename);
        } catch (StorageInternal e) {
            log.error("Error al generar el archivo ZIP: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al generar el archivo ZIP.");
        }
    }

    /**
     * Importa datos desde un archivo ZIP.
     *
     * @param filename nombre del archivo ZIP que se va a importar.
     * @return respuesta HTTP sin contenido si se importa correctamente.
     */
    @GetMapping("/import/{filename:.+}")
    @Operation(summary = "Importar datos desde un archivo ZIP", description = "Carga datos desde un archivo ZIP especificado.")
    @Parameters({
            @Parameter(name = "filename", description = "Nombre del archivo ZIP a importar", example = "datos.zip")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Datos importados correctamente"),
            @ApiResponse(responseCode = "404", description = "Archivo no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Void> importFromZip(@PathVariable String filename) {
        try {
            zipStorageService.loadFromZip(new File(filename));
            return ResponseEntity.noContent().build();
        } catch (StorageNotFound e) {
            log.error("Archivo no encontrado: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        } catch (Exception e) {
            log.error("Error desconocido al procesar el archivo ZIP: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    /**
     * Proporciona el contenido de un archivo ZIP especificado.
     *
     * @param filename nombre del archivo ZIP a servir.
     * @param request información de la solicitud HTTP.
     * @return recurso del archivo ZIP solicitado.
     */
    @GetMapping(value = "/{filename:.+}")
    @ResponseBody
    @Operation(summary = "Servir un archivo ZIP", description = "Devuelve el contenido del archivo ZIP especificado.")
    @Parameters({
            @Parameter(name = "filename", description = "Nombre del archivo ZIP a servir", example = "datos.zip")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Archivo ZIP servido correctamente"),
            @ApiResponse(responseCode = "404", description = "Archivo no encontrado")
    })
    public ResponseEntity<Resource> serveFile(@PathVariable String filename, HttpServletRequest request) {
        try {
            Resource file = zipStorageService.loadAsResource(filename);

            String contentType = request.getServletContext().getMimeType(file.getFile().getAbsolutePath());
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(file);
        } catch (Exception e) {
            log.error("Error al cargar el archivo ZIP", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }
    }

    /**
     * Elimina un archivo ZIP especificado.
     *
     * @param filename nombre del archivo ZIP a eliminar.
     * @return respuesta con el estado de la operación de eliminación.
     */
    @DeleteMapping("/{filename:.+}")
    @Operation(summary = "Eliminar un archivo ZIP", description = "Elimina el archivo ZIP especificado del almacenamiento.")
    @Parameters({
            @Parameter(name = "filename", description = "Nombre del archivo ZIP a eliminar", example = "datos.zip")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Archivo ZIP eliminado correctamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor al eliminar el archivo ZIP")
    })
    public ResponseEntity<String> deleteFile(@PathVariable String filename) {
        try {
            zipStorageService.delete(filename);
            return ResponseEntity.ok("Archivo ZIP eliminado: " + filename);
        } catch (Exception e) {
            log.error("Error al eliminar el archivo ZIP", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar el archivo ZIP: " + e.getMessage());
        }
    }
}

