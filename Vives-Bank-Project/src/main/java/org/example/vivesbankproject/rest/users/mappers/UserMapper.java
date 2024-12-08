package org.example.vivesbankproject.rest.users.mappers;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.vivesbankproject.rest.users.dto.UserRequest;
import org.example.vivesbankproject.rest.users.dto.UserResponse;
import org.example.vivesbankproject.rest.users.models.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Clase encargada de realizar la transformación entre objetos de tipo {@link User}, {@link UserRequest}, y {@link UserResponse}.
 * Se encarga de mapear datos entre capas para facilitar el proceso de conversión de solicitudes a entidades y respuestas a objetos DTO.
 *
 * @author Jaime León, Natalia González, Germán Fernández, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Component
public class UserMapper {

    /**
     * Convierte un objeto {@link User} en un objeto {@link UserResponse}.
     * Convierte la información de la entidad para enviarla en la capa de respuestas.
     *
     * @param user El objeto de tipo {@link User} que se desea transformar.
     * @return Un objeto de tipo {@link UserResponse} con los campos mapeados.
     */
    @Schema(description = "Convierte un objeto User en un objeto UserResponse para enviar la información al cliente.")
    public UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .guid(user.getGuid())
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRoles())
                .createdAt(user.getCreatedAt().toString())
                .updatedAt(user.getUpdatedAt().toString())
                .isDeleted(user.getIsDeleted())
                .build();
    }

    /**
     * Convierte un objeto {@link UserRequest} en un objeto {@link User}.
     * Convierte la solicitud recibida en la capa de petición en la entidad correspondiente.
     *
     * @param request El objeto {@link UserRequest} que contiene la información de entrada del usuario.
     * @return El objeto {@link User} mapeado con la información de la solicitud.
     */
    @Schema(description = "Convierte un objeto UserRequest en un objeto User para almacenamiento en la base de datos.")
    public User toUser(UserRequest request) {
        return User.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .build();
    }

    /**
     * Convierte un objeto {@link UserRequest} y un objeto {@link User} existente en un nuevo objeto {@link User}.
     * Se utiliza para la actualización de usuarios con información proveniente de una solicitud.
     *
     * @param userRequest La solicitud con la información actualizada para el usuario.
     * @param user El objeto {@link User} existente que se desea actualizar.
     * @return El nuevo objeto {@link User} con los campos actualizados.
     */
    @Schema(description = "Convierte un objeto UserRequest y el objeto User existente en un nuevo objeto User para realizar actualizaciones.")
    public User toUserUpdate(UserRequest userRequest, User user) {
        return User.builder()
                .id(user.getId())
                .guid(user.getGuid())
                .username(userRequest.getUsername())
                .password(userRequest.getPassword())
                .roles(userRequest.getRoles())
                .createdAt(user.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .isDeleted(userRequest.getIsDeleted())
                .build();
    }
}