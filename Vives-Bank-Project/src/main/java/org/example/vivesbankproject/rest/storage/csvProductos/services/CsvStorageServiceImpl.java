package org.example.vivesbankproject.rest.storage.csvProductos.services;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.rest.cuenta.dto.tipoCuenta.TipoCuentaRequest;
import org.example.vivesbankproject.rest.cuenta.exceptions.tipoCuenta.TipoCuentaExists;
import org.example.vivesbankproject.rest.cuenta.services.TipoCuentaService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Objects;


/**
 * Implementación del servicio para el procesamiento y almacenamiento de datos de archivos CSV.
 * Esta clase se encarga de leer los archivos CSV, procesar sus datos y pasarlos al servicio
 * de negocio para almacenamiento.
 *
 * @author Jaime León, Natalia González, Germán Fernández, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CsvStorageServiceImpl implements CsvStorageService {

    private final TipoCuentaService tipoCuentaService;

    @Override
    @Operation(
            summary = "Importar datos desde un archivo CSV",
            description = "Procesa un archivo CSV para leer y guardar datos en la base de datos.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Archivo CSV procesado y datos guardados correctamente",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Formato de archivo inválido o error en los datos",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor al procesar el archivo",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    public void importCsv(MultipartFile file) throws IOException {
        // Validamos la extensión del archivo
        if (!Objects.requireNonNull(file.getOriginalFilename()).endsWith(".csv")) {
            throw new IllegalArgumentException("El archivo debe tener formato CSV.");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                // Comprobamos si existe el encabezado
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Saltar la primera línea del CSV si es el encabezado
                }

                String[] data = line.split(",");

                if (data.length != 2) {
                    log.error("Línea con formato incorrecto: {}", line);
                    continue; // Saltar líneas con el formato incorrecto
                }

                try {
                    // Crear el objeto TipoCuentaRequest
                    TipoCuentaRequest tipoCuentaRequest = TipoCuentaRequest.builder()
                            .nombre(data[0].trim())
                            .interes(new BigDecimal(data[1].trim()))
                            .build();

                    // Intentar guardar el tipo de cuenta
                    tipoCuentaService.save(tipoCuentaRequest);
                    log.info("Tipo de cuenta guardado: {}", tipoCuentaRequest.getNombre());
                } catch (TipoCuentaExists e) {
                    log.warn("El tipo de cuenta '{}' ya existe.", data[0].trim());
                } catch (NumberFormatException e) {
                    log.error("Error de formato en la columna 'interes' para la línea: {}", line, e);
                } catch (Exception e) {
                    log.error("Error desconocido al procesar la línea: {}", line, e);
                }
            }
        } catch (IOException e) {
            throw new IOException("Error al leer el archivo CSV.", e);
        }
    }

}