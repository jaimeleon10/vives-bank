package org.example.vivesbankproject.websocket.notifications.mappers;


import org.example.vivesbankproject.movimientos.models.Movimientos;
import org.example.vivesbankproject.websocket.notifications.dto.NotificationDto;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {
    public NotificationDto toNotificationDto(Movimientos movimientos) {
        return new NotificationDto(
                movimientos.getId(),
                movimientos.getIdUsuario(),
                movimientos.getCliente().toString(),
                movimientos.getTotalItems(),
                movimientos.getIsDeleted().toString(),
                movimientos.getCreatedAt().toString(),
                movimientos.getUpdatedAt().toString()
        );
    }
}
