package org.example.vivesbankproject.storage.jsonClientesAdmin.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.cliente.dto.ClienteJson;
import org.example.vivesbankproject.cliente.models.Cliente;
import org.example.vivesbankproject.cliente.repositories.ClienteRepository;
import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaResponse;
import org.example.vivesbankproject.cuenta.models.Cuenta;
import org.example.vivesbankproject.storage.exceptions.StorageInternal;
import org.example.vivesbankproject.storage.exceptions.StorageNotFound;
import org.example.vivesbankproject.storage.jsonClientes.services.JsonClientesStorageService;
import org.example.vivesbankproject.tarjeta.dto.TarjetaResponse;
import org.example.vivesbankproject.tarjeta.models.Tarjeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class JsonClientesAdminFileSystemStorage implements JsonClientesAdminStorageService {

    private final Path rootLocation;
    private final ClienteRepository clienteRepository;

    @Autowired
    public JsonClientesAdminFileSystemStorage(@Value("${upload.root-location-2}") String path, ClienteRepository clienteRepository) {
        this.rootLocation = Paths.get(path);
        this.clienteRepository = clienteRepository;
    }

    @Override
    public String storeAll() {
        String storedFilename = "admin_clientes_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".json";
        Path jsonFilePath = this.rootLocation.resolve(storedFilename);

        try {
            List<Cliente> clientes = clienteRepository.findAll();

            List<Cliente> clienteMap = clientes.stream()
                .map(cliente -> {
                    Cliente cliente1 = new Cliente();
                    cliente1.setGuid(cliente.getGuid());
                    cliente1.setDni(cliente.getDni());
                    cliente1.setNombre(cliente.getNombre());
                    cliente1.setApellidos(cliente.getApellidos());
                    cliente1.setDireccion(cliente.getDireccion());
                    cliente1.setEmail(cliente.getEmail());
                    cliente1.setUser(cliente.getUser());
                    cliente1.setTelefono(cliente.getTelefono());
                    cliente1.setFotoPerfil(cliente.getFotoPerfil());
                    cliente1.setFotoDni(cliente.getFotoDni());

                    Set<Cuenta> cuentas = cliente.getCuentas().stream()
                            .map(cuenta -> {
                                Cuenta cuenta1 = new Cuenta();
                                cuenta1.setGuid(cuenta.getGuid());
                                cuenta1.setIban(cuenta.getIban());
                                cuenta1.setSaldo(cuenta.getSaldo());
                                cuenta1.setTipoCuenta(cuenta.getTipoCuenta());

                                Tarjeta tarjeta = cuenta.getTarjeta();
                                if (tarjeta != null) {
                                    Tarjeta tarjeta1 = new Tarjeta();
                                    tarjeta1.setGuid(tarjeta.getGuid());
                                    tarjeta1.setNumeroTarjeta(tarjeta.getNumeroTarjeta());
                                    tarjeta1.setLimiteDiario(tarjeta.getLimiteDiario());
                                    tarjeta1.setLimiteSemanal(tarjeta.getLimiteSemanal());
                                    tarjeta1.setLimiteMensual(tarjeta.getLimiteMensual());
                                    tarjeta1.setTipoTarjeta(tarjeta.getTipoTarjeta());
                                    tarjeta1.setCreatedAt(tarjeta.getCreatedAt());
                                    tarjeta1.setUpdatedAt(tarjeta.getUpdatedAt());
                                    tarjeta1.setIsDeleted(tarjeta.getIsDeleted());
                                    cuenta1.setTarjeta(tarjeta);
                                }

                                cuenta1.setCliente(cuenta.getCliente());
                                cuenta1.setCreatedAt(cuenta.getCreatedAt());
                                cuenta1.setUpdatedAt(cuenta.getUpdatedAt());
                                cuenta1.setIsDeleted(cuenta.getIsDeleted());
                                return cuenta1;
                            })
                            .collect(Collectors.toSet());

                    cliente1.setCuentas(cuentas);

                    cliente1.setCreatedAt(cliente.getCreatedAt());
                    cliente1.setUpdatedAt(cliente.getUpdatedAt());
                    cliente1.setIsDeleted(cliente.getIsDeleted());

                    return cliente1;
                })
                .collect(Collectors.toList());

            ObjectMapper objectMapper = new ObjectMapper();
            JavaTimeModule module = new JavaTimeModule();
            objectMapper.registerModule(module);

            objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

            String jsonData = objectMapper.writeValueAsString(clienteMap);

            Files.write(jsonFilePath, jsonData.getBytes());

            log.info("Archivo JSON con clientes almacenado: " + storedFilename);

            return storedFilename;
        } catch (IOException e) {
            throw new StorageInternal("Fallo al almacenar el archivo JSON de clientes: " + e);
        }
    }

    @Override
    public String store(String guid) {
        String storedFilename = "admin_clientes_" + guid + "_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".json";
        Path jsonFilePath = this.rootLocation.resolve(storedFilename);

        try {
            Optional<Cliente> clientes = clienteRepository.findByGuid(guid);

            List<Cliente> clienteMap = clientes.stream()
                    .map(cliente -> {
                        Cliente cliente1 = new Cliente();
                        cliente1.setGuid(cliente.getGuid());
                        cliente1.setDni(cliente.getDni());
                        cliente1.setNombre(cliente.getNombre());
                        cliente1.setApellidos(cliente.getApellidos());
                        cliente1.setDireccion(cliente.getDireccion());
                        cliente1.setEmail(cliente.getEmail());
                        cliente1.setTelefono(cliente.getTelefono());
                        cliente1.setFotoPerfil(cliente.getFotoPerfil());
                        cliente1.setFotoDni(cliente.getFotoDni());

                        Set<Cuenta> cuentas = cliente.getCuentas().stream()
                                .map(cuenta -> {
                                    Cuenta cuenta1 = new Cuenta();
                                    cuenta1.setGuid(cuenta.getGuid());
                                    cuenta1.setIban(cuenta.getIban());
                                    cuenta1.setSaldo(cuenta.getSaldo());
                                    cuenta1.setTipoCuenta(cuenta.getTipoCuenta());

                                    Tarjeta tarjeta = cuenta.getTarjeta();
                                    if (tarjeta != null) {
                                        TarjetaResponse tarjetaResponse = new TarjetaResponse();
                                        tarjetaResponse.setGuid(tarjeta.getGuid());
                                        tarjetaResponse.setNumeroTarjeta(tarjeta.getNumeroTarjeta());
                                        tarjetaResponse.setLimiteDiario(String.valueOf(tarjeta.getLimiteDiario()));
                                        tarjetaResponse.setLimiteSemanal(String.valueOf(tarjeta.getLimiteSemanal()));
                                        tarjetaResponse.setLimiteMensual(String.valueOf(tarjeta.getLimiteMensual()));
                                        tarjetaResponse.setTipoTarjeta(tarjeta.getTipoTarjeta());
                                        tarjetaResponse.setCreatedAt(tarjeta.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                                        tarjetaResponse.setUpdatedAt(tarjeta.getUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                                        tarjetaResponse.setIsDeleted(tarjeta.getIsDeleted());
                                        cuenta1.setTarjeta(tarjeta);
                                    }

                                    cuenta1.setCliente(cuenta.getCliente());
                                    cuenta1.setCreatedAt(cuenta.getCreatedAt());
                                    cuenta1.setUpdatedAt(cuenta.getUpdatedAt());
                                    cuenta1.setIsDeleted(cuenta.getIsDeleted());
                                    return cuenta1;
                                })
                                .collect(Collectors.toSet());

                        cliente1.setCuentas(cuentas);

                        cliente1.setCreatedAt(LocalDateTime.parse(cliente.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
                        cliente1.setUpdatedAt(LocalDateTime.parse(cliente.getUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
                        cliente1.setIsDeleted(cliente.getIsDeleted());

                        return cliente1;
                    })
                    .collect(Collectors.toList());

            ObjectMapper objectMapper = new ObjectMapper();
            JavaTimeModule module = new JavaTimeModule();
            module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            objectMapper.registerModule(module);

            objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

            String jsonData = objectMapper.writeValueAsString(clienteMap);

            Files.write(jsonFilePath, jsonData.getBytes());

            log.info("Archivo JSON con cliente almacenado: " + storedFilename);

            return storedFilename;
        } catch (IOException e) {
            throw new StorageInternal("Fallo al almacenar el archivo JSON de cliente: " + e);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        log.info("Cargando todos los ficheros almacenados");
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        } catch (IOException e) {
            throw new StorageInternal("Fallo al leer ficheros almacenados " + e);
        }
    }

    @Override
    public Path load(String filename) {
        log.info("Cargando fichero " + filename);
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        log.info("Cargando fichero " + filename);
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageNotFound("No se puede leer fichero: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new StorageNotFound("No se puede leer fichero: " + filename + " " + e);
        }
    }

    @Override
    public void init() {
        log.info("Inicializando almacenamiento");
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new StorageInternal("No se puede inicializar el almacenamiento " + e);
        }
    }

    @Override
    public void delete(String filename) {
        String justFilename = StringUtils.getFilename(filename);
        try {
            log.info("Eliminando fichero " + filename);
            Path file = load(justFilename);
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new StorageInternal("No se puede eliminar el fichero " + filename + " " + e);
        }
    }
}
