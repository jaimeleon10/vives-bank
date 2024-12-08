
package org.example.vivesbankproject.websocket.notifications.mappers;


import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.vivesbankproject.movimientos.models.*;
import org.example.vivesbankproject.websocket.notifications.dto.*;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir datos entre entidades y DTOs relacionados con notificaciones, pagos y transferencias.
 *
 * <p>
 * Esta clase se encarga de realizar las conversiones necesarias de las entidades de dominio en sus correspondientes
 * DTOs para enviar respuestas a través de la capa de presentación.
 * </p>
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Component
@Tag(name = "NotificationMapper", description = "Clase para mapear entidades a sus respectivos DTOs")
public class NotificationMapper {

    /**
     * Convierte un objeto `IngresoDeNomina` a su correspondiente DTO `IngresoNominaResponse`.
     *
     * @param data Objeto de tipo `IngresoDeNomina`.
     * @return DTO de tipo `IngresoNominaResponse`.
     */
    public IngresoNominaResponse toIngresoNominaDto(IngresoDeNomina data) {
        return new IngresoNominaResponse(
                data.getIban_Origen(),
                data.getIban_Destino(),
                data.getCantidad(),
                data.getNombreEmpresa(),
                data.getCifEmpresa()
        );
    }

    /**
     * Convierte un objeto `Domiciliacion` a su correspondiente DTO `DomiciliacionResponse`.
     *
     * @param data Objeto de tipo `Domiciliacion`.
     * @return DTO de tipo `DomiciliacionResponse`.
     */
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

    /**
     * Convierte un objeto `Transferencia` a su correspondiente DTO `TransferenciaResponse`.
     *
     * @param data Objeto de tipo `Transferencia`.
     * @return DTO de tipo `TransferenciaResponse`.
     */
    public TransferenciaResponse toTransferenciaDto(Transferencia data) {
        return new TransferenciaResponse(
                data.getIban_Origen(),
                data.getIban_Destino(),
                data.getCantidad(),
                data.getNombreBeneficiario()
        );
    }

    /**
     * Convierte un objeto `PagoConTarjeta` a su correspondiente DTO `PagoConTarjetaResponse`.
     *
     * @param data Objeto de tipo `PagoConTarjeta`.
     * @return DTO de tipo `PagoConTarjetaResponse`.
     */
    public PagoConTarjetaResponse toPagoConTarjetaDto(PagoConTarjeta data) {
        return new PagoConTarjetaResponse(
                data.getNumeroTarjeta(),
                data.getCantidad(),
                data.getNombreComercio()
        );
    }

    /*
     * Convierte un objeto `Movimiento` en un DTO de tipo `NotificationDto`.
     * Esta función está actualmente comentada hasta su implementación completa.
     */

    /*
    public NotificationDto toNotificationDto(Movimiento movimiento) {
        return new NotificationDto(
                movimiento.getId(),
                movimiento.getIdUsuario(),
                movimiento.getCliente().toString(),
                movimiento.getTotalItems(),
                movimiento.getIsDeleted().toString(),
                movimiento.getCreatedAt().toString(),
                movimiento.getUpdatedAt().toString()
        );
    }
    */
}