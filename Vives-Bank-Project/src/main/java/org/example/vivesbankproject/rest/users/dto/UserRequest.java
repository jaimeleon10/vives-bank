package org.example.vivesbankproject.rest.users.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.vivesbankproject.rest.users.models.Role;
import org.hibernate.validator.constraints.Length;
import java.util.Set;

/**
 * Representa una solicitud para la creación o actualización de un usuario en la aplicación.
 * Esta clase contiene los datos requeridos para la operación de usuario.
 *
 * @author Jaime León, Natalia González, Germán Fernández, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

    /**
     * Nombre de usuario para la solicitud.
     * Este campo no puede estar vacío.
     */
    @Schema(description = "Nombre de usuario para la solicitud. Este campo no puede estar vacío.", example = "user123")
    @NotBlank(message = "Username no puede estar vacio")
    private String username;

    /**
     * Contraseña para el usuario.
     * Debe tener al menos 5 caracteres.
     */
    @Schema(description = "Contraseña para el usuario. Debe tener al menos 5 caracteres.", example = "securePass123")
    @NotBlank(message = "Password no puede estar vacio")
    @Length(min = 5, message = "Password debe tener al menos 5 caracteres")
    private String password;

    /**
     * Roles asignados al usuario.
     * Se cargan de forma eager por defecto y tienen un valor por defecto de 'USER' si no se especifican otros roles.
     */
    @Schema(description = "Lista de roles asignados al usuario. Por defecto, contiene el rol USER.", example = "[\"USER\"]")
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Set<Role> roles = Set.of(Role.USER);

    /**
     * Indica si el usuario está marcado como eliminado.
     * Por defecto es falso.
     */
    @Schema(description = "Indica si el usuario está marcado como eliminado. Por defecto es false.", example = "false")
    @Builder.Default
    private Boolean isDeleted = false;
}