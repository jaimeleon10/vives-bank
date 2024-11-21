package org.example.vivesbankproject.tarjeta.service;

import org.example.vivesbankproject.tarjeta.dto.TarjetaRequest;
import org.example.vivesbankproject.tarjeta.dto.TarjetaResponse;
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

    TarjetaResponse getById(String id);

    TarjetaResponse save(TarjetaRequest tarjetaRequest);

    TarjetaResponse update(String id, TarjetaRequest tarjetaRequest);

    TarjetaResponse deleteById(String id);
}