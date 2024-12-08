package org.example.vivesbankproject.storage.csvProductos.controller;

import lombok.RequiredArgsConstructor;
import org.example.vivesbankproject.storage.csvProductos.services.CsvStorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/storage/csvProductos")
@RequiredArgsConstructor
public class CsvStorageController {

    private final CsvStorageService csvStorageService;

    @PostMapping("/import")
    public ResponseEntity<String> importCsv(@RequestParam("file") MultipartFile file) {
        try {
            csvStorageService.importCsv(file);
            return ResponseEntity.ok("Archivo CSV importado exitosamente.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error en el archivo CSV: " + e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al procesar el archivo: " + e.getMessage());
        }
    }
}
