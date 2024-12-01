package org.example.vivesbankproject.storage.pdfMovimientos.services;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.movimientos.models.Movimiento;
import org.example.vivesbankproject.movimientos.repositories.MovimientosRepository;
import org.example.vivesbankproject.storage.exceptions.StorageInternal;
import org.example.vivesbankproject.storage.exceptions.StorageNotFound;
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

    @Override
    public String storeAll() {
        String storedFilename = "movimientos_clientes_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".pdf";
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

    @Override
    public String store(String guid) {
        String storedFilename = "movimientos_" + guid + "_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".pdf";
        Path pdfFilePath = this.rootLocation.resolve(storedFilename);

        try {
            Optional<Movimiento> movimientoOpt = movimientosRepository.findMovimientosByClienteGuid(guid);

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