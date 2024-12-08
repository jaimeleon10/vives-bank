package org.example.vivesbankproject.rest.storage.jsonClientes.services;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.core.io.Resource;

import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Interfaz para definir las operaciones relacionadas con el almacenamiento de archivos JSON de clientes.
 * Incluye la capacidad de inicializar almacenamiento, almacenar archivos, listar recursos,
 * recuperar recursos individuales y eliminar recursos almacenados.
 *
 * @author Jaime León, Natalia González, Germán Fernández, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
public interface JsonClientesStorageService {

    /**
     * Inicializa la configuración y estructura de almacenamiento.
     */
    @Operation(
            summary = "Inicializa almacenamiento de clientes JSON",
            description = "Crea las carpetas necesarias para el almacenamiento si no existen.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Almacenamiento inicializado exitosamente"),
                    @ApiResponse(responseCode = "500", description = "Error interno al inicializar el almacenamiento")
            }
    )
    void init();

    /**
     * Almacena un archivo JSON de clientes basado en un identificador GUID.
     *
     * @param guid Identificador único usado para almacenar el recurso JSON.
     * @return Nombre del archivo generado y almacenado.
     */
    @Operation(
            summary = "Almacena un archivo JSON de clientes",
            description = "Almacena un archivo JSON de clientes basado en un identificador único GUID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Archivo almacenado exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Error en el almacenamiento")
            }
    )
    String store(String guid);

    /**
     * Carga todos los recursos almacenados en el almacenamiento de clientes JSON.
     *
     * @return Secuencia de rutas de archivos.
     */
    @Operation(
            summary = "Lista todos los recursos almacenados",
            description = "Devuelve un Stream con las rutas relativas de todos los recursos almacenados.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de archivos devueltos exitosamente")
            }
    )
    Stream<Path> loadAll();

    /**
     * Carga la ruta completa de un recurso específico.
     *
     * @param filename Nombre del archivo que se desea recuperar.
     * @return Ruta completa al recurso.
     */
    @Operation(
            summary = "Carga la ruta de un recurso específico",
            description = "Devuelve la ruta completa de un recurso almacenado basado en su nombre.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso encontrado exitosamente")
            }
    )
    Path load(String filename);

    /**
     * Recupera un recurso como un objeto `Resource`.
     *
     * @param filename Nombre del archivo a recuperar.
     * @return Recurso como objeto de tipo `Resource`.
     */
    @Operation(
            summary = "Obtiene un recurso como Resource",
            description = "Devuelve un recurso almacenado con acceso de tipo Resource para su descarga.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso cargado correctamente"),
                    @ApiResponse(responseCode = "404", description = "Recurso no encontrado")
            }
    )
    Resource loadAsResource(String filename);

    /**
     * Elimina un recurso específico del almacenamiento.
     *
     * @param filename Nombre del recurso que se desea eliminar.
     */
    @Operation(
            summary = "Elimina un recurso específico",
            description = "Elimina un recurso almacenado del almacenamiento JSON de clientes.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso eliminado correctamente"),
                    @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
                    @ApiResponse(responseCode = "500", description = "Error interno al eliminar el recurso")
            }
    )
    void delete(String filename);
}