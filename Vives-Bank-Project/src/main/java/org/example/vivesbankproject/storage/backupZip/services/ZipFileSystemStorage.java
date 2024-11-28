package org.example.vivesbankproject.storage.backupZip.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.cliente.dto.ClienteJson;
import org.example.vivesbankproject.cliente.models.Cliente;
import org.example.vivesbankproject.cliente.repositories.ClienteRepository;
import org.example.vivesbankproject.cliente.service.ClienteService;
import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaResponse;
import org.example.vivesbankproject.cuenta.models.Cuenta;
import org.example.vivesbankproject.storage.exceptions.StorageNotFound;
import org.example.vivesbankproject.storage.exceptions.StorageInternal;
import org.example.vivesbankproject.tarjeta.models.Tarjeta;
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
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

@Service
@Slf4j
public class ZipFileSystemStorage implements ZipStorageService {
    private final Path rootLocation;
    private final ClienteRepository clienteRepository;
    private ClienteService clienteService;

    public ZipFileSystemStorage(@Value("${upload.root-location}") String path, ClienteRepository clienteRepository, ClienteService clienteService) {
        this.rootLocation = Paths.get(path);
        this.clienteRepository = clienteRepository;
        this.clienteService = clienteService;
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
            try (FileOutputStream fos = new FileOutputStream(zipPath.toFile());
                 ZipOutputStream zos = new ZipOutputStream(fos)) {

                Path dataDir = Paths.get("data");

                if (!Files.exists(dataDir)) {
                    throw new StorageNotFound("La carpeta 'data' no existe.");
                }

                Files.walk(dataDir)
                        .filter(file -> !Files.isDirectory(file) && !file.toString().endsWith(".zip")) // Filtrar archivos que no sean .zip
                        .forEach(file -> {
                            try {
                                ZipEntry zipEntry = new ZipEntry(file.getFileName().toString());
                                zos.putNextEntry(zipEntry);

                                Files.copy(file, zos);
                                zos.closeEntry();

                                log.info("Archivo agregado al ZIP: " + file.getFileName());
                            } catch (IOException e) {
                                log.error("Error al agregar archivo al ZIP: " + file.getFileName(), e);
                            }
                        });

                log.info("Todos los archivos de 'data' (excepto .zip) se han agregado al archivo ZIP.");

                return storedFilename;

            }

        } catch (IOException e) {
            throw new StorageInternal("Error al crear archivo ZIP con los archivos de 'data': " + e.getMessage());
        }
    }

    @Override
    public void loadFromZip(File filename) {
        Path jsonFilePath = this.rootLocation.resolve(filename.toPath());

        try {
            if (!Files.exists(jsonFilePath)) {
                throw new StorageNotFound("El archivo JSON no existe: " + filename);
            }

            String jsonData = new String(Files.readAllBytes(jsonFilePath));

            ObjectMapper objectMapper = new ObjectMapper();
            JavaTimeModule module = new JavaTimeModule();
            module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            objectMapper.registerModule(module);

            List<ClienteJson> clienteMap = objectMapper.readValue(jsonData, new TypeReference<List<ClienteJson>>() {});

            for (ClienteJson clienteJson : clienteMap) {
                Cliente cliente = new Cliente();
                cliente.setGuid(clienteJson.getGuid());
                cliente.setDni(clienteJson.getDni());
                cliente.setNombre(clienteJson.getNombre());
                cliente.setApellidos(clienteJson.getApellidos());
                cliente.setDireccion(clienteJson.getDireccion());
                cliente.setEmail(clienteJson.getEmail());
                cliente.setTelefono(clienteJson.getTelefono());
                cliente.setFotoPerfil(clienteJson.getFotoPerfil());
                cliente.setFotoDni(clienteJson.getFotoDni());
                cliente.setIsDeleted(clienteJson.getIsDeleted());

                Set<Cuenta> cuentas = new HashSet<>();
                for (CuentaResponse cuentaResponse : clienteJson.getCuentas()) {
                    Cuenta cuenta = new Cuenta();
                    cuenta.setGuid(cuentaResponse.getGuid());
                    cuenta.setIban(cuentaResponse.getIban());
                    cuenta.setSaldo(new BigDecimal(cuentaResponse.getSaldo()));
                    cuenta.setCreatedAt(LocalDateTime.parse(cuentaResponse.getCreatedAt(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    cuenta.setUpdatedAt(LocalDateTime.parse(cuentaResponse.getUpdatedAt(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    cuenta.setIsDeleted(cuentaResponse.getIsDeleted());

                    cuenta.setCliente(cliente);

                    Tarjeta tarjeta = new Tarjeta();
                    tarjeta.setGuid(cuentaResponse.getTarjetaId());
                    tarjeta.setCreatedAt(LocalDateTime.parse(cuentaResponse.getCreatedAt(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    tarjeta.setUpdatedAt(LocalDateTime.parse(cuentaResponse.getUpdatedAt(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    tarjeta.setIsDeleted(cuentaResponse.getIsDeleted());

                    cuenta.setTarjeta(tarjeta);

                    cuentas.add(cuenta);
                }

                cliente.setCuentas(cuentas);

                clienteRepository.save(cliente);
            }

            log.info("Archivo JSON con clientes importado: " + filename);

        } catch (IOException e) {
            throw new StorageInternal("Fallo al leer el archivo JSON de clientes: " + e);
        }
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