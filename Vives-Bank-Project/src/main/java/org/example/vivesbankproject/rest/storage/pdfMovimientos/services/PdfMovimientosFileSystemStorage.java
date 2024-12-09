package org.example.vivesbankproject.rest.storage.pdfMovimientos.services;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
/**
 * Implementación de almacenamiento de archivos PDF para movimientos de clientes en un almacenamiento
 * basado en el sistema de archivos. Proporciona funciones para generar, almacenar, listar, recuperar,
 * y eliminar archivos PDF relacionados con movimientos de clientes.
 *
 * @author Jaime León, Natalia González, Germán Fernández, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Service
@Slf4j
public class PdfMovimientosFileSystemStorage implements PdfMovimientosStorageService {

    private final Path rootLocation;
    private final MovimientosRepository movimientosRepository;

    @Autowired
    public PdfMovimientosFileSystemStorage(@Value("${upload.root-location}") String path, MovimientosRepository movimientosRepository) {
        this.rootLocation = Paths.get(path);
        this.movimientosRepository = movimientosRepository;
    }
    /**
     * Almacena un archivo PDF con todos los movimientos de clientes en el almacenamiento.
     *
     * @return El nombre del archivo generado y almacenado.
     * @throws StorageInternal En caso de error al generar o almacenar el archivo.
     */
    @Override
    @Operation(
            summary = "Generar y almacenar un archivo PDF con todos los movimientos",
            description = "Genera un archivo PDF con información de todos los movimientos de clientes y lo almacena.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Archivo generado y almacenado correctamente."),
                    @ApiResponse(responseCode = "500", description = "Error al almacenar el archivo PDF.")
            }
    )
    public String storeAll() {
        String storedFilename = "admin_movimientos_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".pdf";
        Path pdfFilePath = Path.of("dataAdmin").resolve(storedFilename);

        try {
            List<Movimiento> movimientos = movimientosRepository.findAll();

            PdfDocument pdfDoc = new PdfDocument(new PdfWriter(pdfFilePath.toString()));
            Document document = new Document(pdfDoc);

            document.add(new Paragraph("Movimientos de Clientes")
                    .setFontSize(18)
                    .setBold());

            for (Movimiento movimiento : movimientos) {
                document.add(new Paragraph("GUID: " + movimiento.getGuid()));
                document.add(new Paragraph("Cliente GUID: " + movimiento.getClienteGuid()));
                document.add(new Paragraph("Domiciliación: " + movimiento.getDomiciliacion()));
                document.add(new Paragraph("Ingreso de Nómina: " + movimiento.getIngresoDeNomina()));
                document.add(new Paragraph("Pago con Tarjeta: " + movimiento.getPagoConTarjeta()));
                document.add(new Paragraph("Transferencia: " + movimiento.getTransferencia()));
                document.add(new Paragraph("\n"));
            }

            document.close();

            log.info("Archivo PDF con movimientos almacenado: " + storedFilename);

            return storedFilename;
        } catch (IOException e) {
            throw new StorageInternal("Fallo al almacenar el archivo PDF de movimientos: " + e);
        }
    }
    /**
     * Almacena un archivo PDF con los movimientos de un cliente específico identificado por el GUID.
     *
     * @param guid El identificador único del cliente.
     * @return El nombre del archivo generado y almacenado.
     * @throws StorageNotFound En caso de que no se encuentren movimientos para el cliente.
     * @throws StorageInternal En caso de error al generar o almacenar el archivo.
     */
    @Override
    @Operation(
            summary = "Generar y almacenar un archivo PDF de movimientos de un cliente específico",
            description = "Genera un archivo PDF con información de los movimientos de un cliente específico identificado por su GUID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Archivo generado y almacenado correctamente."),
                    @ApiResponse(responseCode = "404", description = "Cliente no encontrado."),
                    @ApiResponse(responseCode = "500", description = "Error interno al generar el archivo PDF.")
            }
    )
    public String store(String guid) {
        String storedFilename = "movimientos_" + guid + "_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".pdf";
        Path pdfFilePath = this.rootLocation.resolve(storedFilename);

        try {
            Optional<Movimiento> movimientoOpt = movimientosRepository.findByClienteGuid(guid);

            if (movimientoOpt.isEmpty()) {
                throw new StorageNotFound("No se encontraron movimientos para el cliente con GUID: " + guid);
            }

            Movimiento movimiento = movimientoOpt.get();

            try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(pdfFilePath.toString()));
                 Document document = new Document(pdfDoc)) {

                document.add(new Paragraph("Movimientos del Cliente: " + guid)
                        .setFontSize(18)
                        .setBold());

                document.add(new Paragraph("GUID: " + movimiento.getGuid()));
                document.add(new Paragraph("Cliente GUID: " + movimiento.getClienteGuid()));
                document.add(new Paragraph("Domiciliación: " + movimiento.getDomiciliacion()));
                document.add(new Paragraph("Ingreso de Nómina: " + movimiento.getIngresoDeNomina()));
                document.add(new Paragraph("Pago con Tarjeta: " + movimiento.getPagoConTarjeta()));
                document.add(new Paragraph("Transferencia: " + movimiento.getTransferencia()));
                document.add(new Paragraph("\n"));

                log.info("Archivo PDF con movimientos del cliente almacenado: " + storedFilename);
            }

            return storedFilename;
        } catch (IOException e) {
            throw new StorageInternal("Fallo al almacenar el archivo PDF de movimientos del cliente: " + e);
        }
    }
    /**
     * Lista todos los archivos almacenados en el almacenamiento.
     *
     * @return Un Stream de rutas relativas de todos los archivos almacenados.
     * @throws StorageInternal En caso de error al acceder al almacenamiento.
     */
    @Override
    @Operation(
            summary = "Listar todos los archivos almacenados",
            description = "Devuelve una lista de todos los archivos PDF almacenados en el almacenamiento.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de archivos recuperada correctamente."),
                    @ApiResponse(responseCode = "500", description = "Error interno al listar los archivos almacenados.")
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
     * Carga la ruta completa de un archivo almacenado en la ubicación raíz definida.
     *
     * @param filename El nombre del archivo a cargar.
     * @return La ruta completa del archivo.
     * @throws StorageInternal Si ocurre un problema al resolver la ruta.
     */
    @Override
    @Operation(
            summary = "Cargar la ruta de un archivo",
            description = "Devuelve la ruta completa de un archivo específico almacenado en el almacenamiento.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Ruta cargada correctamente."),
                    @ApiResponse(responseCode = "500", description = "Error interno al resolver la ruta del archivo.")
            }
    )
    public Path load(String filename) {
        log.info("Cargando fichero " + filename);
        return rootLocation.resolve(filename);
    }
    /**
     * Carga un recurso que representa un archivo almacenado y verifica su disponibilidad para ser leído.
     *
     * @param filename El nombre del archivo a recuperar como recurso.
     * @return El recurso correspondiente al archivo almacenado.
     * @throws StorageNotFound Si el archivo no existe o no es legible.
     */
    @Override
    @Operation(
            summary = "Cargar un archivo como recurso",
            description = "Convierte un archivo almacenado en un recurso que puede ser accedido para su lectura.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso cargado correctamente."),
                    @ApiResponse(responseCode = "404", description = "No se puede leer el archivo."),
                    @ApiResponse(responseCode = "500", description = "Error interno al acceder al archivo.")
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
     * Inicializa el almacenamiento creando el directorio raíz si no existe.
     *
     * @throws StorageInternal Si ocurre un error al crear el directorio raíz.
     */
    @Override
    @Operation(
            summary = "Inicializar almacenamiento",
            description = "Crea la carpeta raíz para almacenamiento si aún no existe.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Almacenamiento inicializado correctamente."),
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
     * Elimina un archivo específico del almacenamiento.
     *
     * @param filename El nombre del archivo a eliminar.
     * @throws StorageInternal En caso de error al intentar eliminar el archivo.
     */
    @Override
    @Operation(
            summary = "Eliminar un archivo",
            description = "Elimina un archivo PDF específico del almacenamiento.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Archivo eliminado correctamente."),
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