package org.example.vivesbankproject.rest.storage.jsonClientes.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.rest.cliente.dto.ClienteJson;
import org.example.vivesbankproject.rest.cliente.models.Cliente;
import org.example.vivesbankproject.rest.cliente.repositories.ClienteRepository;
import org.example.vivesbankproject.rest.cuenta.dto.cuenta.CuentaResponse;
import org.example.vivesbankproject.rest.cuenta.mappers.CuentaMapper;
import org.example.vivesbankproject.rest.storage.exceptions.StorageInternal;
import org.example.vivesbankproject.rest.storage.exceptions.StorageNotFound;
import org.example.vivesbankproject.rest.tarjeta.dto.TarjetaResponse;
import org.example.vivesbankproject.rest.tarjeta.mappers.TarjetaMapper;
import org.example.vivesbankproject.rest.tarjeta.models.Tarjeta;
import org.example.vivesbankproject.rest.users.mappers.UserMapper;
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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class JsonClientesFileSystemStorage implements JsonClientesStorageService {

    private final Path rootLocation;
    private final ClienteRepository clienteRepository;
    private final TarjetaMapper tarjetaMapper;
    private final CuentaMapper cuentaMapper;
    private final UserMapper userMapper;

    @Autowired
    public JsonClientesFileSystemStorage(@Value("${upload.root-location}") String path, ClienteRepository clienteRepository, TarjetaMapper tarjetaMapper, CuentaMapper cuentaMapper, UserMapper userMapper) {
        this.rootLocation = Paths.get(path);
        this.clienteRepository = clienteRepository;
        this.tarjetaMapper = tarjetaMapper;
        this.cuentaMapper = cuentaMapper;
        this.userMapper = userMapper;
    }

    @Override
    public String store(String guid) {
        String storedFilename = "clientes_" + guid + "_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".json";
        Path jsonFilePath = this.rootLocation.resolve(storedFilename);

        try {

            if (Files.exists(jsonFilePath)) {
                Files.delete(jsonFilePath);
                log.info("Archivo existente eliminado: {}", storedFilename);
            }

            Optional<Cliente> clientes = clienteRepository.findByGuid(guid);

            List<ClienteJson> clienteMap = clientes.stream()
                    .map(cliente -> {
                        ClienteJson clienteJson = new ClienteJson();
                        clienteJson.setGuid(cliente.getGuid());
                        clienteJson.setDni(cliente.getDni());
                        clienteJson.setNombre(cliente.getNombre());
                        clienteJson.setApellidos(cliente.getApellidos());
                        clienteJson.setDireccion(cliente.getDireccion());
                        clienteJson.setEmail(cliente.getEmail());
                        clienteJson.setTelefono(cliente.getTelefono());
                        clienteJson.setFotoPerfil(cliente.getFotoPerfil());
                        clienteJson.setFotoDni(cliente.getFotoDni());

                        Set<TarjetaResponse> tarjetasResponse = new HashSet<>(Set.of());
                        Set<CuentaResponse> cuentasResponse = cliente.getCuentas().stream()
                                .map(cuenta -> {
                                    var cuentaResponse = cuentaMapper.toCuentaResponse(cuenta, cuenta.getTipoCuenta().getGuid(), cuenta.getTarjeta().getGuid(), cuenta.getCliente().getGuid());

                                    Tarjeta tarjeta = cuenta.getTarjeta();
                                    if (tarjeta != null) {
                                        var tarjetaResponse = tarjetaMapper.toTarjetaResponse(tarjeta);
                                        tarjetasResponse.add(tarjetaResponse);
                                    }

                                    return cuentaResponse;
                                })
                                .collect(Collectors.toSet());

                        clienteJson.setCuentas(cuentasResponse);
                        clienteJson.setTarjetas(tarjetasResponse);
                        clienteJson.setUsuario(userMapper.toUserResponse(cliente.getUser()));
                        clienteJson.setCreatedAt(cliente.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                        clienteJson.setUpdatedAt(cliente.getUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                        clienteJson.setIsDeleted(cliente.getIsDeleted());

                        return clienteJson;
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
