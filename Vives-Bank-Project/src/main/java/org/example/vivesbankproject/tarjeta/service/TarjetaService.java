package org.example.vivesbankproject.tarjeta.service;

import org.example.vivesbankproject.tarjeta.dto.TarjetaRequest;
import org.example.vivesbankproject.tarjeta.dto.TarjetaResponse;
import org.example.vivesbankproject.tarjeta.models.Tipo;
import org.example.vivesbankproject.tarjeta.models.TipoTarjeta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface TarjetaService {

    Page<TarjetaResponse> getAll(Optional<String> numero, Optional<LocalDate> caducidad,
                                 Optional<TipoTarjeta> tipoTarjeta,
                                 Optional<Double> limiteDiario,
                                 Optional<Double> limiteSemanal,
                                 Optional<Double> limiteMensual,
                                 Pageable pageable);

    TarjetaResponse getById(UUID id);

    TarjetaResponse save(TarjetaRequest tarjetaRequest);

    TarjetaResponse update(UUID id, TarjetaRequest tarjetaRequest);

    TarjetaResponse deleteById(UUID id);
}