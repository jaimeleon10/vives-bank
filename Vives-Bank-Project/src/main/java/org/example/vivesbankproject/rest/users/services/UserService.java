package org.example.vivesbankproject.rest.users.services;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.vivesbankproject.rest.users.dto.UserRequest;
import org.example.vivesbankproject.rest.users.dto.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import java.util.Optional;


/**
 * Servicio para manejar operaciones relacionadas con la gestión de usuarios.
 * Implementa la interfaz {@link UserDetailsService} para la autenticación de usuarios.
 * Contiene métodos para realizar operaciones CRUD, búsquedas por identificador o nombre de usuario,
 * y obtener detalles de los usuarios.
 *
 * @author Jaime León, Natalia González, Germán Fernández, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Service
@Schema(description = "Servicio para operaciones de usuario, incluyendo autenticación y gestión de datos de usuario.")
public interface UserService extends UserDetailsService {

    /**
     * Recupera una página de usuarios con la capacidad de filtrar por nombre de usuario y rol.
     *
     * @param username Filtro opcional para buscar por nombre de usuario.
     * @param rol      Filtro opcional para buscar por rol.
     * @param pageable Parámetros de paginación y orden para la consulta.
     * @return Una página de {@link UserResponse} que contiene los datos de los usuarios.
     */
    @Schema(description = "Obtiene una lista paginada de usuarios con filtros opcionales para nombre de usuario y rol.")
    Page<UserResponse> getAll(Optional<String> username, Optional<String> rol, Pageable pageable);

    /**
     * Recupera un usuario por su identificador GUID.
     *
     * @param guid Identificador único global del usuario.
     * @return El objeto {@link UserResponse} correspondiente al usuario.
     */
    @Schema(description = "Obtiene un usuario por su identificador GUID único.")
    UserResponse getById(String guid);

    /**
     * Recupera un usuario por su nombre de usuario.
     *
     * @param username Nombre de usuario para buscar.
     * @return El objeto {@link UserResponse} correspondiente al usuario.
     */
    @Schema(description = "Obtiene un usuario por su nombre de usuario.")
    UserResponse getByUsername(String username);

    /**
     * Guarda un nuevo usuario en la base de datos.
     *
     * @param user Solicitud con los datos del nuevo usuario.
     * @return El objeto {@link UserResponse} correspondiente al usuario creado.
     */
    @Schema(description = "Crea un nuevo usuario con los datos proporcionados en la solicitud.")
    UserResponse save(UserRequest user);

    /**
     * Actualiza los datos de un usuario existente en la base de datos.
     *
     * @param guid Identificador único global del usuario.
     * @param user Datos actualizados para el usuario.
     * @return El objeto {@link UserResponse} correspondiente al usuario actualizado.
     */
    @Schema(description = "Actualiza los datos de un usuario existente en la base de datos.")
    UserResponse update(String guid, UserRequest user);

    /**
     * Elimina un usuario de la base de datos por su identificador GUID.
     *
     * @param guid Identificador único global del usuario que se desea eliminar.
     */
    @Schema(description = "Elimina un usuario de la base de datos por su identificador GUID único.")
    void deleteById(String guid);

    /**
     * Carga los detalles de usuario necesarios para el proceso de autenticación.
     *
     * @param username Nombre de usuario para autenticar.
     * @return Los detalles del usuario autenticado como {@link UserDetails}.
     */
    @Override
    @Schema(description = "Carga la información de usuario para la autenticación a través de su nombre de usuario.")
    UserDetails loadUserByUsername(String username);
}