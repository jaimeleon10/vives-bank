package org.example.vivesbankproject.tarjeta.mappers;

import org.example.vivesbankproject.tarjeta.dto.TarjetaRequest;
import org.example.vivesbankproject.tarjeta.models.Tarjeta;
import org.example.vivesbankproject.tarjeta.models.Tipo;
import org.example.vivesbankproject.tarjeta.models.TipoTarjeta;
import org.example.vivesbankproject.tarjeta.service.TarjetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TarjetaMapper {

    private final TarjetaService tarjetaService;

    @Autowired
    public TarjetaMapper(TarjetaService tarjetaService) {
        this.tarjetaService = tarjetaService;
    }

    public Tarjeta toTarjeta(TarjetaRequest request) {
        TipoTarjeta tipoTarjeta = tarjetaService.getTipoTarjetaByNombre(Tipo.valueOf(request.getTipoTarjeta()));

        return Tarjeta.builder()
                .id(UUID.randomUUID())
                .numeroTarjeta(request.getNumeroTarjeta())
                .fechaCaducidad(request.getFechaCaducidad())
                .cvv(request.getCvv())
                .pin(request.getPin())
                .limiteDiario(request.getLimiteDiario())
                .limiteSemanal(request.getLimiteSemanal())
                .limiteMensual(request.getLimiteMensual())
                .tipoTarjeta(tipoTarjeta)
                .cuenta(null)
                .build();
    }
}
