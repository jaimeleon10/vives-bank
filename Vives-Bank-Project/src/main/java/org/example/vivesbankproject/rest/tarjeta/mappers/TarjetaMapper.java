package org.example.vivesbankproject.rest.tarjeta.mappers;

import org.example.vivesbankproject.rest.tarjeta.dto.TarjetaRequestSave;
import org.example.vivesbankproject.rest.tarjeta.dto.TarjetaRequestUpdate;
import org.example.vivesbankproject.rest.tarjeta.dto.TarjetaResponse;
import org.example.vivesbankproject.rest.tarjeta.dto.TarjetaResponsePrivado;
import org.example.vivesbankproject.rest.tarjeta.models.Tarjeta;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TarjetaMapper {

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
                .cvv(tarjeta.getCvv().toString())
                .pin(tarjeta.getPin())
                .build();
    }
}
