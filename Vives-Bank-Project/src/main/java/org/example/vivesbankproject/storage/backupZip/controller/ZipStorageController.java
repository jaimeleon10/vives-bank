package org.example.vivesbankproject.storage.backupZip.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.storage.exceptions.StorageInternal;
import org.example.vivesbankproject.storage.backupZip.services.ZipStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/storage/zip")
public class ZipStorageController {

    private final ZipStorageService zipStorageService;

    @Autowired
    public ZipStorageController(ZipStorageService zipStorageService) {
        this.zipStorageService = zipStorageService;
    }

    @PostMapping("/generate")
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

    @PostMapping("/import/{filename:.+}")
    public ResponseEntity<List<Object>> importFromZip(@PathVariable String filename) {
        try {
            Path path = Path.of("data", "clientes.zip");

            List<Object> data = zipStorageService.loadFromZip(path.toFile());

            return ResponseEntity.ok(data);
        } catch (Exception e) {
            log.error("Error desconocido al procesar el archivo ZIP: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @GetMapping(value = "/{filename:.+}")
    @ResponseBody
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

    @DeleteMapping("/{filename:.+}")
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