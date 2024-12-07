package org.example.vivesbankproject.cuenta.mappers;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.vivesbankproject.cuenta.dto.tipoCuenta.TipoCuentaRequest;
import org.example.vivesbankproject.cuenta.dto.tipoCuenta.TipoCuentaResponse;
import org.example.vivesbankproject.cuenta.dto.tipoCuenta.TipoCuentaResponseCatalogo;
import org.example.vivesbankproject.cuenta.models.TipoCuenta;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Componente responsable de realizar la conversión entre las entidades {@link TipoCuenta} y sus respectivas
 * representaciones en objetos de transferencia como {@link TipoCuentaResponse}, {@link TipoCuentaResponseCatalogo} y solicitudes.
 *
 * <p>Este mapper gestiona la transformación de datos entre la capa de negocio y la capa de presentación,
 * convirtiendo las entidades en respuestas para la API y viceversa.</p>
 *
 * @version 1.0-SNAPSHOT
 * @see TipoCuentaResponse
 * @see TipoCuentaResponseCatalogo
 * @see TipoCuentaRequest
 * @see TipoCuenta
 * @see Component
 * @author Jaime León, Natalia González,
 *         German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 */
@Component
public class TipoCuentaMapper {

    /**
     * Convierte una entidad {@link TipoCuenta} en un objeto de respuesta {@link TipoCuentaResponse}.
     *
     * @param tipoCuenta La entidad {@link TipoCuenta} que se debe convertir
     * @return Un objeto {@link TipoCuentaResponse} que representa la información de la cuenta tipo convertida
     */
    @Schema(description = "Convierte una entidad TipoCuenta en un objeto de respuesta TipoCuentaResponse")
    public TipoCuentaResponse toTipoCuentaResponse(TipoCuenta tipoCuenta) {
        return TipoCuentaResponse.builder()
                .guid(tipoCuenta.getGuid())
                .nombre(tipoCuenta.getNombre())
                .interes(tipoCuenta.getInteres().toString())
                .createdAt(tipoCuenta.getCreatedAt().toString())
                .updatedAt(tipoCuenta.getUpdatedAt().toString())
                .isDeleted(tipoCuenta.getIsDeleted())
                .build();
    }

    /**
     * Convierte una entidad {@link TipoCuenta} en una representación de catálogo {@link TipoCuentaResponseCatalogo}.
     *
     * @param tipoCuenta La entidad {@link TipoCuenta} que se debe convertir
     * @return Un objeto {@link TipoCuentaResponseCatalogo} que contiene los datos necesarios para el catálogo
     */
    @Schema(description = "Convierte una entidad TipoCuenta en un objeto de respuesta de catálogo TipoCuentaResponseCatalogo")
    public TipoCuentaResponseCatalogo toTipoCuentaResponseCatalogo(TipoCuenta tipoCuenta) {
        return TipoCuentaResponseCatalogo.builder()
                .nombre(tipoCuenta.getNombre())
                .interes(tipoCuenta.getInteres().toString())
                .build();
    }

    /**
     * Convierte una solicitud {@link TipoCuentaRequest} en una entidad {@link TipoCuenta}.
     *
     * @param tipoCuentaRequest Objeto {@link TipoCuentaRequest} que contiene la información para crear la entidad
     * @return Una nueva instancia de {@link TipoCuenta} creada con los datos proporcionados en la solicitud
     */
    @Schema(description = "Convierte una solicitud TipoCuentaRequest en una entidad TipoCuenta")
    public TipoCuenta toTipoCuenta(TipoCuentaRequest tipoCuentaRequest) {
        return TipoCuenta.builder()
                .nombre(tipoCuentaRequest.getNombre())
                .interes(tipoCuentaRequest.getInteres())
                .build();
    }

    /**
     * Actualiza una entidad existente {@link TipoCuenta} con los datos de una solicitud {@link TipoCuentaRequest}.
     *
     * @param tipoCuentaRequest Objeto {@link TipoCuentaRequest} que contiene los nuevos datos para actualizar
     * @param tipoCuenta        Instancia existente de la entidad {@link TipoCuenta} que se debe actualizar
     * @return Una nueva instancia de {@link TipoCuenta} con los datos actualizados
     */
    @Schema(description = "Actualiza una entidad TipoCuenta con la información de una solicitud TipoCuentaRequest")
    public TipoCuenta toTipoCuentaUpdate(TipoCuentaRequest tipoCuentaRequest, TipoCuenta tipoCuenta) {
        return TipoCuenta.builder()
                .id(tipoCuenta.getId())
                .guid(tipoCuenta.getGuid())
                .nombre(tipoCuentaRequest.getNombre())
                .interes(tipoCuentaRequest.getInteres())
                .createdAt(tipoCuenta.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .isDeleted(tipoCuenta.getIsDeleted())
                .build();
    }
}