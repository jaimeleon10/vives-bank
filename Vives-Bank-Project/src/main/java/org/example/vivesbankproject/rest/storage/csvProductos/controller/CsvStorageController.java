package org.example.vivesbankproject.rest.storage.csvProductos.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.example.vivesbankproject.rest.storage.csvProductos.services.CsvStorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Controlador para gestionar operaciones relacionadas con la importación de archivos CSV.
 * Proporciona un endpoint para importar archivos CSV utilizando el servicio asociado.
 *
 * @author Jaime León, Natalia González, Germán Fernández, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@RestController
@RequestMapping("/storage/csvProductos")
@RequiredArgsConstructor
public class CsvStorageController {

    private final CsvStorageService csvStorageService;

    /**
     * Endpoint para importar archivos CSV.
     * Recibe un archivo CSV a través de un formulario multipart.
     * Si el archivo es válido, se procesa mediante el servicio `csvStorageService`.
     *
     * @param file Archivo CSV que se enviará para su procesamiento.
     * @return ResponseEntity con un mensaje indicando el resultado de la operación.
     */
    @PostMapping("/import")
    @Operation(summary = "Importar archivo CSV",
            description = "Procesa un archivo CSV enviado a través de un formulario para importar sus datos.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Archivo CSV importado exitosamente."),
                    @ApiResponse(responseCode = "400", description = "Error en el archivo CSV."),
                    @ApiResponse(responseCode = "500", description = "Error interno al procesar el archivo.")
            })
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