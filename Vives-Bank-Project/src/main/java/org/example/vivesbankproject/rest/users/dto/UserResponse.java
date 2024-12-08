package org.example.vivesbankproject.rest.users.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.vivesbankproject.rest.users.models.Role;

import java.io.Serializable;
import java.util.Set;

/**
 * Representa la respuesta para una operación relacionada con el usuario.
 * Contiene información detallada de un usuario en el sistema.
 *
 * @author Jaime León, Natalia González, Germán Fernández, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse implements Serializable {

    /**
     * Identificador único global del usuario.
     */
    @Schema(description = "Identificador único del usuario en el sistema.", example = "123e4567-e89b-12d3-a456-426614174000")
    private String guid;

    /**
     * Nombre de usuario.
     */
    @Schema(description = "Nombre de usuario.", example = "user123")
    private String username;

    /**
     * Contraseña del usuario.
     * Nota: Por motivos de seguridad, las contraseñas no deben enviarse en respuestas reales.
     */
    @Schema(description = "Contraseña del usuario. No debe enviarse en respuestas reales por seguridad.", example = "securePass123")
    private String password;

    /**
     * Lista de roles asignados al usuario.
     */
    @Schema(description = "Lista de roles asignados al usuario.", example = "[\"USER\", \"ADMIN\"]")
    private Set<Role> roles;

    /**
     * Fecha de creación de la cuenta de usuario en formato ISO 8601.
     */
    @Schema(description = "Fecha en la que se creó el usuario en formato ISO 8601.", example = "2023-01-10T15:30:00Z")
    private String createdAt;

    /**
     * Fecha de la última actualización de los datos del usuario en formato ISO 8601.
     */
    @Schema(description = "Fecha en la que se realizó la última actualización de datos del usuario en formato ISO 8601.", example = "2023-12-01T09:45:00Z")
    private String updatedAt;

    /**
     * Indica si el usuario está marcado como eliminado.
     */
    @Schema(description = "Indica si el usuario está marcado como eliminado en el sistema.", example = "false")
    private Boolean isDeleted;
}