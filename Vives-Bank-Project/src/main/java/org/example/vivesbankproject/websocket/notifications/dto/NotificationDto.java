package org.example.vivesbankproject.websocket.notifications.dto;

import org.bson.types.ObjectId;

public record NotificationDto(
        ObjectId id,
        String idUsuario,
        String cliente,
        Integer totalItems,
        String isDeleted,

        String createdAt,
        String updatedAt
) {
}