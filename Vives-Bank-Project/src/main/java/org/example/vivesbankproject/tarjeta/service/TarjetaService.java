package org.example.vivesbankproject.tarjeta.service;

import org.example.vivesbankproject.tarjeta.models.Tarjeta;
import org.example.vivesbankproject.tarjeta.models.Tipo;
import org.example.vivesbankproject.tarjeta.models.TipoTarjeta;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public interface TarjetaService {

    Page<Tarjeta> getAll(Pageable pageable);

    Optional<Tarjeta> getById(UUID id);

    Tarjeta save(Tarjeta tarjeta);

    Tarjeta update(UUID id, Tarjeta tarjetaActualizada);

    Tarjeta deleteById(UUID id);

    TipoTarjeta getTipoTarjetaByNombre(Tipo nombre);
}
