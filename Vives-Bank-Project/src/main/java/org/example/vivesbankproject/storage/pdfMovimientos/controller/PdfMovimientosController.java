package org.example.vivesbankproject.storage.pdfMovimientos.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.storage.exceptions.StorageInternal;
import org.example.vivesbankproject.storage.pdfMovimientos.services.PdfMovimientosStorageService;
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
@RequestMapping("/storage/pdfMovimientos")
public class PdfMovimientosController {

    private final PdfMovimientosStorageService pdfMovimientosStorageService;

    @Autowired
    public PdfMovimientosController(PdfMovimientosStorageService pdfMovimientosStorageService) {
        this.pdfMovimientosStorageService = pdfMovimientosStorageService;
    }

    @PostMapping("/generate")
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

    @PostMapping("/generate/{guid}")
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

    @GetMapping(value = "/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename, HttpServletRequest request) {
        Resource file = pdfMovimientosStorageService.loadAsResource(filename);

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