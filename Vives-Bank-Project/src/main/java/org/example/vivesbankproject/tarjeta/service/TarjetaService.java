package org.example.vivesbankproject.tarjeta.service;

import org.example.vivesbankproject.tarjeta.models.Tarjeta;
import org.example.vivesbankproject.tarjeta.models.Tipo;
import org.example.vivesbankproject.tarjeta.models.TipoTarjeta;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface TarjetaService {

    @Cacheable(value = "tiposTarjeta", key = "#nombre")
    TipoTarjeta getTipoTarjetaByNombre(Tipo nombre);

    Tarjeta create(Tarjeta tarjeta);

    @Cacheable(value = "tarjetas", key = "#id")
    Tarjeta getById(UUID id);

    Tarjeta update(UUID id, Tarjeta tarjetaActualizada);

    List<Tarjeta> getAll();

    void delete(UUID id);
}
