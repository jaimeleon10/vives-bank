package org.example.vivesbankproject.storage.backupZip.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.cliente.dto.ClienteJsonAdmin;
import org.example.vivesbankproject.cliente.models.Cliente;
import org.example.vivesbankproject.cliente.repositories.ClienteRepository;
import org.example.vivesbankproject.cuenta.models.Cuenta;
import org.example.vivesbankproject.cuenta.repositories.CuentaRepository;
import org.example.vivesbankproject.movimientos.models.Movimiento;
import org.example.vivesbankproject.movimientos.repositories.MovimientosRepository;
import org.example.vivesbankproject.storage.exceptions.StorageNotFound;
import org.example.vivesbankproject.storage.exceptions.StorageInternal;
import org.example.vivesbankproject.tarjeta.models.Tarjeta;
import org.example.vivesbankproject.tarjeta.repositories.TarjetaRepository;
import org.example.vivesbankproject.users.models.User;
import org.example.vivesbankproject.users.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Service
@Slf4j
public class ZipFileSystemStorage implements ZipStorageService {
    private final Path rootLocation;
    private final ClienteRepository clienteRepository;
    private final UserRepository userRepository;
    private final TarjetaRepository tarjetaRepository;
    private final CuentaRepository cuentaRepository;
    private MovimientosRepository movimientosRepository;

    public ZipFileSystemStorage(@Value("${upload.root-location-2}") String path, ClienteRepository clienteRepository, MovimientosRepository movimientosRepository, UserRepository userRepository, TarjetaRepository tarjetaRepository, CuentaRepository cuentaRepository) {
        this.rootLocation = Paths.get(path);
        this.clienteRepository = clienteRepository;
        this.movimientosRepository = movimientosRepository;
        this.userRepository = userRepository;
        this.tarjetaRepository = tarjetaRepository;
        this.cuentaRepository = cuentaRepository;
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

                Path dataDir = Paths.get("dataAdmin");

                if (!Files.exists(dataDir)) {
                    throw new StorageNotFound("La carpeta 'dataAdmin' no existe.");
                }

                Files.walk(dataDir)
                        .filter(file -> !Files.isDirectory(file) && !file.toString().endsWith(".zip"))
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
        log.warn("inicio");
        Path zipFilePath = this.rootLocation.resolve(filename.toPath());

        try {
            if (!Files.exists(zipFilePath)) {
                throw new StorageNotFound("El archivo ZIP no existe: " + filename);
            }

            try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipFilePath))) {
                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    if (entry.getName().endsWith(".json")) {
                        String jsonData = new String(zis.readAllBytes());

                        ObjectMapper objectMapper = new ObjectMapper();
                        JavaTimeModule module = new JavaTimeModule();
                        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                        objectMapper.registerModule(module);

                        List<ClienteJsonAdmin> clienteMap = objectMapper.readValue(jsonData, new TypeReference<>() {});

                        for (ClienteJsonAdmin clienteJsonAdmin : clienteMap) {
                            Cliente cliente = Cliente.builder()
                                    .id(clienteJsonAdmin.getId())
                                    .guid(clienteJsonAdmin.getGuid())
                                    .dni(clienteJsonAdmin.getDni())
                                    .nombre(clienteJsonAdmin.getNombre())
                                    .apellidos(clienteJsonAdmin.getApellidos())
                                    .direccion(clienteJsonAdmin.getDireccion())
                                    .email(clienteJsonAdmin.getEmail())
                                    .telefono(clienteJsonAdmin.getTelefono())
                                    .fotoPerfil(clienteJsonAdmin.getFotoPerfil())
                                    .fotoDni(clienteJsonAdmin.getFotoDni())
                                    .user(clienteJsonAdmin.getUser())
                                    .cuentas(clienteJsonAdmin.getCuentas())
                                    .isDeleted(clienteJsonAdmin.getIsDeleted())
                                    .build();

                            cliente.getCuentas().forEach(cuenta -> {
                                if (tarjetaRepository.findByGuid(cuenta.getTarjeta().getGuid()).isEmpty()) {
                                    tarjetaRepository.save(cuenta.getTarjeta());
                                }
                            });
                            if (userRepository.findByGuid(cliente.getUser().getGuid()).isEmpty()) {
                                userRepository.save(cliente.getUser());
                            }
                            if (clienteRepository.findByGuid(cliente.getGuid()).isEmpty()) {
                                Cliente cliente1 = cliente;
                                cliente1.setCuentas(Set.of());
                                clienteRepository.save(cliente1);
                            }
                            cliente.getCuentas().forEach(cuenta -> {
                                if (cuentaRepository.findByGuid(cuenta.getGuid()).isEmpty()) {
                                    cuentaRepository.save(cuenta);
                                }
                            });
                        }

                        List<Movimiento> movimientos = movimientosRepository.findAll();
                        for (Movimiento movimiento : movimientos) {
                            Movimiento nuevoMovimiento = new Movimiento();
                            nuevoMovimiento.setGuid(movimiento.getGuid());
                            nuevoMovimiento.setClienteGuid(movimiento.getClienteGuid());
                            nuevoMovimiento.setCreatedAt(movimiento.getCreatedAt());
                            nuevoMovimiento.setIsDeleted(movimiento.getIsDeleted());

                            if (movimiento.getDomiciliacion() != null) {
                                nuevoMovimiento.setDomiciliacion(movimiento.getDomiciliacion());
                            }
                            if (movimiento.getIngresoDeNomina() != null) {
                                nuevoMovimiento.setIngresoDeNomina(movimiento.getIngresoDeNomina());
                            }
                            if (movimiento.getPagoConTarjeta() != null) {
                                nuevoMovimiento.setPagoConTarjeta(movimiento.getPagoConTarjeta());
                            }
                            if (movimiento.getTransferencia() != null) {
                                nuevoMovimiento.setTransferencia(movimiento.getTransferencia());
                            }

                            movimientosRepository.save(nuevoMovimiento);
                        }

                        log.info("Archivo JSON del ZIP procesado correctamente.");
                    }
                }
            }
        } catch (IOException e) {
            throw new StorageInternal("Fallo al procesar el archivo ZIP: " + e);
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