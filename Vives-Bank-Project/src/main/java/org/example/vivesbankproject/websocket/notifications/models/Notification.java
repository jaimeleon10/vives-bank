package org.example.vivesbankproject.websocket.notifications.models;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

/**
 * Clase genérica para representar una notificación con tipo, entidad y fecha de creación.
 *
 * @param <T> Tipo de dato que contendrá la información de la notificación.
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
public record Notification<T>(

        @Schema(description = "Nombre de la entidad asociada a la notificación", example = "Usuarios")
        String entity,          // Nombre de la entidad asociada a la notificación

        @Schema(description = "Tipo de operación asociada a la notificación", example = "CREATE")
        Tipo type,             // Tipo de operación asociada a la notificación

        @Schema(description = "Datos relevantes para la operación", example = "{\"id\":\"12345\"}")
        T data,                // Datos relevantes para la operación

        @Schema(description = "Fecha de creación de la notificación", example = "2023-12-08T10:15:30Z")
        String createdAt       // Fecha de creación de la notificación
) implements Serializable {

    /**
     * Enumeración para definir los tipos de operaciones en las notificaciones.
     */
    @Schema(description = "Tipos posibles de operación para la notificación")
    public enum Tipo {

        /** Operación de creación */
        @Schema(description = "Operación de creación")
        CREATE,

        /** Operación de actualización */
        @Schema(description = "Operación de actualización")
        UPDATE,

        /** Operación de eliminación */
        @Schema(description = "Operación de eliminación")
        DELETE
    }
}