package org.example.vivesbankproject.rest.storage.jsonClientesAdmin.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.rest.cliente.dto.ClienteJsonZip;
import org.example.vivesbankproject.rest.cliente.models.Cliente;
import org.example.vivesbankproject.rest.cliente.repositories.ClienteRepository;
import org.example.vivesbankproject.rest.cuenta.dto.cuenta.CuentaZip;
import org.example.vivesbankproject.rest.storage.exceptions.StorageInternal;
import org.example.vivesbankproject.rest.storage.exceptions.StorageNotFound;
import org.example.vivesbankproject.rest.tarjeta.models.Tarjeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
/**
 * Servicio de almacenamiento de clientes utilizando el sistema de archivos.
 * Implementa la lógica para guardar, recuperar y eliminar archivos JSON que contienen información de clientes.
 *
 * @author Jaime León, Natalia González, Germán Fernández, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
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
    /**
     * Almacena todos los clientes en un archivo JSON en el sistema de archivos.
     *
     * @return Nombre del archivo JSON almacenado con la fecha actual en el nombre.
     * @throws StorageInternal Si ocurre un error al almacenar los datos en el sistema de archivos.
     */
    @Override
    @Transactional
    @Operation(
            summary = "Almacenar información de clientes en formato JSON",
            description = "Genera y almacena un archivo JSON con información de clientes en el sistema de almacenamiento local.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Se generó el archivo JSON exitosamente."),
                    @ApiResponse(responseCode = "500", description = "Error interno durante la operación de almacenamiento.")
            }
    )
    public String storeAll() {
        String storedFilename = "admin_clientes_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".json";
        Path jsonFilePath = this.rootLocation.resolve(storedFilename);

        try {

            if (Files.exists(jsonFilePath)) {
                Files.delete(jsonFilePath);
                log.info("Archivo existente eliminado: {}", storedFilename);
            }

            List<Cliente> clientes = clienteRepository.findAll();

            List<ClienteJsonZip> clienteMap = clientes.stream()
                .map(cliente -> {
                    ClienteJsonZip cliente1 = new ClienteJsonZip();
                    cliente1.setId(cliente.getId());
                    cliente1.setGuid(cliente.getGuid());
                    cliente1.setDni(cliente.getDni());
                    cliente1.setNombre(cliente.getNombre());
                    cliente1.setApellidos(cliente.getApellidos());
                    cliente1.setDireccion(cliente.getDireccion());
                    cliente1.setEmail(cliente.getEmail());
                    cliente1.setTelefono(cliente.getTelefono());
                    cliente1.setFotoPerfil(cliente.getFotoPerfil());
                    cliente1.setFotoDni(cliente.getFotoDni());

                    Set<CuentaZip> cuentas = cliente.getCuentas().stream()
                            .map(cuenta -> {
                                CuentaZip cuenta1 = new CuentaZip();
                                cuenta1.setId(cuenta.getId());
                                cuenta1.setGuid(cuenta.getGuid());
                                cuenta1.setIban(cuenta.getIban());
                                cuenta1.setSaldo(cuenta.getSaldo());
                                cuenta1.setTipoCuenta(cuenta.getTipoCuenta());

                                Tarjeta tarjeta = cuenta.getTarjeta();
                                if (tarjeta != null) {
                                    Tarjeta tarjeta1 = new Tarjeta();
                                    tarjeta1.setId(tarjeta.getId());
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

                                cuenta1.setClienteId(cuenta.getCliente().getGuid());
                                cuenta1.setCreatedAt(cuenta.getCreatedAt());
                                cuenta1.setUpdatedAt(cuenta.getUpdatedAt());
                                cuenta1.setIsDeleted(cuenta.getIsDeleted());
                                return cuenta1;
                            })
                            .collect(Collectors.toSet());

                    cliente1.setCuentas(cuentas);
                    cliente1.setUsuario(cliente.getUser());
                    cliente1.setCreatedAt(cliente.getCreatedAt().toString());
                    cliente1.setUpdatedAt(cliente.getUpdatedAt().toString());
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

            log.info("Archivo JSON con clientes almacenado: {}", storedFilename);

            return storedFilename;
        } catch (IOException e) {
            throw new StorageInternal("Fallo al almacenar el archivo JSON de clientes: " + e);
        }
    }
    /**
     * Recupera la lista de todos los archivos disponibles en el almacenamiento.
     *
     * @return Lista de rutas relativas de los archivos JSON.
     */
    @Override
    @Operation(
            summary = "Obtener la lista de archivos en almacenamiento",
            description = "Devuelve una lista de nombres de todos los archivos JSON en la carpeta de almacenamiento.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista recuperada con éxito."),
                    @ApiResponse(responseCode = "500", description = "Error interno al recuperar la lista de archivos.")
            }
    )
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
    /**
     * Carga el archivo especificado en la ruta de almacenamiento configurada.
     *
     * @param filename Nombre del archivo a cargar.
     * @return Ruta al archivo en el almacenamiento local.
     */
    @Override
    @Operation(
            summary = "Cargar un archivo desde el almacenamiento",
            description = "Recupera la ruta de un archivo almacenado según el nombre proporcionado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Archivo cargado con éxito."),
                    @ApiResponse(responseCode = "500", description = "Error interno al cargar el archivo.")
            }
    )
    public Path load(String filename) {
        log.info("Cargando fichero {}", filename);
        return rootLocation.resolve(filename);
    }
    /**
     * Convierte la ruta de almacenamiento en un recurso para su recuperación.
     *
     * @param filename Nombre del archivo a cargar como recurso.
     * @return Recurso para su entrega a través de una solicitud HTTP.
     */
    @Override
    @Operation(
            summary = "Cargar un recurso desde el almacenamiento",
            description = "Convierte una ruta de archivo en un recurso accesible para su entrega.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso cargado exitosamente."),
                    @ApiResponse(responseCode = "404", description = "Recurso no encontrado."),
                    @ApiResponse(responseCode = "500", description = "Error interno al cargar el recurso.")
            }
    )
    public Resource loadAsResource(String filename) {
        log.info("Cargando fichero {}", filename);
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
    /**
     * Inicializa la carpeta raíz para almacenamiento si no existe.
     *
     * @throws StorageInternal Si no es posible crear la ruta de almacenamiento.
     */
    @Override
    @Operation(
            summary = "Inicializar almacenamiento",
            description = "Crea la carpeta raíz de almacenamiento si aún no existe.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Almacenamiento inicializado correctamente."),
                    @ApiResponse(responseCode = "500", description = "Error interno al crear el almacenamiento.")
            }
    )
    public void init() {
        log.info("Inicializando almacenamiento");
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new StorageInternal("No se puede inicializar el almacenamiento " + e);
        }
    }
    /**
     * Elimina un archivo del almacenamiento si existe.
     *
     * @param filename Nombre del archivo que se debe eliminar.
     * @throws StorageInternal Si no es posible eliminar el archivo.
     */
    @Override
    @Operation(
            summary = "Eliminar un archivo",
            description = "Elimina un archivo de almacenamiento según su nombre.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Archivo eliminado correctamente."),
                    @ApiResponse(responseCode = "404", description = "Archivo no encontrado."),
                    @ApiResponse(responseCode = "500", description = "Error interno al intentar eliminar el archivo.")
            }
    )
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
