package org.example.vivesbankproject.storage.csvProductos.services;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.cuenta.dto.tipoCuenta.TipoCuentaRequest;
import org.example.vivesbankproject.cuenta.mappers.TipoCuentaMapper;
import org.example.vivesbankproject.cuenta.models.TipoCuenta;
import org.example.vivesbankproject.cuenta.repositories.TipoCuentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CsvProductosStorageServiceImpl implements CsvProductosStorageService{
    private final Path rootLocation;
    private final TipoCuentaRepository tipoCuentaRepository;
    private final TipoCuentaMapper tipoCuentaMapper;

    @Autowired
    public CsvProductosStorageServiceImpl(
            @Value("${upload.root-location}") String path,
            TipoCuentaRepository tipoCuentaRepository, TipoCuentaMapper tipoCuentaMapper
    ) {
        this.rootLocation = Paths.get(path);
        this.tipoCuentaRepository = tipoCuentaRepository;
        this.tipoCuentaMapper = tipoCuentaMapper;
    }

    @Transactional
    @Override
    public List<TipoCuenta> importTiposCuentaFromCsv(MultipartFile file) {
        List<TipoCuenta> tiposCuenta = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            // Saltar la fila de encabezado
            reader.readNext();

            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                try {
                    // Convertir la línea de datos CSV a un TipoCuentaRequest
                    TipoCuentaRequest tipoCuentaRequest = convertToTipoCuentaRequest(nextLine);

                    // Usar el mapper para convertir el TipoCuentaRequest en TipoCuenta
                    TipoCuenta tipoCuenta = tipoCuentaMapper.toTipoCuenta(tipoCuentaRequest);

                    // Comprobar si ya existe
                    Optional<TipoCuenta> existingTipoCuenta = tipoCuentaRepository.findByNombre(tipoCuenta.getNombre());
                    if (existingTipoCuenta.isEmpty()) {
                        tiposCuenta.add(tipoCuenta);
                    } else {
                        log.warn("Tipo de cuenta ya existe: {}", tipoCuenta.getNombre());
                    }
                } catch (Exception e) {
                    log.error("Error procesando línea de CSV: {}", (Object) nextLine, e);
                }
            }

            // Guardar todos los nuevos tipos de cuenta
            return tipoCuentaRepository.saveAll(tiposCuenta);
        } catch (IOException | CsvValidationException e) {
            log.error("Error importando CSV: ", e);
            throw new RuntimeException("Error importando archivo CSV", e);
        }
    }

    @Override
    public TipoCuentaRequest convertToTipoCuentaRequest(String[] data) {
        if (data == null || data.length < 2) {
            throw new IllegalArgumentException("Datos de CSV inválidos para tipo de cuenta");
        }

        return TipoCuentaRequest.builder()
                .nombre(data[0].trim())
                .interes(new BigDecimal(data[1].trim()))
                .build();
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("No se puede inicializar el almacenamiento", e);
        }
    }

    @Override
    public String storeImportedCsv(MultipartFile file) {
        String storedFilename = "tipos_cuenta_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".csv";
        Path csvFilePath = this.rootLocation.resolve(storedFilename);

        try {
            Files.copy(file.getInputStream(), csvFilePath);
            log.info("Archivo CSV almacenado: " + storedFilename);
            return storedFilename;
        } catch (IOException e) {
            throw new RuntimeException("Fallo al almacenar el archivo CSV: " + e);
        }
    }

    @Override
    public void delete(String filename) {
        try {
            log.info("Eliminando fichero " + filename);
            Path file = rootLocation.resolve(filename);
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new RuntimeException("No se puede eliminar el fichero " + filename, e);
        }
    }
}