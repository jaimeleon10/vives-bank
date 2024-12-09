package org.example.vivesbankproject.rest.storage.jsonMovimientos.services;

import org.springframework.core.io.Resource;

import java.nio.file.Path;
import java.util.stream.Stream;


/**
 * Interfaz para el servicio de almacenamiento de archivos JSON que contienen información de movimientos de clientes.
 * Define los métodos necesarios para interactuar con el almacenamiento de estos archivos JSON, incluyendo
 * operaciones para almacenamiento, recuperación, eliminación y listado de archivos.
 * @author Jaime León, Natalia González, Germán Fernández, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
public interface JsonMovimientosStorageService {

    /**
     * Inicializa el almacenamiento creando los directorios necesarios si aún no existen.
     */
    @io.swagger.v3.oas.annotations.Operation(
            summary = "Inicializar almacenamiento",
            description = "Crea los directorios de almacenamiento si no existen.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Almacenamiento inicializado con éxito."),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error al inicializar el almacenamiento.")
            }
    )
    void init();

    /**
     * Almacena un archivo JSON con todos los movimientos de clientes.
     *
     * @return Nombre del archivo generado con los movimientos.
     */
    @io.swagger.v3.oas.annotations.Operation(
            summary = "Almacenar todos los movimientos",
            description = "Genera un archivo JSON con todos los movimientos de clientes.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Archivo JSON generado correctamente."),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error al generar el archivo JSON.")
            }
    )
    String storeAll();

    /**
     * Almacena un archivo JSON con los movimientos de un cliente específico basado en su GUID.
     *
     * @param guid Identificador único del cliente.
     * @return Nombre del archivo generado con la información de movimientos del cliente.
     */
    @io.swagger.v3.oas.annotations.Operation(
            summary = "Almacenar movimientos de un cliente específico",
            description = "Genera un archivo JSON con los movimientos de un cliente específico identificado por su GUID.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Archivo JSON generado correctamente."),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error al generar el archivo JSON.")
            }
    )
    String store(String guid);

    /**
     * Recupera un Stream con todos los archivos JSON almacenados.
     *
     * @return Stream de rutas de archivos JSON.
     */
    @io.swagger.v3.oas.annotations.Operation(
            summary = "Listar todos los archivos almacenados",
            description = "Devuelve un Stream con todas las rutas de archivos almacenados en el directorio de almacenamiento.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de archivos recuperada correctamente."),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error al recuperar la lista de archivos.")
            }
    )
    Stream<Path> loadAll();

    /**
     * Carga la ruta de un archivo específico por su nombre.
     *
     * @param filename Nombre del archivo que se desea recuperar.
     * @return Ruta del archivo específico.
     */
    @io.swagger.v3.oas.annotations.Operation(
            summary = "Cargar archivo específico",
            description = "Devuelve la ruta de un archivo específico por su nombre.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Archivo cargado correctamente."),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error al cargar el archivo.")
            }
    )
    Path load(String filename);

    /**
     * Carga un archivo como recurso para su descarga.
     *
     * @param filename Nombre del archivo que se va a recuperar.
     * @return Recurso que representa el archivo JSON.
     */
    @io.swagger.v3.oas.annotations.Operation(
            summary = "Cargar archivo como recurso",
            description = "Devuelve el archivo como recurso para que pueda ser descargado.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Recurso cargado correctamente."),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Recurso no encontrado."),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error al intentar cargar el recurso.")
            }
    )
    Resource loadAsResource(String filename);

    /**
     * Elimina un archivo específico del almacenamiento.
     *
     * @param filename Nombre del archivo que se desea eliminar.
     */
    @io.swagger.v3.oas.annotations.Operation(
            summary = "Eliminar archivo específico",
            description = "Elimina un archivo JSON específico del almacenamiento.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Archivo eliminado correctamente."),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error al eliminar el archivo.")
            }
    )
    void delete(String filename);
}
