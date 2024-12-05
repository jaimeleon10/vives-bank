package org.example.vivesbankproject.storage.csvProductos.services;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.extern.slf4j.Slf4j;
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

@Service
@Slf4j
public class CsvProductosStorageServiceImpl implements CsvProductosStorageService{
    private final Path rootLocation;
    private final TipoCuentaRepository tipoCuentaRepository;

    @Autowired
    public CsvProductosStorageServiceImpl(
            @Value("${upload.root-location}") String path,
            TipoCuentaRepository tipoCuentaRepository
    ) {
        this.rootLocation = Paths.get(path);
        this.tipoCuentaRepository = tipoCuentaRepository;
    }

    @Transactional
    @Override
    public List<TipoCuenta> importTiposCuentaFromCsv(MultipartFile file) {
        List<TipoCuenta> tiposCuenta = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] header = reader.readNext();

            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                TipoCuenta tipoCuenta = convertToTipoCuenta(nextLine);
                tiposCuenta.add(tipoCuenta);
            }

            return tipoCuentaRepository.saveAll(tiposCuenta);
        } catch (IOException | CsvValidationException e) {
            log.error("Error importing CSV: ", e);
            throw new RuntimeException("Error importing CSV file", e);
        }
    }

    @Override
    public TipoCuenta convertToTipoCuenta(String[] data) {
        return TipoCuenta.builder()
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