package org.example.vivesbankproject.tarjeta.service;

import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.tarjeta.exceptions.TarjetaNotFound;
import org.example.vivesbankproject.tarjeta.mappers.TarjetaMapper;
import org.example.vivesbankproject.tarjeta.models.Tarjeta;
import org.example.vivesbankproject.tarjeta.models.Tipo;
import org.example.vivesbankproject.tarjeta.models.TipoTarjeta;
import org.example.vivesbankproject.tarjeta.repositories.TarjetaRepository;
import org.example.vivesbankproject.tarjeta.repositories.TipoTarjetaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;


@Slf4j
@Service
public class TarjetaServiceImpl implements TarjetaService {

    private final TarjetaRepository tarjetaRepository;
    private final TipoTarjetaRepository tipoTarjetaRepository;
    private final TarjetaMapper tarjetaMapper;

    @Autowired
    public TarjetaServiceImpl(TarjetaRepository tarjetaRepository, TipoTarjetaRepository tipoTarjetaRepository, TarjetaMapper tarjetaMapper) {
        this.tarjetaRepository = tarjetaRepository;
        this.tipoTarjetaRepository = tipoTarjetaRepository;
        this.tarjetaMapper = tarjetaMapper;
    }

    @Override
    public Page<Tarjeta> getAll(Pageable pageable) {
        log.info("Obteniendo todas las tarjetas...");
        return tarjetaRepository.findAll(pageable);
    }

    @Override
    public Optional<Tarjeta> getById(UUID id) {
        log.info("Obteniendo la tarjeta con ID: {}", id);
        return tarjetaRepository.findById(id);
    }

    @Override
    public Tarjeta save(Tarjeta tarjeta) {
        log.info("Guardando tarjeta: {}", tarjeta);
        return tarjetaRepository.save(tarjeta);
    }

    @Override
    public Tarjeta update(UUID id, Tarjeta tarjetaActualizada) {
        log.info("Actualizando tarjeta con ID: {}", id);
        var tarjetaExistente = tarjetaRepository.findById(id)
                .orElseThrow(() -> new TarjetaNotFound(id));

        tarjetaExistente.setPin(tarjetaActualizada.getPin());
        tarjetaExistente.setLimiteDiario(tarjetaActualizada.getLimiteDiario());
        tarjetaExistente.setLimiteSemanal(tarjetaActualizada.getLimiteSemanal());
        tarjetaExistente.setLimiteMensual(tarjetaActualizada.getLimiteMensual());
        tarjetaExistente.setTipoTarjeta(tarjetaActualizada.getTipoTarjeta());

        return tarjetaRepository.save(tarjetaExistente);
    }

    @Override
    public Tarjeta deleteById(UUID id) {
        log.info("Eliminando tarjeta con ID: {}", id);
        var tarjetaExistente = tarjetaRepository.findById(id)
                .orElseThrow(() -> new TarjetaNotFound(id));

        tarjetaRepository.delete(tarjetaExistente);
        return tarjetaExistente;
    }


    @Override
    public TipoTarjeta getTipoTarjetaByNombre(Tipo nombre) {
        log.info("Buscando tipo de tarjeta con nombre: {}", nombre);
        return tipoTarjetaRepository.findByNombre(nombre)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de tarjeta no encontrado: " + nombre));
    }
}
