package org.example.vivesbankproject.tarjeta.service;

import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.tarjeta.models.Tarjeta;
import org.example.vivesbankproject.tarjeta.models.Tipo;
import org.example.vivesbankproject.tarjeta.models.TipoTarjeta;
import org.example.vivesbankproject.tarjeta.repository.TarjetaRepository;
import org.example.vivesbankproject.tarjeta.repository.TipoTarjetaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@CacheConfig(cacheNames = {"tarjetas"})
public class TarjetaServiceImpl implements TarjetaService{

    private final TipoTarjetaRepository tipoTarjetaRepository;

    private final TarjetaRepository tarjetaRepository;

    @Autowired
    public TarjetaServiceImpl(TipoTarjetaRepository tipoTarjetaRepository, TarjetaRepository tarjetaRepository) {
        this.tipoTarjetaRepository = tipoTarjetaRepository;
        this.tarjetaRepository = tarjetaRepository;
    }

    @Cacheable(value = "tiposTarjeta", key = "#nombre")
    @Override
    public TipoTarjeta getTipoTarjetaByNombre(Tipo nombre) {
        log.info("Buscando tipo de tarjeta con nombre: {}", nombre);

        Optional<TipoTarjeta> tipoTarjeta = tipoTarjetaRepository.findByNombre(nombre);

        if (tipoTarjeta.isEmpty()) {
            log.error("Tipo de tarjeta no encontrado para: {}", nombre);
            throw new IllegalArgumentException("Tipo de tarjeta no encontrado: " + nombre);
        }

        return tipoTarjeta.get();
    }

    @Override
    public Tarjeta create(Tarjeta tarjeta) {
        log.info("Creando nueva tarjeta: {}", tarjeta);
        return tarjetaRepository.save(tarjeta);
    }

    @Cacheable(value = "tarjetas", key = "#id")
    @Override
    public Tarjeta getById(UUID id) {
        log.info("Buscando tarjeta con ID: {}", id);
        Optional<Tarjeta> tarjeta = tarjetaRepository.findById(id);

        if (tarjeta.isEmpty()) {
            log.error("Tarjeta no encontrada para ID: {}", id);
            throw new IllegalArgumentException("Tarjeta no encontrada con ID: " + id);
        }

        return tarjeta.get();
    }

    @Override
    public Tarjeta update(UUID id, Tarjeta tarjetaActualizada) {
        log.info("Actualizando tarjeta con ID: {}", id);
        Optional<Tarjeta> tarjetaExistente = tarjetaRepository.findById(id);

        if (tarjetaExistente.isEmpty()) {
            log.error("Tarjeta no encontrada para actualizar con ID: {}", id);
            throw new IllegalArgumentException("Tarjeta no encontrada para actualizar con ID: " + id);
        }

        Tarjeta tarjeta = tarjetaExistente.get();
        tarjeta.setNumeroTarjeta(tarjetaActualizada.getNumeroTarjeta());
        tarjeta.setFechaCaducidad(tarjetaActualizada.getFechaCaducidad());
        tarjeta.setCvv(tarjetaActualizada.getCvv());
        tarjeta.setPin(tarjetaActualizada.getPin());
        tarjeta.setLimiteDiario(tarjetaActualizada.getLimiteDiario());
        tarjeta.setLimiteSemanal(tarjetaActualizada.getLimiteSemanal());
        tarjeta.setLimiteMensual(tarjetaActualizada.getLimiteMensual());
        tarjeta.setTipoTarjeta(tarjetaActualizada.getTipoTarjeta());
        tarjeta.setCuenta(tarjetaActualizada.getCuenta());

        return tarjetaRepository.save(tarjeta);
    }

    @Override
    public List<Tarjeta> getAll() {
        log.info("Buscando todas las tarjetas");
        return tarjetaRepository.findAll();
    }

    @Override
    public void delete(UUID id) {
        log.info("Eliminando tarjeta con ID: {}", id);
        Optional<Tarjeta> tarjetaExistente = tarjetaRepository.findById(id);

        if (tarjetaExistente.isEmpty()) {
            log.error("Tarjeta no encontrada para eliminar con ID: {}", id);
            throw new IllegalArgumentException("Tarjeta no encontrada para eliminar con ID: " + id);
        }

        tarjetaRepository.delete(tarjetaExistente.get());
    }
}
