package org.example.vivesbankproject.rest.storage.jsonClientesAdmin.services;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.core.io.Resource;

import java.nio.file.Path;
import java.util.stream.Stream;


/**
 * Interfaz que define las operaciones para el almacenamiento de archivos relacionados con clientes,
 * incluyendo almacenamiento, carga, inicialización y eliminación de recursos.
 * Esta interfaz es la base para la implementación de la lógica de almacenamiento en el sistema de archivos.
 *
 * @author Jaime León, Natalia González, Germán Fernández, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
public interface JsonClientesAdminStorageService {

    /**
     * Inicializa la estructura de almacenamiento raíz si no existe.
     *
     */
    @Operation(
            summary = "Inicializar almacenamiento",
            description = "Crea la carpeta raíz de almacenamiento si aún no existe.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Almacenamiento inicializado correctamente."),
                    @ApiResponse(responseCode = "500", description = "Error interno al crear el almacenamiento.")
            }
    )
    void init();

    /**
     * Almacena un archivo JSON con la información de clientes en el almacenamiento raíz.
     *
     * @return El nombre del archivo JSON que se creó en el almacenamiento.
     */
    @Operation(
            summary = "Almacenar todos los clientes en un archivo JSON",
            description = "Crea un archivo JSON con la información de todos los clientes y lo almacena.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Archivo JSON creado correctamente."),
                    @ApiResponse(responseCode = "500", description = "Error interno al crear el archivo.")
            }
    )
    String storeAll();

    /**
     * Recupera una lista de todas las rutas de archivos almacenados en el almacenamiento.
     *
     * @return Un flujo de rutas (Stream) que representan los archivos almacenados.
     */
    @Operation(
            summary = "Obtener una lista de todos los archivos almacenados",
            description = "Devuelve un Stream con todas las rutas de archivos almacenados en el almacenamiento raíz.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de archivos recuperada correctamente."),
                    @ApiResponse(responseCode = "500", description = "Error interno al obtener la lista de archivos.")
            }
    )
    Stream<Path> loadAll();

    /**
     * Carga una ruta específica de archivo desde el almacenamiento.
     *
     * @param filename Nombre del archivo a cargar.
     * @return Ruta al archivo específico en el almacenamiento raíz.
     */
    @Operation(
            summary = "Cargar un archivo específico desde el almacenamiento",
            description = "Devuelve la ruta al archivo solicitado en el almacenamiento.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Ruta cargada correctamente."),
                    @ApiResponse(responseCode = "500", description = "Error interno al acceder a la ruta del archivo.")
            }
    )
    Path load(String filename);

    /**
     * Convierte una ruta en un recurso para ser accedido a través de una solicitud HTTP.
     *
     * @param filename Nombre del archivo para ser devuelto como recurso.
     * @return El recurso correspondiente a la ruta solicitada.
     */
    @Operation(
            summary = "Cargar archivo como recurso",
            description = "Convierte un archivo en un recurso accesible a través de HTTP.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso cargado correctamente."),
                    @ApiResponse(responseCode = "404", description = "Recurso no encontrado."),
                    @ApiResponse(responseCode = "500", description = "Error interno al acceder al recurso.")
            }
    )
    Resource loadAsResource(String filename);

    /**
     * Elimina un archivo del almacenamiento raíz.
     *
     * @param filename Nombre del archivo que se debe eliminar.
     */
    @Operation(
            summary = "Eliminar un archivo del almacenamiento",
            description = "Elimina un archivo específico del almacenamiento raíz.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Archivo eliminado correctamente."),
                    @ApiResponse(responseCode = "404", description = "Archivo no encontrado."),
                    @ApiResponse(responseCode = "500", description = "Error interno al intentar eliminar el archivo.")
            }
    )
    void delete(String filename);
}