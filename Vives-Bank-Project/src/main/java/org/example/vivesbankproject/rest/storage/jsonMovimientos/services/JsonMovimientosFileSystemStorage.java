package org.example.vivesbankproject.rest.storage.jsonMovimientos.services;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.rest.movimientos.dto.MovimientoResponse;
import org.example.vivesbankproject.rest.movimientos.models.Movimiento;
import org.example.vivesbankproject.rest.movimientos.repositories.MovimientosRepository;
import org.example.vivesbankproject.rest.storage.exceptions.StorageInternal;
import org.example.vivesbankproject.rest.storage.exceptions.StorageNotFound;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;
/**
 * JsonMovimientosFileSystemStorage es la implementación del servicio JsonMovimientosStorageService para manejar operaciones
 * de almacenamiento de archivos JSON en el sistema de archivos. Contiene métodos para guardar, cargar, eliminar y listar archivos JSON.
 * <p>
 * @author Jaime León, Natalia González, Germán Fernández, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Service
@Slf4j
public class JsonMovimientosFileSystemStorage implements JsonMovimientosStorageService {

    private final Path rootLocation;
    private final MovimientosRepository movimientosRepository;
    /**
     * Constructor que inicializa la ubicación raíz del almacenamiento y el repositorio de movimientos.
     *
     * @param path Ruta base de almacenamiento para los archivos.
     * @param movimientosRepository Repositorio que interactúa con la base de datos para obtener los movimientos.
     */
    @Autowired
    public JsonMovimientosFileSystemStorage(@Value("${upload.root-location}") String path, MovimientosRepository movimientosRepository) {
        this.rootLocation = Paths.get(path);
        this.movimientosRepository = movimientosRepository;
    }
    /**
     * Almacena un archivo JSON con todos los movimientos en el almacenamiento principal.
     *
     * @return Nombre del archivo JSON almacenado.
     */
    @Override
    @Operation(
            summary = "Guardar todos los movimientos en un archivo JSON",
            description = "Crea un archivo JSON con todos los movimientos actuales en el almacenamiento.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Archivo JSON almacenado con éxito."),
                    @ApiResponse(responseCode = "500", description = "Error interno al almacenar el archivo JSON.")
            }
    )
    public String storeAll() {
        String storedFilename = "admin_movimientos_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".json";
        Path jsonFilePath = Path.of("dataAdmin").resolve(storedFilename);

        try {

            if (Files.exists(jsonFilePath)) {
                Files.delete(jsonFilePath);
                log.info("Archivo existente eliminado: {}", storedFilename);
            }

            List<Movimiento> movimientos = movimientosRepository.findAll();

            List<MovimientoResponse> movimientoMap = movimientos.stream()
                    .map(movimiento -> {
                        MovimientoResponse movimientoResponse = new MovimientoResponse();
                        movimientoResponse.setGuid(movimiento.getGuid());
                        movimientoResponse.setClienteGuid(movimiento.getClienteGuid());

                        if (movimiento.getDomiciliacion() != null) {
                            movimientoResponse.setDomiciliacion(movimiento.getDomiciliacion());
                        }
                        if (movimiento.getIngresoDeNomina() != null) {
                            movimientoResponse.setIngresoDeNomina(movimiento.getIngresoDeNomina());
                        }
                        if (movimiento.getPagoConTarjeta() != null) {
                            movimientoResponse.setPagoConTarjeta(movimiento.getPagoConTarjeta());
                        }
                        if (movimiento.getTransferencia() != null) {
                            movimientoResponse.setTransferencia(movimiento.getTransferencia());
                        }

                        movimientoResponse.setCreatedAt(movimiento.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                        movimientoResponse.setIsDeleted(movimiento.getIsDeleted());

                        return movimientoResponse;
                    })
                    .collect(Collectors.toList());

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

            JavaTimeModule module = new JavaTimeModule();
            module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            objectMapper.registerModule(module);

            objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

            String jsonData = objectMapper.writeValueAsString(movimientoMap);

            Files.write(jsonFilePath, jsonData.getBytes());

            log.info("Archivo JSON con movimientos almacenado: " + storedFilename);

            return storedFilename;
        } catch (IOException e) {
            throw new StorageInternal("Fallo al almacenar el archivo JSON de movimientos: " + e);
        }
    }

    /**
     * Almacena un archivo JSON con movimientos de un cliente específico.
     *
     * @param guid Identificador único del cliente.
     * @return Nombre del archivo JSON almacenado para el cliente.
     */
    @Override
    @Operation(
            summary = "Guardar movimientos de un cliente específico",
            description = "Crea un archivo JSON solo con los movimientos de un cliente específico.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Archivo JSON para el cliente almacenado con éxito."),
                    @ApiResponse(responseCode = "500", description = "Error interno al almacenar el archivo JSON del cliente.")
            }
    )
    public String store(String guid) {
        String storedFilename = "movimientos_" + guid + "_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".json";
        Path jsonFilePath = this.rootLocation.resolve(storedFilename);

        try {
            Optional<Movimiento> movimientos = movimientosRepository.findByClienteGuid(guid);

            List<MovimientoResponse> movimientoMap = movimientos.stream()
                    .map(movimiento -> {
                        MovimientoResponse movimientoResponse = new MovimientoResponse();
                        movimientoResponse.setGuid(movimiento.getGuid());
                        movimientoResponse.setClienteGuid(movimiento.getClienteGuid());

                        if (movimiento.getDomiciliacion() != null) {
                            movimientoResponse.setDomiciliacion(movimiento.getDomiciliacion());
                        }
                        if (movimiento.getIngresoDeNomina() != null) {
                            movimientoResponse.setIngresoDeNomina(movimiento.getIngresoDeNomina());
                        }
                        if (movimiento.getPagoConTarjeta() != null) {
                            movimientoResponse.setPagoConTarjeta(movimiento.getPagoConTarjeta());
                        }
                        if (movimiento.getTransferencia() != null) {
                            movimientoResponse.setTransferencia(movimiento.getTransferencia());
                        }

                        movimientoResponse.setCreatedAt(movimiento.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                        movimientoResponse.setIsDeleted(movimiento.getIsDeleted());

                        return movimientoResponse;
                    })
                    .collect(Collectors.toList());

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

            JavaTimeModule module = new JavaTimeModule();
            module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            objectMapper.registerModule(module);

            objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

            String jsonData = objectMapper.writeValueAsString(movimientoMap);

            Files.write(jsonFilePath, jsonData.getBytes());

            log.info("Archivo JSON con movimientos del cliente almacenado: " + storedFilename);

            return storedFilename;
        } catch (IOException e) {
            throw new StorageInternal("Fallo al almacenar el archivo JSON de movimientos del cliente: " + e);
        }
    }
    /**
     * Lista todos los archivos almacenados en la ruta raíz configurada.
     *
     * @return Un Stream con las rutas relativas de los archivos almacenados.
     */
    @Override
    @Operation(
            summary = "Cargar todos los ficheros almacenados",
            description = "Devuelve un Stream con los ficheros almacenados en la ruta predeterminada.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Ficheros cargados con éxito."),
                    @ApiResponse(responseCode = "500", description = "Error interno al cargar los ficheros.")
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
     * Carga un archivo específico desde la ruta raíz configurada.
     *
     * @param filename Nombre del archivo a cargar.
     * @return Ruta del archivo para operaciones posteriores.
     */
    @Override
    @Operation(
            summary = "Cargar un archivo desde el almacenamiento",
            description = "Devuelve la ruta para acceder al archivo en el almacenamiento local.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Ruta del archivo cargada con éxito."),
                    @ApiResponse(responseCode = "500", description = "Error interno al cargar la ruta del archivo.")
            }
    )
    public Path load(String filename) {
        log.info("Cargando fichero " + filename);
        return rootLocation.resolve(filename);
    }
    /**
     * Carga un archivo desde la ruta de almacenamiento como recurso.
     *
     * @param filename Nombre del archivo a cargar.
     * @return Recurso que representa el archivo.
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
    /**
     * Inicializa el almacenamiento creando las carpetas necesarias en la ruta configurada.
     * Este método se ejecuta para asegurarse de que el directorio raíz exista antes de realizar operaciones de almacenamiento.
     */
    @Override
    @Operation(
            summary = "Inicializar almacenamiento",
            description = "Crea las carpetas necesarias en la ruta de almacenamiento raíz para operaciones futuras.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Almacenamiento inicializado con éxito."),
                    @ApiResponse(responseCode = "500", description = "Error interno al inicializar el almacenamiento.")
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
     * Elimina un archivo del almacenamiento dado su nombre.
     *
     * @param filename Nombre del archivo a eliminar.
     */
    @Override
    @Operation(
            summary = "Eliminar un archivo",
            description = "Elimina un archivo específico del almacenamiento.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Archivo eliminado con éxito."),
                    @ApiResponse(responseCode = "500", description = "Error interno al eliminar el archivo.")
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