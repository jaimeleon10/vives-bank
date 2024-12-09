package org.example.vivesbankproject.rest.users.services;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.rest.users.dto.UserRequest;
import org.example.vivesbankproject.rest.users.dto.UserResponse;
import org.example.vivesbankproject.rest.users.exceptions.UserExists;
import org.example.vivesbankproject.rest.users.exceptions.UserNotFoundById;
import org.example.vivesbankproject.rest.users.exceptions.UserNotFoundByUsername;
import org.example.vivesbankproject.rest.users.exceptions.UserNotFoundException;
import org.example.vivesbankproject.rest.users.mappers.UserMapper;
import org.example.vivesbankproject.rest.users.models.Role;
import org.example.vivesbankproject.rest.users.models.User;
import org.example.vivesbankproject.rest.users.repositories.UserRepository;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;
/**
 * Implementación de la interfaz {@link UserService} para la lógica de negocio de usuarios.
 * Esta clase se encarga de interactuar con el repositorio y el mapeo de datos entre entidades y DTOs.
 * Se incluyen operaciones CRUD, búsquedas avanzadas, gestión de caché y soporte para autenticación de usuarios.
 *
 * @author Jaime León, Natalia González, Germán Fernández, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Slf4j
@Service
@Primary
@CacheConfig(cacheNames = {"usuario"})
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    /**
     * Constructor de la clase UserServiceImpl.
     *
     * @param userRepository Repositorio para interactuar con la base de datos.
     * @param userMapper     Manejador para mapear entre entidades y DTOs.
     */
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }
    /**
     * Recupera una lista paginada de usuarios con la opción de realizar filtros por nombre de usuario o rol.
     *
     * @param username Filtro opcional por nombre de usuario.
     * @param roles    Filtro opcional por rol.
     * @param pageable Parámetros de paginación.
     * @return Una página de respuestas de usuarios.
     */
    @Override
    @Operation(
            summary = "Obtener todos los usuarios",
            description = "Recupera una lista paginada de usuarios con opciones de filtrado avanzado"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de usuarios recuperada exitosamente",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parámetros de búsqueda inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Page<UserResponse> getAll(Optional<String> username, Optional<String> roles, Pageable pageable) {
        log.info("Obteniendo todos los usuarios");
        Specification<User> specUsername = (root, query, criteriaBuilder) ->
                username.map(u -> criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), "%" + u.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<User> specRole = (root, query, criteriaBuilder) ->
                roles.map(r -> {
                    try {
                        return criteriaBuilder.isMember(Role.valueOf(r.toUpperCase()), root.get("roles"));
                    } catch (IllegalArgumentException e) {
                        return criteriaBuilder.isFalse(criteriaBuilder.literal(true));
                    }
                }).orElse(null);

        Specification<User> criterio = Specification.where(specUsername).and(specRole);

        Page<User> userPage = userRepository.findAll(criterio, pageable);

        return userPage.map(userMapper::toUserResponse);
    }
    /**
     * Obtiene la información de un usuario utilizando su identificador único (GUID).
     *
     * @param id Identificador único del usuario.
     * @return Información del usuario como un objeto UserResponse.
     */
    @Override
    @Cacheable
    @Operation(
            summary = "Obtener usuario por ID",
            description = "Recupera un usuario específico utilizando su identificador único (GUID)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario recuperado exitosamente",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public UserResponse getById(String id) {
        log.info("Obteniendo usuarios por id: {}", id);
        var user = userRepository.findByGuid(id).orElseThrow(() -> new UserNotFoundById(id));
        return userMapper.toUserResponse(user);
    }
    /**
     * Obtiene la información de un usuario utilizando su nombre de usuario.
     *
     * @param username Nombre de usuario para buscar.
     * @return Información del usuario como un objeto UserResponse.
     */
    @Override
    @Cacheable
    @Operation(
            summary = "Obtener usuario por nombre de usuario",
            description = "Recupera un usuario específico utilizando el nombre de usuario"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario recuperado exitosamente",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public UserResponse getByUsername(String username) {
        log.info("Obteniendo usuarios por username: {}", username);
        var user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundByUsername(username));
        return userMapper.toUserResponse(user);
    }
    /**
     * Guarda un nuevo usuario en la base de datos.
     *
     * @param userRequest Información del usuario a guardar.
     * @return Respuesta con la información del usuario guardado.
     */
    @Override
    @CachePut
    @Operation(
            summary = "Guardar un nuevo usuario",
            description = "Crea y guarda un nuevo usuario en la base de datos"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "El nombre de usuario ya existe"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public UserResponse save(UserRequest userRequest) {
        log.info("Guardando usuario");
        if (userRepository.findByUsername(userRequest.getUsername()).isPresent()) {
            throw new UserExists(userRequest.getUsername());
        }
        var user = userRepository.save(userMapper.toUser(userRequest));
        return userMapper.toUserResponse(user);
    }
    /**
     * Actualiza la información de un usuario existente en la base de datos.
     *
     * @param id          Identificador único del usuario.
     * @param userRequest Información actualizada para el usuario.
     * @return Respuesta con la información actualizada del usuario.
     */
    @Override
    @CachePut
    @Operation(
            summary = "Actualizar un usuario",
            description = "Actualiza la información de un usuario existente en la base de datos"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "400", description = "El nombre de usuario ya existe"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public UserResponse update(String id, UserRequest userRequest) {
        log.info("Actualizando usuario con id: {}", id);
        var user = userRepository.findByGuid(id).orElseThrow(
                () -> new UserNotFoundById(id)
        );
        if (userRepository.findByUsername(userRequest.getUsername()).isPresent()) {
            throw new UserExists(userRequest.getUsername());
        }
        var userUpdated = userRepository.save(userMapper.toUserUpdate(userRequest, user));
        return userMapper.toUserResponse(userUpdated);
    }
    /**
     * Marca un usuario como eliminado en la base de datos.
     *
     * @param id Identificador único del usuario a eliminar.
     */
    @Override
    @CacheEvict
    @Operation(
            summary = "Eliminar usuario",
            description = "Marca un usuario como eliminado en la base de datos"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario marcado como eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public void deleteById(String id) {
        log.info("Borrando usuario con id: {}", id);
        var user = userRepository.findByGuid(id).orElseThrow(
                () -> new UserNotFoundById(id)
        );
        user.setIsDeleted(true);
        userRepository.save(user);
    }
    @Override
/**
 * Carga los detalles del usuario basado en el nombre de usuario.
 * <p>
 * Este método se utiliza para la autenticación y carga de datos de usuario en el contexto de seguridad.
 * Si el usuario no existe en la base de datos, se lanzará una excepción {@link UserNotFoundException}.
 * </p>
 *
 * @param username El nombre de usuario que se utilizará para buscar al usuario en la base de datos.
 * @return {@link UserDetails} Detalles del usuario cargado.
 * @throws UserNotFoundException Si el usuario con el nombre de usuario no existe en la base de datos.
 */
    @Operation(
            summary = "Cargar usuario por nombre de usuario",
            description = "Carga los detalles del usuario utilizando el nombre de usuario para autenticación"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario cargado exitosamente",
                    content = @Content(schema = @Schema(implementation = UserDetails.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public UserDetails loadUserByUsername(String username)  {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException( username ));
    }
}
