package org.example.vivesbankproject.rest.storage.images.services;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Interfaz de servicio para gestionar operaciones relacionadas con el almacenamiento de imágenes.
 * Contiene los métodos para inicializar, almacenar, recuperar, borrar y obtener recursos de almacenamiento de imágenes.
 * @author Jaime León, Natalia González, Germán Fernández, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
public interface StorageImagesService {

    /**
     * Inicializa el servicio de almacenamiento de imágenes.
     * Crea directorios necesarios u otras configuraciones iniciales.
     */
    @Operation(
            summary = "Inicializa el almacenamiento de imágenes",
            description = "Configura y prepara el servicio de almacenamiento para el correcto funcionamiento.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Servicio inicializado correctamente"),
                    @ApiResponse(responseCode = "500", description = "Error interno al inicializar el servicio")
            }
    )
    void init();

    /**
     * Almacena un archivo en el almacenamiento.
     *
     * @param file Archivo que se desea almacenar.
     * @return Ruta única generada para el almacenamiento del archivo.
     */
    @Operation(
            summary = "Almacena un archivo en el almacenamiento de imágenes",
            description = "Recibe un archivo y lo almacena en el directorio de almacenamiento local.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Archivo almacenado con éxito", content = @Content(mediaType = "text/plain")),
                    @ApiResponse(responseCode = "400", description = "Error en la solicitud")
            }
    )
    String store(MultipartFile file);

    /**
     * Recupera todos los archivos en el almacenamiento como un flujo de rutas.
     *
     * @return Stream con todas las rutas de los archivos almacenados.
     */
    @Operation(
            summary = "Carga todos los archivos del almacenamiento",
            description = "Devuelve un flujo con todas las rutas de los archivos almacenados en el almacenamiento.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de archivos devueltos correctamente")
            }
    )
    Stream<Path> loadAll();

    /**
     * Recupera la ruta de un archivo específico en el almacenamiento.
     *
     * @param filename Nombre del archivo a recuperar.
     * @return Ruta al archivo específico.
     */
    @Operation(
            summary = "Cargar un archivo específico desde el almacenamiento",
            description = "Devuelve la ruta de un archivo almacenado en el almacenamiento local.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Ruta de archivo recuperada correctamente"),
                    @ApiResponse(responseCode = "404", description = "No se encontró el archivo")
            }
    )
    Path load(String filename);

    /**
     * Recupera un recurso como respuesta para un archivo específico desde el almacenamiento.
     *
     * @param filename Nombre del archivo a recuperar.
     * @return El recurso correspondiente para acceder a la imagen.
     */
    @Operation(
            summary = "Carga un recurso desde el almacenamiento como respuesta",
            description = "Devuelve un recurso para el archivo en el almacenamiento con la ruta correspondiente.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso devuelto correctamente"),
                    @ApiResponse(responseCode = "404", description = "No se encontró el recurso")
            }
    )
    Resource loadAsResource(String filename);

    /**
     * Elimina un archivo específico del almacenamiento.
     *
     * @param filename Nombre del archivo que se desea eliminar.
     */
    @Operation(
            summary = "Elimina un archivo del almacenamiento",
            description = "Elimina el archivo almacenado que tiene el nombre especificado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Archivo eliminado correctamente"),
                    @ApiResponse(responseCode = "404", description = "No se encontró el archivo para eliminar")
            }
    )
    void delete(String filename);

    /**
     * Elimina todos los archivos en el almacenamiento.
     */
    @Operation(
            summary = "Elimina todos los archivos del almacenamiento",
            description = "Borra todos los archivos almacenados en el directorio de almacenamiento local.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Archivos eliminados correctamente"),
                    @ApiResponse(responseCode = "500", description = "Error interno en la operación de eliminación")
            }
    )
    void deleteAll();

    /**
     * Obtiene la URL para acceder a un archivo específico en el almacenamiento.
     *
     * @param filename Nombre del archivo para el que se solicita la URL.
     * @return URL generada para acceder al recurso.
     */
    @Operation(
            summary = "Obtiene la URL de acceso a un archivo almacenado",
            description = "Genera una URL válida para acceder a la imagen almacenada.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "URL generada correctamente", content = @Content(mediaType = "text/plain"))
            }
    )
    String getUrl(String filename);
}