package org.example.vivesbankproject.rest.storage.pdfMovimientos.services;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.core.io.Resource;

import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Interfaz para el servicio de almacenamiento y gestión de archivos PDF relacionados con movimientos.
 * Proporciona métodos para inicializar el almacenamiento, almacenar archivos PDF, recuperar recursos,
 * listar todos los archivos, obtener un archivo individual y eliminar archivos.
 *
 * @author Jaime León, Natalia González, Germán Fernández, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
public interface PdfMovimientosStorageService {

    /**
     * Inicializa la estructura de almacenamiento para el servicio.
     * Crea el directorio de almacenamiento si no existe.
     */
    void init();

    /**
     * Almacena un archivo PDF con todos los movimientos generados.
     *
     * @return El nombre del archivo PDF generado y almacenado.
     */
    @Operation(
            summary = "Almacenar un archivo PDF con todos los movimientos generados",
            description = "Genera y almacena un archivo PDF con todos los movimientos de la base de datos.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Archivo PDF generado y almacenado correctamente."),
                    @ApiResponse(responseCode = "500", description = "Error interno al almacenar el archivo PDF.")
            }
    )
    String storeAll();

    /**
     * Almacena un archivo PDF con los movimientos de un cliente específico identificado por su GUID.
     *
     * @param guid Identificador del cliente.
     * @return El nombre del archivo PDF generado y almacenado para el cliente.
     */
    @Operation(
            summary = "Almacenar un archivo PDF de movimientos para un cliente específico",
            description = "Genera y almacena un archivo PDF con los movimientos de un cliente específico.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Archivo PDF de movimientos generado correctamente."),
                    @ApiResponse(responseCode = "500", description = "Error interno al almacenar el archivo PDF.")
            }
    )
    String store(String guid);

    /**
     * Recupera una lista de todos los archivos PDF almacenados en el almacenamiento.
     *
     * @return Un stream con las rutas relativas a los archivos PDF almacenados.
     */
    @Operation(
            summary = "Listar todos los archivos PDF almacenados",
            description = "Recupera un Stream con los nombres de todos los archivos PDF almacenados.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de archivos PDF recuperada correctamente."),
                    @ApiResponse(responseCode = "500", description = "Error interno al listar los archivos PDF.")
            }
    )
    Stream<Path> loadAll();

    /**
     * Carga un archivo específico en el almacenamiento.
     *
     * @param filename Nombre del archivo a cargar.
     * @return La ruta del archivo cargado.
     */
    @Operation(
            summary = "Cargar un archivo específico",
            description = "Recupera la ruta de un archivo almacenado según el nombre proporcionado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Archivo cargado correctamente."),
                    @ApiResponse(responseCode = "500", description = "Error interno al cargar el archivo.")
            }
    )
    Path load(String filename);

    /**
     * Recupera un recurso de un archivo PDF almacenado para su descarga.
     *
     * @param filename Nombre del archivo a recuperar como recurso.
     * @return El recurso del archivo para su acceso o descarga.
     */
    @Operation(
            summary = "Recuperar un recurso de archivo para su descarga",
            description = "Recupera el archivo PDF almacenado como recurso para su descarga.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso de archivo cargado correctamente."),
                    @ApiResponse(responseCode = "404", description = "El recurso no existe."),
                    @ApiResponse(responseCode = "500", description = "Error interno al recuperar el recurso del archivo.")
            }
    )
    Resource loadAsResource(String filename);

    /**
     * Elimina un archivo PDF del almacenamiento.
     *
     * @param filename Nombre del archivo que se desea eliminar.
     */
    @Operation(
            summary = "Eliminar un archivo del almacenamiento",
            description = "Elimina un archivo PDF específico del almacenamiento.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Archivo eliminado correctamente."),
                    @ApiResponse(responseCode = "500", description = "Error interno al eliminar el archivo.")
            }
    )
    void delete(String filename);
}