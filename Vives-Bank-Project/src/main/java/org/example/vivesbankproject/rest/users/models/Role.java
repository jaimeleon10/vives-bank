package org.example.vivesbankproject.rest.users.models;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Enumeración que representa los roles posibles de un usuario en el sistema.
 * Contiene los distintos niveles de acceso que un usuario puede tener.
 *
 * @author Jaime León, Natalia González, Germán Fernández, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Schema(description = "Roles de usuario disponibles en el sistema con sus distintos niveles de privilegios.")
public enum Role {

    /** Rol estándar para usuarios regulares con acceso limitado */
    @Schema(description = "Rol de usuario estándar con privilegios básicos.")
    USER,

    /** Rol de administrador con privilegios adicionales */
    @Schema(description = "Rol con privilegios de administrador para realizar tareas avanzadas.")
    ADMIN,

    /** Rol de superadministrador con el máximo nivel de privilegio */
    @Schema(description = "Rol con el máximo nivel de privilegio en el sistema para realizar todas las acciones.")
    SUPER_ADMIN
}