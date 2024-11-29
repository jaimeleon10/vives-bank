package org.example.vivesbankproject.storage.jsonClientes.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.storage.exceptions.StorageInternal;
import org.example.vivesbankproject.storage.jsonClientes.services.JsonClientesStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@Slf4j
@RequestMapping("/storage/jsonClientes")
public class JsonClientesController {

    private final JsonClientesStorageService jsonClientesStorageService;

    @Autowired
    public JsonClientesController(JsonClientesStorageService jsonClientesStorageService) {
        this.jsonClientesStorageService = jsonClientesStorageService;
    }

    @PostMapping("/generate")
    public ResponseEntity<String> generateClientesJson() {
        try {
            String storedFilename = jsonClientesStorageService.storeAll();
            return ResponseEntity.ok("Archivo JSON de clientes generado con éxito: " + storedFilename);
        } catch (StorageInternal e) {
            log.error("Error al generar el archivo JSON de clientes: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al generar el archivo JSON de clientes.");
        }
    }

    @PostMapping("/generate/{guid}")
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

    @GetMapping(value = "/{filename:.+}")
    @ResponseBody
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

    @GetMapping("/list")
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