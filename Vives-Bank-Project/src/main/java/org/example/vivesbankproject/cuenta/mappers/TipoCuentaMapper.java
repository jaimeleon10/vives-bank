package org.example.vivesbankproject.cuenta.mappers;

import org.example.vivesbankproject.cuenta.dto.tipoCuenta.TipoCuentaRequest;
import org.example.vivesbankproject.cuenta.dto.tipoCuenta.TipoCuentaResponse;
import org.example.vivesbankproject.cuenta.models.TipoCuenta;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TipoCuentaMapper {

    public TipoCuentaResponse toTipoCuentaResponse(TipoCuenta tipoCuenta) {
        return TipoCuentaResponse.builder()
                .guid(tipoCuenta.getGuid())
                .nombre(tipoCuenta.getNombre())
                .interes(tipoCuenta.getInteres())
                .createdAt(tipoCuenta.getCreatedAt())
                .updatedAt(tipoCuenta.getUpdatedAt())
                .isDeleted(tipoCuenta.getIsDeleted())
                .build();
    }

    public TipoCuenta toTipoCuenta(TipoCuentaRequest tipoCuentaRequest) {
        return TipoCuenta.builder()
                .nombre(tipoCuentaRequest.getNombre())
                .interes(tipoCuentaRequest.getInteres())
                .build();
    }

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
