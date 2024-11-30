
package org.example.vivesbankproject.websocket.notifications.mappers;


import org.example.vivesbankproject.movimientos.models.IngresoDeNomina;
import org.example.vivesbankproject.movimientos.models.Movimiento;
import org.example.vivesbankproject.websocket.notifications.dto.IngresoNominaResponse;
import org.example.vivesbankproject.websocket.notifications.dto.NotificationDto;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {
    public IngresoNominaResponse toIngresoNominaDto(IngresoDeNomina data) {
        return new IngresoNominaResponse(
                data.getIban_Destino(),
                data.getCantidad(),
                data.getNombreEmpresa(),
                data.getCifEmpresa()
        );
    }

/*    public NotificationDto toNotificationDto(Movimiento movimiento) {
        return new NotificationDto(
                movimiento.getId(),
                movimiento.getIdUsuario(),
                movimiento.getCliente().toString(),
                movimiento.getTotalItems(),
                movimiento.getIsDeleted().toString(),
                movimiento.getCreatedAt().toString(),
                movimiento.getUpdatedAt().toString()
        );
    }*/
}

