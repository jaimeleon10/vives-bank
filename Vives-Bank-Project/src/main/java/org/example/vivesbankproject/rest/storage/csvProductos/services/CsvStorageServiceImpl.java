package org.example.vivesbankproject.rest.storage.csvProductos.services;

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

@Service
@RequiredArgsConstructor
@Slf4j
public class CsvStorageServiceImpl implements CsvStorageService {

    private final TipoCuentaService tipoCuentaService;

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
                    continue; // Con esto saltamos a la siguiente linea del csv
                }

                String[] data = line.split(",");

                if (data.length != 2) {
                    log.error("Línea con formato incorrecto: {}", line);
                    continue; // Con esto saltamos a la siguiente linea del csv
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