package org.example.vivesbankproject.rest.tarjeta.mappers;

import org.example.vivesbankproject.rest.tarjeta.dto.TarjetaRequestSave;
import org.example.vivesbankproject.rest.tarjeta.dto.TarjetaRequestUpdate;
import org.example.vivesbankproject.rest.tarjeta.dto.TarjetaResponse;
import org.example.vivesbankproject.rest.tarjeta.dto.TarjetaResponsePrivado;
import org.example.vivesbankproject.rest.tarjeta.models.Tarjeta;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Mapeador para convertir objetos de tarjeta entre diferentes representaciones.
 * Esta clase se encarga de transformar entidades de tarjeta a DTOs y viceversa.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Component
@Tag(name = "Tarjeta Mapper", description = "Mapeador de conversión de entidades de tarjeta")
public class TarjetaMapper {

    /**
     * Convierte una entidad Tarjeta a un objeto TarjetaResponse.
     *
     * @param tarjeta Entidad de tarjeta a convertir
     * @return Objeto TarjetaResponse con los datos de la tarjeta
     */
    @Operation(
            summary = "Convertir entidad Tarjeta a TarjetaResponse",
            description = "Transforma una entidad Tarjeta en un objeto de respuesta TarjetaResponse"
    )
    public TarjetaResponse toTarjetaResponse(Tarjeta tarjeta) {
        return TarjetaResponse.builder()
                .guid(tarjeta.getGuid())
                .numeroTarjeta(tarjeta.getNumeroTarjeta())
                .fechaCaducidad(tarjeta.getFechaCaducidad().toString())
                .limiteDiario(tarjeta.getLimiteDiario().toString())
                .limiteSemanal(tarjeta.getLimiteSemanal().toString())
                .limiteMensual(tarjeta.getLimiteMensual().toString())
                .tipoTarjeta(tarjeta.getTipoTarjeta())
                .createdAt(tarjeta.getCreatedAt().toString())
                .updatedAt(tarjeta.getUpdatedAt().toString())
                .isDeleted(tarjeta.getIsDeleted())
                .build();
    }

    /**
     * Convierte un objeto TarjetaRequestSave a una entidad Tarjeta.
     *
     * @param tarjetaRequestSave Objeto de solicitud para crear una tarjeta
     * @return Entidad Tarjeta creada a partir de la solicitud
     */
    @Operation(
            summary = "Convertir TarjetaRequestSave a Tarjeta",
            description = "Transforma un objeto de solicitud de creación en una entidad Tarjeta"
    )
    public Tarjeta toTarjeta(TarjetaRequestSave tarjetaRequestSave) {
        return Tarjeta.builder()
                .pin(tarjetaRequestSave.getPin())
                .limiteDiario(tarjetaRequestSave.getLimiteDiario())
                .limiteSemanal(tarjetaRequestSave.getLimiteSemanal())
                .limiteMensual(tarjetaRequestSave.getLimiteMensual())
                .tipoTarjeta(tarjetaRequestSave.getTipoTarjeta())
                .build();
    }

    /**
     * Actualiza una entidad Tarjeta existente con los datos de una solicitud de actualización.
     *
     * @param tarjetaRequestUpdate Objeto con los datos para actualizar la tarjeta
     * @param tarjeta Entidad de tarjeta existente a actualizar
     * @return Entidad Tarjeta actualizada
     */
    @Operation(
            summary = "Actualizar entidad Tarjeta",
            description = "Actualiza una entidad Tarjeta existente con los datos de una solicitud de actualización"
    )
    public Tarjeta toTarjetaUpdate(TarjetaRequestUpdate tarjetaRequestUpdate, Tarjeta tarjeta) {
        return Tarjeta.builder()
                .id(tarjeta.getId())
                .guid(tarjeta.getGuid())
                .numeroTarjeta(tarjeta.getNumeroTarjeta())
                .fechaCaducidad(tarjeta.getFechaCaducidad())
                .cvv(tarjeta.getCvv())
                .pin(tarjeta.getPin())
                .limiteDiario(tarjetaRequestUpdate.getLimiteDiario())
                .limiteSemanal(tarjetaRequestUpdate.getLimiteSemanal())
                .limiteMensual(tarjetaRequestUpdate.getLimiteMensual())
                .tipoTarjeta(tarjeta.getTipoTarjeta())
                .createdAt(tarjeta.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .isDeleted(tarjetaRequestUpdate.getIsDeleted())
                .build();
    }

    /**
     * Convierte una entidad Tarjeta a un objeto TarjetaResponsePrivado con información sensible.
     *
     * @param tarjeta Entidad de tarjeta a convertir
     * @return Objeto TarjetaResponsePrivado con datos privados de la tarjeta
     */
    @Operation(
            summary = "Convertir a Tarjeta Privada",
            description = "Transforma una entidad Tarjeta en un objeto de respuesta con información privada"
    )
    public TarjetaResponsePrivado toTarjetaPrivado(Tarjeta tarjeta) {
        return TarjetaResponsePrivado.builder()
                .guid(tarjeta.getGuid())
                .cvv(tarjeta.getCvv().toString())
                .pin(tarjeta.getPin())
                .build();
    }
}