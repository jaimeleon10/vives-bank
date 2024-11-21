package org.example.vivesbankproject.tarjeta.service;

import org.example.vivesbankproject.tarjeta.dto.TarjetaRequest;
import org.example.vivesbankproject.tarjeta.dto.TarjetaRequestUpdate;
import org.example.vivesbankproject.tarjeta.dto.TarjetaResponse;
import org.example.vivesbankproject.tarjeta.dto.TarjetaResponseCVV;
import org.example.vivesbankproject.tarjeta.models.Tarjeta;
import org.example.vivesbankproject.tarjeta.models.TipoTarjeta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface TarjetaService {

    Page<Tarjeta> getAll(Optional<String> numero,
                         Optional<LocalDate> caducidad,
                         Optional<TipoTarjeta> tipoTarjeta,
                         Optional<BigDecimal> minLimiteDiario,
                         Optional<BigDecimal> maxLimiteDiario,
                         Optional<BigDecimal> minLimiteSemanal,
                         Optional<BigDecimal> maxLimiteSemanal,
                         Optional<BigDecimal> minLimiteMensual,
                         Optional<BigDecimal> maxLimiteMensual,
                         Pageable pageable);

    TarjetaResponse getById(String id);

    TarjetaResponseCVV getCVV(String id);

    TarjetaResponse save(TarjetaRequest tarjetaRequest);

    TarjetaResponse update(String id, TarjetaRequestUpdate tarjetaRequestUpdate);

    TarjetaResponse deleteById(String id);
}