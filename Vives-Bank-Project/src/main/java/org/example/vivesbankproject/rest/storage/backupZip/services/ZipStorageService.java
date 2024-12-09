package org.example.vivesbankproject.rest.storage.backupZip.services;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.core.io.Resource;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

/**
 * Interfaz para el servicio de almacenamiento de archivos ZIP.
 * Define operaciones relacionadas con el almacenamiento, creación, procesamiento y eliminación de archivos ZIP.
 * Implementa las operaciones básicas para la gestión de archivos ZIP, así como la importación de datos.
 *
 * @author Jaime León, Natalia González, Germán Fernández, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
public interface ZipStorageService {

    /**
     * Inicializa el almacenamiento de archivos ZIP.
     * Crea los directorios necesarios para el almacenamiento de archivos si aún no existen.
     *
     */
    @Operation(summary = "Inicializa el almacenamiento de archivos ZIP",
            description = "Crea los directorios necesarios para el almacenamiento de archivos ZIP si aún no existen.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Almacenamiento inicializado correctamente."),
                    @ApiResponse(responseCode = "500", description = "Error interno en el servidor al inicializar el almacenamiento.")
            })
    void init();

    /**
     * Exporta los archivos relevantes a un archivo ZIP.
     * Elimina el archivo ZIP anterior si existe y crea uno nuevo con los datos.
     *
     * @return El nombre del archivo ZIP generado.
     */
    @Operation(summary = "Genera un archivo ZIP con los datos",
            description = "Crea un archivo ZIP con los archivos relevantes de la carpeta `dataAdmin`. Elimina cualquier archivo existente si ya existe.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "ZIP creado exitosamente."),
                    @ApiResponse(responseCode = "500", description = "Error interno en el servidor al crear el archivo ZIP.")
            })
    String export();

    /**
     * Carga un archivo ZIP como recurso para su descarga.
     * Este método verifica si el recurso es accesible.
     *
     * @param filename Nombre del archivo ZIP.
     * @return Un recurso que apunta al archivo ZIP solicitado.
     */
    @Operation(summary = "Carga un archivo ZIP como recurso",
            description = "Este método permite cargar un archivo ZIP desde el almacenamiento local para su descarga.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "ZIP cargado exitosamente."),
                    @ApiResponse(responseCode = "404", description = "El recurso no existe o no es accesible."),
                    @ApiResponse(responseCode = "500", description = "Error interno en el servidor al acceder al recurso.")
            })
    Resource loadAsResource(String filename);

    /**
     * Elimina un archivo ZIP del almacenamiento local.
     * Este método verifica la existencia del archivo antes de intentar eliminarlo.
     *
     * @param filename Nombre del archivo ZIP a eliminar.
     */
    @Operation(summary = "Elimina un archivo ZIP del almacenamiento",
            description = "Este método elimina un archivo ZIP del almacenamiento local, verificando su existencia.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "ZIP eliminado correctamente."),
                    @ApiResponse(responseCode = "404", description = "El archivo ZIP no existe."),
                    @ApiResponse(responseCode = "500", description = "Error interno en el servidor al intentar eliminar el archivo.")
            })
    void delete(String filename);

    /**
     * Carga un archivo ZIP para procesar su contenido y deserializa los datos.
     * Procesa archivos JSON dentro del ZIP.
     *
     * @param fileToUnzip Archivo ZIP para procesar.
     */
    @Operation(summary = "Carga un archivo ZIP para procesar datos",
            description = "Procesa un archivo ZIP para leer los datos internos. Solo procesa archivos JSON dentro del ZIP.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Datos importados exitosamente."),
                    @ApiResponse(responseCode = "500", description = "Error interno al procesar el archivo ZIP.")
            })
    void loadFromZip(File fileToUnzip);

    /**
     * Carga un archivo JSON desde almacenamiento local para su procesamiento.
     * Convierte el contenido JSON a una lista de objetos.
     *
     * @param jsonFile Archivo JSON para procesar.
     * @return Lista de objetos deserializados desde el archivo JSON.
     */
    @Operation(summary = "Carga un archivo JSON para su procesamiento",
            description = "Deserializa un archivo JSON para su procesamiento desde el almacenamiento local.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Archivo JSON cargado correctamente."),
                    @ApiResponse(responseCode = "404", description = "El archivo JSON no existe."),
                    @ApiResponse(responseCode = "500", description = "Error interno al leer el archivo JSON.")
            })
    List<Object> loadJson(File jsonFile);

    /**
     * Carga un archivo desde almacenamiento local utilizando la ruta proporcionada.
     * Devuelve la ruta al archivo.
     *
     * @param filename Ruta del archivo que se desea cargar.
     * @return Ruta al recurso solicitado.
     */
    @Operation(summary = "Cargar archivo ZIP desde almacenamiento local",
            description = "Devuelve la ruta al recurso para un archivo ZIP específico.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso cargado correctamente."),
                    @ApiResponse(responseCode = "404", description = "No existe el recurso solicitado."),
                    @ApiResponse(responseCode = "500", description = "Error interno al obtener el recurso.")
            })
    Path load(String filename);
}