
package org.example.vivesbankproject.websocket.notifications.mappers;


import org.example.vivesbankproject.movimientos.models.*;
import org.example.vivesbankproject.websocket.notifications.dto.*;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {
    public IngresoNominaResponse toIngresoNominaDto(IngresoDeNomina data) {
        return new IngresoNominaResponse(
                data.getIban_Origen(),
                data.getIban_Destino(),
                data.getCantidad(),
                data.getNombreEmpresa(),
                data.getCifEmpresa()
        );
    }

    public DomiciliacionResponse toDomiciliacionDto(Domiciliacion data) {
        return new DomiciliacionResponse(
                data.getGuid(),
                data.getIbanOrigen(),
                data.getIbanDestino(),
                data.getCantidad(),
                data.getNombreAcreedor(),
                data.getFechaInicio().toString(),
                data.getPeriodicidad().toString(),
                data.getActiva(),
                data.getUltimaEjecucion().toString()
        );
    }

    public TransferenciaResponse toTransferenciaDto(Transferencia data) {
        return new TransferenciaResponse(
                data.getIban_Origen(),
                data.getIban_Destino(),
                data.getCantidad(),
                data.getNombreBeneficiario()
        );
    }

    public PagoConTarjetaResponse toPagoConTarjetaDto(PagoConTarjeta data) {
        return new PagoConTarjetaResponse(
                data.getNumeroTarjeta(),
                data.getCantidad(),
                data.getNombreComercio()
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

