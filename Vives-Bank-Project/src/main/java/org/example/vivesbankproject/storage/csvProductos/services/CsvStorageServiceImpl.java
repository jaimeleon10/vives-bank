package org.example.vivesbankproject.storage.csvProductos.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.cuenta.dto.tipoCuenta.TipoCuentaRequest;
import org.example.vivesbankproject.cuenta.services.TipoCuentaService;
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
                // Omitir la primera línea si es el encabezado
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] data = line.split(",");

                // Validar que los datos sean correctos
                if (data.length != 2) {
                    throw new IllegalArgumentException("El archivo CSV tiene un formato incorrecto.");
                }

                // Crear el objeto TipoCuentaRequest
                TipoCuentaRequest tipoCuentaRequest = TipoCuentaRequest.builder()
                        .nombre(data[0].trim())
                        .interes(new BigDecimal(data[1].trim()))
                        .build();

                tipoCuentaService.save(tipoCuentaRequest);
            }
        } catch (IOException e) {
            throw new IOException("Error al leer el archivo CSV.", e);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El archivo CSV contiene datos inválidos en la columna 'interes':", e);
        }
    }
}