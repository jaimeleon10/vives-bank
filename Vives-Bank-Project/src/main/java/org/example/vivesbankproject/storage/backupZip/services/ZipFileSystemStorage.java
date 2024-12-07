package org.example.vivesbankproject.storage.backupZip.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.example.vivesbankproject.cliente.dto.ClienteJsonZip;
import org.example.vivesbankproject.cliente.models.Cliente;
import org.example.vivesbankproject.cliente.repositories.ClienteRepository;
import org.example.vivesbankproject.cuenta.models.Cuenta;
import org.example.vivesbankproject.cuenta.repositories.CuentaRepository;
import org.example.vivesbankproject.movimientos.dto.MovimientoResponse;
import org.example.vivesbankproject.movimientos.models.Movimiento;
import org.example.vivesbankproject.movimientos.repositories.MovimientosRepository;
import org.example.vivesbankproject.storage.exceptions.StorageNotFound;
import org.example.vivesbankproject.storage.exceptions.StorageInternal;
import org.example.vivesbankproject.tarjeta.repositories.TarjetaRepository;
import org.example.vivesbankproject.users.mappers.UserMapper;
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
    private final MovimientosRepository movimientosRepository;
    private final UserMapper userMapper;

    public ZipFileSystemStorage(@Value("${upload.root-location-2}") String path, ClienteRepository clienteRepository, MovimientosRepository movimientosRepository, UserRepository userRepository, TarjetaRepository tarjetaRepository, CuentaRepository cuentaRepository, UserMapper userMapper) {
        this.rootLocation = Paths.get(path);
        this.clienteRepository = clienteRepository;
        this.movimientosRepository = movimientosRepository;
        this.userRepository = userRepository;
        this.tarjetaRepository = tarjetaRepository;
        this.cuentaRepository = cuentaRepository;
        this.userMapper = userMapper;
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
            if (Files.exists(zipPath)) {
                Files.delete(zipPath);
                log.info("Archivo ZIP existente eliminado: {}", storedFilename);
            }

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

                                log.info("Archivo agregado al ZIP: {}", file.getFileName());
                            } catch (IOException e) {
                                log.error("Error al agregar archivo al ZIP: {}", file.getFileName(), e);
                            }
                        });

                log.info("Todos los archivos de 'dataAdmin' (excepto .zip) se han agregado al archivo ZIP.");
                return storedFilename;

            }
        } catch (IOException e) {
            throw new StorageInternal("Error al crear archivo ZIP con los archivos de 'dataAdmin': " + e.getMessage());
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

                        try {
                            List<ClienteJsonZip> clienteMap = objectMapper.readValue(jsonData, new TypeReference<>() {});

                            for (ClienteJsonZip clienteJson : clienteMap) {

                                Set<Cuenta> cuentasVacias = new HashSet<>();

                                Cliente cliente = Cliente.builder()
                                        .id(Long.parseLong("1"))
                                        .guid(clienteJson.getGuid())
                                        .dni(clienteJson.getDni())
                                        .nombre(clienteJson.getNombre())
                                        .apellidos(clienteJson.getApellidos())
                                        .direccion(clienteJson.getDireccion())
                                        .email(clienteJson.getEmail())
                                        .telefono(clienteJson.getTelefono())
                                        .fotoPerfil(clienteJson.getFotoPerfil())
                                        .fotoDni(clienteJson.getFotoDni())
                                        .user(clienteJson.getUsuario())
                                        .cuentas(cuentasVacias)
                                        .createdAt(LocalDateTime.parse(clienteJson.getCreatedAt()))
                                        .updatedAt(LocalDateTime.parse(clienteJson.getUpdatedAt()))
                                        .isDeleted(clienteJson.getIsDeleted())
                                        .build();

                                if (userRepository.findByGuid(cliente.getUser().getGuid()).isEmpty()) {
                                    User usuario = cliente.getUser();
                                    usuario.setId(null);
                                    userRepository.save(usuario);
                                }

                                if (clienteRepository.findByGuid(cliente.getGuid()).isEmpty()) {
                                    cliente.setCuentas(Set.of());
                                    var clienteSaved = clienteRepository.save(cliente);
                                    cliente.setId(clienteSaved.getId());
                                }

                                Set<Cuenta> cuentasCliente = clienteJson.getCuentas().stream()
                                        .map(cuentaZip ->
                                                Cuenta.builder()
                                                        .id(cuentaZip.getId())
                                                        .guid(cuentaZip.getGuid())
                                                        .iban(cuentaZip.getIban())
                                                        .saldo(cuentaZip.getSaldo())
                                                        .tipoCuenta(cuentaZip.getTipoCuenta())
                                                        .tarjeta(cuentaZip.getTarjeta())
                                                        .cliente(cliente)
                                                        .createdAt(cuentaZip.getCreatedAt())
                                                        .updatedAt(cuentaZip.getUpdatedAt())
                                                        .isDeleted(cuentaZip.getIsDeleted())
                                                        .build()
                                        ).collect(Collectors.toSet());

                                cuentasCliente.forEach(cuenta -> {
                                    if (cuentaRepository.findByGuid(cuenta.getGuid()).isEmpty()) {
                                        cuentaRepository.save(cuenta);
                                    }
                                });
                            }
                        } catch (Exception e1) {
                            try {
                                List<MovimientoResponse> movimientoMap = objectMapper.readValue(jsonData, new TypeReference<>() {});

                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                                for (MovimientoResponse movimientoJson : movimientoMap) {
                                    if (movimientosRepository.findByGuid(movimientoJson.getGuid()).isEmpty()) {
                                        Movimiento nuevoMovimiento = new Movimiento();
                                        nuevoMovimiento.setId(new ObjectId());
                                        nuevoMovimiento.setGuid(movimientoJson.getGuid());
                                        nuevoMovimiento.setClienteGuid(movimientoJson.getClienteGuid());

                                        if (movimientoJson.getDomiciliacion() != null) {
                                            nuevoMovimiento.setDomiciliacion(movimientoJson.getDomiciliacion());
                                        }
                                        if (movimientoJson.getIngresoDeNomina() != null) {
                                            nuevoMovimiento.setIngresoDeNomina(movimientoJson.getIngresoDeNomina());
                                        }
                                        if (movimientoJson.getPagoConTarjeta() != null) {
                                            nuevoMovimiento.setPagoConTarjeta(movimientoJson.getPagoConTarjeta());
                                        }
                                        if (movimientoJson.getTransferencia() != null) {
                                            nuevoMovimiento.setTransferencia(movimientoJson.getTransferencia());
                                        }

                                        nuevoMovimiento.setCreatedAt(LocalDateTime.parse(movimientoJson.getCreatedAt(), formatter));
                                        nuevoMovimiento.setIsDeleted(movimientoJson.getIsDeleted());

                                        movimientosRepository.save(nuevoMovimiento);
                                    }
                                }

                            } catch (Exception e2) {
                                log.warn("El archivo JSON no corresponde a un tipo esperado.");
                                log.warn(e2.getMessage());
                            }
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