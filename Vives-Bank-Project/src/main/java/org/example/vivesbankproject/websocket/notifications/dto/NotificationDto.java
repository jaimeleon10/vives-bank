package org.example.vivesbankproject.websocket.notifications.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.bson.types.ObjectId;

/**
 * Clase que representa un DTO para la notificación.
 *
 * <p>
 * Este objeto de transferencia de datos (DTO) contiene la información necesaria para identificar,
 * registrar y manejar notificaciones en la aplicación, con campos relacionados al usuario, el cliente,
 * los elementos relacionados, la marca de borrado lógico, y las fechas de creación y actualización.
 * </p>
 *  @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 *  @version 1.0-SNAPSHOT
 */
@Tag(name = "NotificationDto", description = "Clase DTO para transferir información de notificaciones")
public record NotificationDto(

        /** Identificador único de la notificación */
        @Schema(description = "Identificador único de la notificación", example = "60f7e8e0b4d15f5b8c21b4d3")
        ObjectId id,

        /** Identificador del usuario al que está asociada la notificación */
        @Schema(description = "Identificador del usuario relacionado con la notificación", example = "user12345")
        String idUsuario,

        /** Nombre del cliente al que la notificación está asociada */
        @Schema(description = "Nombre del cliente relacionado con la notificación", example = "Cliente XYZ")
        String cliente,

        /** Cantidad total de elementos asociados a la notificación */
        @Schema(description = "Total de elementos relacionados con la notificación", example = "10")
        Integer totalItems,

        /** Indicador para saber si la notificación ha sido eliminada de manera lógica */
        @Schema(description = "Indica si la notificación está marcada como eliminada", example = "true")
        String isDeleted,

        /** Fecha de creación de la notificación en formato ISO 8601 */
        @Schema(description = "Fecha de creación de la notificación", example = "2023-11-25T12:30:00Z")
        String createdAt,

        /** Fecha de última actualización de la notificación en formato ISO 8601 */
        @Schema(description = "Fecha de última actualización de la notificación", example = "2023-12-01T14:00:00Z")
        String updatedAt
) {}