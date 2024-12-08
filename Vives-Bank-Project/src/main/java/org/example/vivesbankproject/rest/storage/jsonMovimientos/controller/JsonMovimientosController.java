package org.example.vivesbankproject.rest.storage.jsonMovimientos.controller;

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

@RestController
@Slf4j
@RequestMapping("/storage/jsonMovimientos")
public class JsonMovimientosController {

    private final JsonMovimientosStorageService jsonMovimientosStorageService;

    @Autowired
    public JsonMovimientosController(JsonMovimientosStorageService jsonMovimientosStorageService) {
        this.jsonMovimientosStorageService = jsonMovimientosStorageService;
    }

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