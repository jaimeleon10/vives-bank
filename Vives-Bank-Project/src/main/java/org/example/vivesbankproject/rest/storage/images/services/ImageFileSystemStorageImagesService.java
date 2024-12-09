package org.example.vivesbankproject.rest.storage.images.services;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.rest.storage.exceptions.StorageBadRequest;
import org.example.vivesbankproject.rest.storage.exceptions.StorageInternal;
import org.example.vivesbankproject.rest.storage.exceptions.StorageNotFound;
import org.example.vivesbankproject.rest.storage.images.controller.StorageImagesController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;
/**
 * Servicio de almacenamiento de imágenes en el sistema de archivos.
 * Implementa la lógica para almacenar, recuperar, eliminar y gestionar imágenes en el almacenamiento local.
 *
 * @author Jaime León, Natalia González, Germán Fernández, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Service
@Slf4j
public class ImageFileSystemStorageImagesService implements StorageImagesService {

    private final Path rootLocation;
    /**
     * Constructor que inicializa la ruta raíz de almacenamiento utilizando la configuración definida en properties.
     *
     * @param path Ruta configurada para el almacenamiento de archivos.
     */

    public ImageFileSystemStorageImagesService(@Value("${upload.root-location}") String path) {
        this.rootLocation = Paths.get(path);
    }

    @Override
    @Operation(
            summary = "Almacena un archivo en el almacenamiento de imágenes",
            description = "Almacena un archivo utilizando un nombre único generado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "El archivo se almacena exitosamente", content = @Content(mediaType = "text/plain")),
                    @ApiResponse(responseCode = "400", description = "Archivo vacío o formato incorrecto"),
                    @ApiResponse(responseCode = "500", description = "Error interno al almacenar el archivo")
            }
    )
    public String store(MultipartFile file) {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = StringUtils.getFilenameExtension(filename);
        String justFilename = filename.replace("." + extension, "");
        String storedFilename = System.currentTimeMillis() + "_" + justFilename.replaceAll("\\s+", "") + "." + extension;

        try {
            if (file.isEmpty()) {
                throw new StorageBadRequest("Fichero vacío " + filename);
            }
            if (filename.contains("..")) {
                throw new StorageBadRequest(
                        "No se puede almacenar un fichero con una ruta relativa fuera del directorio actual "
                                + filename);
            }

            try (InputStream inputStream = file.getInputStream()) {
                log.info("Almacenando fichero " + filename + " como " + storedFilename);
                Files.copy(inputStream, this.rootLocation.resolve(storedFilename),
                        StandardCopyOption.REPLACE_EXISTING);
                return storedFilename;
            }

        } catch (IOException e) {
            throw new StorageInternal("Fallo al almacenar fichero " + filename + " " + e);
        }

    }
    @Override
    @Operation(
            summary = "Carga todos los archivos almacenados",
            description = "Devuelve un stream de todas las rutas relativas de los archivos almacenados.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de archivos devueltos correctamente")
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
    @Override
    @Operation(
            summary = "Carga un archivo específico",
            description = "Obtiene la ruta completa para un archivo almacenado en el almacenamiento local.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Archivo cargado exitosamente"),
                    @ApiResponse(responseCode = "404", description = "No se encuentra el archivo en el almacenamiento")
            }
    )
    public Path load(String filename) {
        log.info("Cargando fichero " + filename);
        return rootLocation.resolve(filename);
    }
    @Override
    @Operation(
            summary = "Carga un recurso como respuesta",
            description = "Devuelve un recurso para un archivo almacenado que se puede descargar.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso cargado correctamente"),
                    @ApiResponse(responseCode = "404", description = "No se encuentra el recurso")
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

    @Override
    @Operation(
            summary = "Elimina todos los archivos del almacenamiento",
            description = "Elimina todos los archivos almacenados en el almacenamiento local.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Archivos eliminados correctamente"),
                    @ApiResponse(responseCode = "500", description = "Error interno al intentar eliminar los archivos")
            }
    )
    public void deleteAll() {
        log.info("Eliminando todos los ficheros almacenados");
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }
    @Override
    @Operation(
            summary = "Inicializa el almacenamiento",
            description = "Crea los directorios necesarios para el almacenamiento.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Almacenamiento inicializado correctamente"),
                    @ApiResponse(responseCode = "500", description = "Error interno al intentar inicializar el almacenamiento")
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
    @Override
    @Operation(
            summary = "Elimina un archivo específico del almacenamiento",
            description = "Elimina un recurso específico del almacenamiento por nombre.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Archivo eliminado correctamente"),
                    @ApiResponse(responseCode = "404", description = "No se encuentra el recurso para eliminar")
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
    @Override
    @Operation(
            summary = "Obtiene la URL de acceso a un archivo",
            description = "Genera una URL para acceder a un recurso almacenado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "URL generada correctamente")
            }
    )
    public String getUrl(String filename) {
        log.info("Obteniendo URL del fichero " + filename);
        return MvcUriComponentsBuilder
                .fromMethodName(StorageImagesController.class, "serveFile", filename, null)
                .build().toUriString();
    }

}