package org.example.vivesbankproject.rest.cliente.service;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.vivesbankproject.rest.cliente.dto.ClienteProducto;
import org.example.vivesbankproject.rest.cliente.dto.ClienteRequestSave;
import org.example.vivesbankproject.rest.cliente.dto.ClienteRequestUpdate;
import org.example.vivesbankproject.rest.cliente.dto.ClienteResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

/**
 * Servicio para gestionar la lógica de negocio relacionada con la entidad Cliente.
 * Proporciona métodos para realizar operaciones CRUD, actualizaciones, búsqueda avanzada, operaciones de borrado lógico,
 * así como interacciones con funcionalidades como el derecho al olvido y la administración de fotos de perfil.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
public interface ClienteService {

    /**
     * Obtiene una lista paginada de clientes con filtros opcionales por DNI, nombre, apellidos, email y teléfono.
     *
     * @param dni Filtro opcional por DNI del cliente.
     * @param nombre Filtro opcional por nombre del cliente.
     * @param apellidos Filtro opcional por apellidos del cliente.
     * @param email Filtro opcional por el correo electrónico del cliente.
     * @param telefono Filtro opcional por el número de teléfono del cliente.
     * @param pageable Parámetros de paginación para la consulta.
     * @return Una página de objetos {@link ClienteResponse} con los resultados de la búsqueda.
     */
    @Schema(description = "Obtiene una lista paginada de clientes con filtros opcionales.")
    Page<ClienteResponse> getAll(
            @Schema(description = "Filtro opcional por el número de identificación del cliente (DNI).") Optional<String> dni,
            @Schema(description = "Filtro opcional por el nombre del cliente.") Optional<String> nombre,
            @Schema(description = "Filtro opcional por los apellidos del cliente.") Optional<String> apellidos,
            @Schema(description = "Filtro opcional por el correo electrónico del cliente.") Optional<String> email,
            @Schema(description = "Filtro opcional por el número de teléfono del cliente.") Optional<String> telefono,
            Pageable pageable
    );

    /**
     * Obtiene un cliente por su identificador único.
     *
     * @param id El identificador único del cliente.
     * @return El objeto {@link ClienteResponse} correspondiente al cliente.
     */
    @Schema(description = "Obtiene un cliente por su identificador único.")
    ClienteResponse getById(@Schema(description = "Identificador único del cliente.") String id);

    /**
     * Obtiene un cliente por su DNI.
     *
     * @param dni El número de DNI del cliente.
     * @return El objeto {@link ClienteResponse} correspondiente al cliente.
     */
    @Schema(description = "Obtiene un cliente por su DNI.")
    ClienteResponse getByDni(@Schema(description = "Número de identificación del cliente.") String dni);

    /**
     * Guarda un nuevo cliente en la base de datos.
     *
     * @param cliente El objeto con los datos del nuevo cliente para ser guardado.
     * @return El objeto {@link ClienteResponse} correspondiente al cliente recién creado.
     */
    @Schema(description = "Crea y guarda un nuevo cliente.")
    ClienteResponse save(@Schema(description = "Datos necesarios para guardar el cliente.") ClienteRequestSave cliente);

    /**
     * Actualiza un cliente existente.
     *
     * @param id El identificador único del cliente a actualizar.
     * @param clienteRequestUpdate El objeto con los datos actualizados para el cliente.
     * @return El objeto {@link ClienteResponse} correspondiente al cliente actualizado.
     */
    @Schema(description = "Actualiza un cliente existente en la base de datos.")
    ClienteResponse update(
            @Schema(description = "Identificador único del cliente a actualizar.") String id,
            @Schema(description = "Datos con las actualizaciones para el cliente.") ClienteRequestUpdate clienteRequestUpdate
    );

    /**
     * Elimina un cliente de forma lógica usando su identificador.
     *
     * @param id El identificador único del cliente que se eliminará de forma lógica.
     */
    @Schema(description = "Elimina un cliente de forma lógica.")
    void deleteById(@Schema(description = "Identificador único del cliente que se eliminará.") String id);

    /**
     * Recupera un cliente autenticado por su GUID.
     *
     * @param userGuid El GUID del usuario autenticado.
     * @return El objeto {@link ClienteResponse} correspondiente al cliente autenticado.
     */
    @Schema(description = "Obtiene el cliente autenticado por su GUID.")
    ClienteResponse getUserAuthenticatedByGuid(@Schema(description = "GUID del usuario autenticado.") String userGuid);

    /**
     * Actualiza los datos de un cliente autenticado.
     *
     * @param userGuid El GUID del usuario autenticado.
     * @param clienteRequestUpdate El objeto con los datos actualizados para el cliente.
     * @return El objeto {@link ClienteResponse} correspondiente al cliente actualizado.
     */
    @Schema(description = "Actualiza la información del cliente autenticado.")
    ClienteResponse updateUserAuthenticated(
            @Schema(description = "GUID del usuario autenticado.") String userGuid,
            @Schema(description = "Datos con las actualizaciones para el cliente.") ClienteRequestUpdate clienteRequestUpdate
    );

    /**
     * Permite al cliente ejercer su derecho al olvido, eliminando su información de la base de datos.
     *
     * @param userGuid El identificador GUID del usuario que solicita el derecho al olvido.
     * @return Un mensaje de confirmación sobre la operación.
     */
    @Schema(description = "Ejecuta la solicitud del derecho al olvido para un usuario.")
    String derechoAlOlvido(@Schema(description = "Identificador GUID del cliente.") String userGuid);

    /**
     * Actualiza la foto de identidad (DNI) de un cliente.
     *
     * @param id El identificador único del cliente.
     * @param file El archivo que contiene la foto actualizada del DNI.
     * @return El objeto {@link ClienteResponse} con la información actualizada.
     */
    @Schema(description = "Actualiza la foto del DNI para un cliente.")
    ClienteResponse updateDniFoto(
            @Schema(description = "Identificador único del cliente.") String id,
            @Schema(description = "Archivo con la foto actualizada.") MultipartFile file
    );

    /**
     * Actualiza la foto de perfil de un cliente.
     *
     * @param id El identificador único del cliente.
     * @param file El archivo que contiene la foto de perfil actualizada.
     * @return El objeto {@link ClienteResponse} con la información actualizada.
     */
    @Schema(description = "Actualiza la foto de perfil de un cliente.")
    ClienteResponse updateProfileFoto(
            @Schema(description = "Identificador único del cliente.") String id,
            @Schema(description = "Archivo con la foto de perfil actualizada.") MultipartFile file
    );

    /**
     * Obtiene la información del catálogo de productos relacionados con el cliente.
     *
     * @return El objeto {@link ClienteProducto} con la información del catálogo.
     */
    @Schema(description = "Obtiene el catálogo de productos disponibles.")
    ClienteProducto getCatalogue();
}