package org.example.vivesbankproject.tarjeta.service;

import org.example.vivesbankproject.tarjeta.dto.TarjetaRequest;
import org.example.vivesbankproject.tarjeta.models.Tarjeta;
import org.example.vivesbankproject.tarjeta.models.Tipo;
import org.example.vivesbankproject.tarjeta.models.TipoTarjeta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
public interface TarjetaService {

    Page<Tarjeta> getAll(Optional<String> numero, Optional<Integer> cvv, Optional<LocalDate> caducidad, Optional<TipoTarjeta> tipoTarjeta, Optional<Double> limiteDiario, Optional<Double> limiteSemanal, Optional<Double> limiteMensual, Optional<UUID> cuentaId, Pageable pageable);

    Optional<Tarjeta> getById(UUID id);

    Tarjeta save(TarjetaRequest tarjetaRequest);

    Tarjeta update(UUID id, TarjetaRequest tarjetaRequest);

    Tarjeta deleteById(UUID id);

    TipoTarjeta getTipoTarjetaByNombre(Tipo nombre);
}
