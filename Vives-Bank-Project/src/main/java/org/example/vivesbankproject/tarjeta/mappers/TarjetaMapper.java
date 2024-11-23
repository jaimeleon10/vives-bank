package org.example.vivesbankproject.tarjeta.mappers;

import org.example.vivesbankproject.tarjeta.dto.TarjetaRequestSave;
import org.example.vivesbankproject.tarjeta.dto.TarjetaRequestUpdate;
import org.example.vivesbankproject.tarjeta.dto.TarjetaResponse;
import org.example.vivesbankproject.tarjeta.dto.TarjetaResponsePrivado;
import org.example.vivesbankproject.tarjeta.models.Tarjeta;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TarjetaMapper {

    public TarjetaResponse toTarjetaResponse(Tarjeta tarjeta) {
        return TarjetaResponse.builder()
                .guid(tarjeta.getGuid())
                .numeroTarjeta(tarjeta.getNumeroTarjeta())
                .fechaCaducidad(tarjeta.getFechaCaducidad())
                .limiteDiario(tarjeta.getLimiteDiario())
                .limiteSemanal(tarjeta.getLimiteSemanal())
                .limiteMensual(tarjeta.getLimiteMensual())
                .tipoTarjeta(tarjeta.getTipoTarjeta())
                .createdAt(tarjeta.getCreatedAt())
                .updatedAt(tarjeta.getUpdatedAt())
                .isDeleted(tarjeta.getIsDeleted())
                .build();
    }

    public Tarjeta toTarjeta(TarjetaRequestSave tarjetaRequestSave) {
        return Tarjeta.builder()
                .pin(tarjetaRequestSave.getPin())
                .limiteDiario(tarjetaRequestSave.getLimiteDiario())
                .limiteSemanal(tarjetaRequestSave.getLimiteSemanal())
                .limiteMensual(tarjetaRequestSave.getLimiteMensual())
                .tipoTarjeta(tarjetaRequestSave.getTipoTarjeta())
                .build();
    }

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

    public TarjetaResponsePrivado toTarjetaPrivado(Tarjeta tarjeta) {
        return TarjetaResponsePrivado.builder()
                .guid(tarjeta.getGuid())
                .cvv(tarjeta.getCvv())
                .pin(tarjeta.getPin())
                .build();
    }
}
