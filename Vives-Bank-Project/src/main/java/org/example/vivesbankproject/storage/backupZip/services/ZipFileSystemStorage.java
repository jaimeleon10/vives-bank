package org.example.vivesbankproject.storage.backupZip.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.cliente.models.Cliente;
import org.example.vivesbankproject.cliente.repositories.ClienteRepository;
import org.example.vivesbankproject.storage.exceptions.StorageNotFound;
import org.example.vivesbankproject.storage.exceptions.StorageInternal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

@Service
@Slf4j
public class ZipFileSystemStorage implements ZipStorageService {
    private final Path rootLocation;
    private final ClienteRepository clienteRepository;

    public ZipFileSystemStorage(@Value("${upload.root-location}") String path, ClienteRepository clienteRepository) {
        this.rootLocation = Paths.get(path);
        this.clienteRepository = clienteRepository;
    }

    @Override
    public void init() {
        log.info("Inicializando almacenamiento de ZIP");
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new StorageInternal("No se puede inicializar el almacenamiento ZIP" + e);
        }
    }

    @Override
    public String export() {
        String storedFilename = "clientes.zip";
        Path zipPath = this.rootLocation.resolve(storedFilename);

        try {
            List<Cliente> clientes = clienteRepository.findAll();

            ObjectMapper objectMapper = new ObjectMapper();
            JavaTimeModule module = new JavaTimeModule();
            module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            objectMapper.registerModule(module);

            objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            String clientesJson = objectMapper.writeValueAsString(clientes);

            try (FileOutputStream fos = new FileOutputStream(zipPath.toFile());
                 ZipOutputStream zos = new ZipOutputStream(fos)) {

                ZipEntry zipEntry = new ZipEntry("clientes" + System.currentTimeMillis() + ".json");
                zos.putNextEntry(zipEntry);

                zos.write(clientesJson.getBytes());
                zos.closeEntry();
            }

            log.info("Archivo ZIP con JSON de clientes creado y almacenado: " + storedFilename);
            return storedFilename;

        } catch (IOException e) {
            throw new StorageInternal("Error al crear archivo ZIP para clientes: " + e.getMessage());
        }
    }

    @Override
    public List<Object> loadFromZip(File fileToUnzip) {
        log.info("Importando desde ZIP " + fileToUnzip);

        Path dataDir = Paths.get("data");

        if (!Files.exists(dataDir)) {
            throw new StorageNotFound("El directorio 'data' no existe.");
        }

        List<Object> listado = new ArrayList<>();

        try (ZipFile zipFile = new ZipFile(fileToUnzip)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                log.info("Archivo encontrado en el ZIP: {}", entry.getName());

                try (InputStream inputStream = zipFile.getInputStream(entry)) {
                    if (!entry.isDirectory()) {
                        Path outputPath = dataDir.resolve(entry.getName());
                        Files.createDirectories(outputPath.getParent());
                        Files.copy(inputStream, outputPath, StandardCopyOption.REPLACE_EXISTING);

                        if (entry.getName().endsWith(".json")) {
                            File jsonFile = outputPath.toFile();
                            List<Object> dataProductos = loadJson(jsonFile);
                            listado.addAll(dataProductos);
                        }
                    }
                }
            }

        } catch (IOException e) {
            log.error("Error al procesar el archivo ZIP: ", e);
            throw new StorageNotFound("Error al procesar el archivo ZIP: " + e.getMessage());
        }

        if (listado.isEmpty()) {
            throw new StorageNotFound("No se encontraron archivos JSON dentro del archivo ZIP.");
        }

        return listado;
    }

    @Override
    public List<Object> loadJson(File jsonFile) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(jsonFile, List.class);
        } catch (IOException e) {
            log.error("Error al deserializar el archivo JSON: ", e);
            throw new StorageNotFound("Error al deserializar el archivo JSON: " + e.getMessage());
        }
    }

    @Override
    public Path load(String filename) {
        log.info("Cargando fichero ZIP " + filename);
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        log.info("Cargando fichero ZIP " + filename);
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageNotFound("No se puede leer fichero ZIP: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new StorageNotFound("No se puede leer fichero ZIP: " + filename + " " + e);
        }
    }

    @Override
    public void delete(String filename) {
        String justFilename = StringUtils.getFilename(filename);
        try {
            log.info("Eliminando fichero ZIP" + filename);
            Path file = load(justFilename);
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new StorageInternal("No se puede eliminar el fichero ZIP " + filename + " " + e);
        }
    }
}