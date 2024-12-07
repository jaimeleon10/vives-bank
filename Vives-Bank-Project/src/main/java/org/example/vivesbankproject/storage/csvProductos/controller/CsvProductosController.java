package org.example.vivesbankproject.storage.csvProductos.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.cuenta.models.TipoCuenta;
import org.example.vivesbankproject.storage.csvProductos.services.CsvProductosStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/storage/csvProductos")
public class CsvProductosController {
    private final CsvProductosStorageService csvProductosStorageService;

    @Autowired
    public CsvProductosController(CsvProductosStorageService csvProductosStorageService) {
        this.csvProductosStorageService = csvProductosStorageService;
    }

    @PostMapping(value = "/import", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> importTiposCuentaCsv(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("El archivo CSV está vacío");
            }

            if (!file.getOriginalFilename().toLowerCase().endsWith(".csv")) {
                return ResponseEntity.badRequest().body("Por favor, suba un archivo CSV válido");
            }

            String storedFilename = csvProductosStorageService.storeImportedCsv(file);

            List<TipoCuenta> importedTiposCuenta = csvProductosStorageService.importTiposCuentaFromCsv(file);

            return ResponseEntity.ok(Map.of(
                    "message", String.format("Importación exitosa. %d tipos de cuenta importados.", importedTiposCuenta.size()),
                    "storedFilename", storedFilename,
                    "importedTiposCuenta", importedTiposCuenta
            ));
        } catch (Exception e) {
            log.error("Error en la importación de CSV", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al importar el archivo CSV: " + e.getMessage());
        }
    }
}