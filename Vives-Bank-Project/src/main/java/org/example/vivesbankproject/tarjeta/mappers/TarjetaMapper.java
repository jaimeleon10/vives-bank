package org.example.vivesbankproject.tarjeta.mappers;

import org.example.vivesbankproject.tarjeta.dto.TarjetaRequest;
import org.example.vivesbankproject.tarjeta.dto.TarjetaResponse;
import org.example.vivesbankproject.tarjeta.models.Tarjeta;
import org.springframework.stereotype.Component;

@Component
public class TarjetaMapper {

    public Tarjeta toTarjeta(TarjetaRequest request) {
        return Tarjeta.builder()
                .numeroTarjeta(request.getNumeroTarjeta())
                .fechaCaducidad(request.getFechaCaducidad())
                .cvv(request.getCvv())
                .pin(request.getPin())
                .limiteDiario(request.getLimiteDiario())
                .limiteSemanal(request.getLimiteSemanal())
                .limiteMensual(request.getLimiteMensual())
                .tipoTarjeta(request.getTipoTarjeta())
                .build();
    }

    public TarjetaResponse toTarjetaResponse(Tarjeta tarjeta) {
        return TarjetaResponse.builder()
                .guid(tarjeta.getGuid())
                .numeroTarjeta(tarjeta.getNumeroTarjeta())
                .fechaCaducidad(tarjeta.getFechaCaducidad())
                .cvv(tarjeta.getCvv())
                .limiteDiario(tarjeta.getLimiteDiario())
                .limiteSemanal(tarjeta.getLimiteSemanal())
                .limiteMensual(tarjeta.getLimiteMensual())
                .tipoTarjeta(tarjeta.getTipoTarjeta())
                .createdAt(tarjeta.getCreatedAt())
                .updatedAt(tarjeta.getUpdatedAt())
                .build();
    }

    public TarjetaRequest toRequest(Tarjeta tarjeta) {

        return TarjetaRequest.builder()
                .numeroTarjeta(tarjeta.getNumeroTarjeta())
                .fechaCaducidad(tarjeta.getFechaCaducidad())
                .cvv(tarjeta.getCvv())
                .pin(tarjeta.getPin())
                .limiteDiario(tarjeta.getLimiteDiario())
                .limiteSemanal(tarjeta.getLimiteSemanal())
                .limiteMensual(tarjeta.getLimiteMensual())
                .tipoTarjeta(tarjeta.getTipoTarjeta())
                .build();
    }
}
