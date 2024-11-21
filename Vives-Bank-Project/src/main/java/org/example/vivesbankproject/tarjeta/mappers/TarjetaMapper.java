package org.example.vivesbankproject.tarjeta.mappers;

import org.example.vivesbankproject.tarjeta.dto.TarjetaRequest;
import org.example.vivesbankproject.tarjeta.dto.TarjetaResponse;
import org.example.vivesbankproject.tarjeta.dto.TarjetaResponseCVV;
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
                .build();
    }

    public Tarjeta toTarjeta(TarjetaRequest tarjetaRequest) {
        return Tarjeta.builder()
                .pin(tarjetaRequest.getPin())
                .limiteDiario(tarjetaRequest.getLimiteDiario())
                .limiteSemanal(tarjetaRequest.getLimiteSemanal())
                .limiteMensual(tarjetaRequest.getLimiteMensual())
                .tipoTarjeta(tarjetaRequest.getTipoTarjeta())
                .build();
    }

    public Tarjeta toTarjetaUpdate(TarjetaRequest tarjetaRequest, Tarjeta tarjeta) {
        return Tarjeta.builder()
                .guid(tarjeta.getGuid())
                .numeroTarjeta(tarjeta.getNumeroTarjeta())
                .fechaCaducidad(tarjeta.getFechaCaducidad())
                .cvv(tarjeta.getCvv())
                .pin(tarjeta.getPin())
                .limiteDiario(tarjetaRequest.getLimiteDiario())
                .limiteSemanal(tarjetaRequest.getLimiteSemanal())
                .limiteMensual(tarjetaRequest.getLimiteMensual())
                .tipoTarjeta(tarjetaRequest.getTipoTarjeta())
                .createdAt(tarjeta.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public TarjetaResponseCVV toTarjetaResponseCVV(Tarjeta tarjeta) {
        return TarjetaResponseCVV.builder()
                .guid(tarjeta.getGuid())
                .numeroTarjeta(tarjeta.getNumeroTarjeta())
                .cvv(tarjeta.getCvv())
                .build();
    }
}
