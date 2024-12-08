package org.example.vivesbankproject.rest.storage.pdfMovimientos.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.rest.storage.exceptions.StorageInternal;
import org.example.vivesbankproject.rest.storage.pdfMovimientos.services.PdfMovimientosStorageService;
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
 * Controlador REST para la generación, recuperación y listado de archivos PDF de movimientos.
 * Ofrece endpoints para generar un archivo PDF completo con movimientos, archivos individuales por cliente
 * y la posibilidad de listar los archivos PDF generados.
 * @author Jaime León, Natalia González, Germán Fernández, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@RestController
@Slf4j
@RequestMapping("/storage/pdfMovimientos")
public class PdfMovimientosController {

    private final PdfMovimientosStorageService pdfMovimientosStorageService;

    /**
     * Constructor para inyección de dependencias en el controlador.
     *
     * @param pdfMovimientosStorageService Servicio encargado de la lógica de almacenamiento para los archivos PDF.
     */
    @Autowired
    public PdfMovimientosController(PdfMovimientosStorageService pdfMovimientosStorageService) {
        this.pdfMovimientosStorageService = pdfMovimientosStorageService;
    }

    /**
     * Genera un archivo PDF con todos los movimientos.
     *
     * @return ResponseEntity con el mensaje de éxito o error en la generación del archivo PDF.
     */
    @PostMapping("/generate")
    @Operation(
            summary = "Generar un archivo PDF con todos los movimientos",
            description = "Genera un archivo PDF con todos los movimientos de la base de datos y lo almacena.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Archivo PDF generado con éxito."),
                    @ApiResponse(responseCode = "500", description = "Error interno al generar el archivo PDF.")
            }
    )
    public ResponseEntity<String> generateMovimientosPdf() {
        try {
            String storedFilename = pdfMovimientosStorageService.storeAll();
            return ResponseEntity.ok("Archivo PDF de movimientos generado con éxito: " + storedFilename);
        } catch (StorageInternal e) {
            log.error("Error al generar el archivo PDF de movimientos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al generar el archivo PDF de movimientos.");
        }
    }

    /**
     * Genera un archivo PDF con los movimientos de un cliente específico.
     *
     * @param guid Identificador del cliente para el que se generará el archivo PDF.
     * @return ResponseEntity con el mensaje de éxito o error en la generación del archivo PDF para el cliente.
     */
    @PostMapping("/generate/{guid}")
    @Operation(
            summary = "Generar un archivo PDF de movimientos para un cliente específico",
            description = "Genera un archivo PDF con los movimientos de un cliente específico basándose en su identificador (GUID).",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Archivo PDF generado con éxito."),
                    @ApiResponse(responseCode = "500", description = "Error interno al generar el archivo PDF.")
            }
    )
    public ResponseEntity<String> generateMovimientoPdf(@PathVariable String guid) {
        try {
            String storedFilename = pdfMovimientosStorageService.store(guid);
            return ResponseEntity.ok("Archivo PDF de movimientos de cliente generado con éxito: " + storedFilename);
        } catch (StorageInternal e) {
            log.error("Error al generar el archivo PDF de movimientos de cliente: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al generar el archivo PDF de movimientos de cliente.");
        }
    }

    /**
     * Recupera el archivo PDF almacenado para su descarga.
     *
     * @param filename Nombre del archivo PDF que se desea recuperar.
     * @param request  Información de la solicitud HTTP.
     * @return ResponseEntity con el archivo PDF como recurso para la descarga.
     */
    @GetMapping(value = "/{filename:.+}")
    @ResponseBody
    @Operation(
            summary = "Recuperar un archivo PDF desde el almacenamiento",
            description = "Recupera el archivo PDF especificado para su descarga desde el almacenamiento.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Archivo PDF cargado con éxito."),
                    @ApiResponse(responseCode = "500", description = "Error interno al recuperar el archivo PDF."),
                    @ApiResponse(responseCode = "404", description = "Archivo no encontrado en el almacenamiento.")
            }
    )
    public ResponseEntity<Resource> serveFile(@PathVariable String filename, HttpServletRequest request) {
        Resource file = pdfMovimientosStorageService.loadAsResource(filename);

        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(file.getFile().getAbsolutePath());
        } catch (IOException ex) {
            log.info("No se puede determinar el tipo de contenido del fichero");
        }

        if (contentType == null) {
            contentType = "application/pdf";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(file);
    }

    /**
     * Lista todos los archivos PDF disponibles en el almacenamiento.
     *
     * @return ResponseEntity con la lista de nombres de archivos almacenados o un error en caso de fallo.
     */
    @GetMapping("/list")
    @Operation(
            summary = "Listar todos los archivos PDF en el almacenamiento",
            description = "Devuelve una lista con los nombres de todos los archivos PDF generados y almacenados.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de archivos PDF recuperada con éxito."),
                    @ApiResponse(responseCode = "500", description = "Error interno al recuperar la lista de archivos.")
            }
    )
    public ResponseEntity<List<String>> listAllFiles() {
        try {
            Stream<Path> files = pdfMovimientosStorageService.loadAll();
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