package org.example.vivesbankproject.storage.jsonMovimientos.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.cliente.dto.ClienteJson;
import org.example.vivesbankproject.cliente.models.Cliente;
import org.example.vivesbankproject.cliente.repositories.ClienteRepository;
import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaResponse;
import org.example.vivesbankproject.movimientos.dto.MovimientoResponse;
import org.example.vivesbankproject.movimientos.models.*;
import org.example.vivesbankproject.movimientos.repositories.MovimientosRepository;
import org.example.vivesbankproject.storage.exceptions.StorageInternal;
import org.example.vivesbankproject.storage.exceptions.StorageNotFound;
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
public class JsonMovimientosFileSystemStorage implements JsonMovimientosStorageService {

    private final Path rootLocation;
    private final MovimientosRepository movimientosRepository;

    @Autowired
    public JsonMovimientosFileSystemStorage(@Value("${upload.root-location}") String path, MovimientosRepository movimientosRepository) {
        this.rootLocation = Paths.get(path);
        this.movimientosRepository = movimientosRepository;
    }

    @Override
    public String storeAll() {
        String storedFilename = "movimientos_clientes_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".json";
        Path jsonFilePath = this.rootLocation.resolve(storedFilename);

        try {
            List<Movimiento> movimientos = movimientosRepository.findAll();

            List<MovimientoResponse> movimientoMap = movimientos.stream()
                .map(movimiento -> {
                    MovimientoResponse movimientoResponse = new MovimientoResponse();
                    movimientoResponse.setGuid(movimiento.getGuid());
                    movimientoResponse.setClienteGuid(movimiento.getClienteGuid());

                    Domiciliacion domiciliacion = movimiento.getDomiciliacion();
                    if (domiciliacion != null) {
                        domiciliacion.setIban_Origen(domiciliacion.getIban_Origen());
                        domiciliacion.setNombreAcreedor(domiciliacion.getNombreAcreedor());
                        domiciliacion.setIdentificadorAcreedor(domiciliacion.getIdentificadorAcreedor());
                        movimientoResponse.setDomiciliacion(domiciliacion);
                    }

                    IngresoDeNomina ingresoDeNomina = movimiento.getIngresoDeNomina();
                    if (ingresoDeNomina != null){
                        ingresoDeNomina.setIban_Destino(ingresoDeNomina.getIban_Destino());
                        ingresoDeNomina.setNombreEmpresa(ingresoDeNomina.getNombreEmpresa());
                        ingresoDeNomina.setCifEmpresa(ingresoDeNomina.getCifEmpresa());
                        movimientoResponse.setIngresoDeNomina(ingresoDeNomina);
                    }

                    PagoConTarjeta pagoConTarjeta = movimiento.getPagoConTarjeta();
                    if (pagoConTarjeta != null) {
                        pagoConTarjeta.setNumeroTarjeta(pagoConTarjeta.getNumeroTarjeta());
                        pagoConTarjeta.setNombreComercio(pagoConTarjeta.getNombreComercio());
                        pagoConTarjeta.setCvv(pagoConTarjeta.getCvv());
                        movimientoResponse.setPagoConTarjeta(pagoConTarjeta);
                    }

                    Transferencia transferencia = movimiento.getTransferencia();
                    if (transferencia!= null) {
                        transferencia.setIban_Origen(transferencia.getIban_Origen());
                        transferencia.setIban_Destino(transferencia.getIban_Destino());
                        transferencia.setNombreBeneficiario(transferencia.getNombreBeneficiario());
                        movimientoResponse.setTransferencia(transferencia);
                    }

                    movimientoResponse.setCreatedAt(movimiento.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    movimientoResponse.setIsDeleted(movimiento.getIsDeleted());

                    return movimientoResponse;
                })
                .collect(Collectors.toList());

            ObjectMapper objectMapper = new ObjectMapper();
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

    @Override
    public String store(String guid) {
        String storedFilename = "movimientos_" + guid + "_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".json";
        Path jsonFilePath = this.rootLocation.resolve(storedFilename);

        try {
            Optional<Movimiento> movimientos = movimientosRepository.findMovimientosByClienteGuid(guid);

            List<MovimientoResponse> movimientoMap = movimientos.stream()
                    .map(movimiento -> {
                        MovimientoResponse movimientoResponse = new MovimientoResponse();
                        movimientoResponse.setGuid(movimiento.getGuid());
                        movimientoResponse.setClienteGuid(movimiento.getClienteGuid());

                        Domiciliacion domiciliacion = movimiento.getDomiciliacion();
                        if (domiciliacion != null) {
                            domiciliacion.setIban_Origen(domiciliacion.getIban_Origen());
                            domiciliacion.setNombreAcreedor(domiciliacion.getNombreAcreedor());
                            domiciliacion.setIdentificadorAcreedor(domiciliacion.getIdentificadorAcreedor());
                            movimientoResponse.setDomiciliacion(domiciliacion);
                        }

                        IngresoDeNomina ingresoDeNomina = movimiento.getIngresoDeNomina();
                        if (ingresoDeNomina != null) {
                            ingresoDeNomina.setIban_Destino(ingresoDeNomina.getIban_Destino());
                            ingresoDeNomina.setNombreEmpresa(ingresoDeNomina.getNombreEmpresa());
                            ingresoDeNomina.setCifEmpresa(ingresoDeNomina.getCifEmpresa());
                            movimientoResponse.setIngresoDeNomina(ingresoDeNomina);
                        }

                        PagoConTarjeta pagoConTarjeta = movimiento.getPagoConTarjeta();
                        if (pagoConTarjeta != null) {
                            pagoConTarjeta.setNumeroTarjeta(pagoConTarjeta.getNumeroTarjeta());
                            pagoConTarjeta.setNombreComercio(pagoConTarjeta.getNombreComercio());
                            pagoConTarjeta.setCvv(pagoConTarjeta.getCvv());
                            movimientoResponse.setPagoConTarjeta(pagoConTarjeta);
                        }

                        Transferencia transferencia = movimiento.getTransferencia();
                        if (transferencia != null) {
                            transferencia.setIban_Origen(transferencia.getIban_Origen());
                            transferencia.setIban_Destino(transferencia.getIban_Destino());
                            transferencia.setNombreBeneficiario(transferencia.getNombreBeneficiario());
                            movimientoResponse.setTransferencia(transferencia);
                        }

                        movimientoResponse.setCreatedAt(movimiento.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                        movimientoResponse.setIsDeleted(movimiento.getIsDeleted());

                        return movimientoResponse;
                    })
                    .collect(Collectors.toList());

            ObjectMapper objectMapper = new ObjectMapper();
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