package org.example.vivesbankproject.cuenta.services;

import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.cuenta.exceptions.CuentaExists;
import org.example.vivesbankproject.cuenta.exceptions.CuentaNotFound;
import org.example.vivesbankproject.cuenta.exceptions.TipoCuentaNotFound;
import org.example.vivesbankproject.cuenta.models.Cuenta;
import org.example.vivesbankproject.cuenta.models.TipoCuenta;
import org.example.vivesbankproject.cuenta.repositories.TipoCuentaRepository;
import org.example.vivesbankproject.tarjeta.models.Tipo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class TipoCuentaServiceImpl implements TipoCuentaService {
    private final TipoCuentaRepository tipoCuentaRepository;

    @Autowired
    public TipoCuentaServiceImpl(TipoCuentaRepository tipoCuentaRepository) {
        this.tipoCuentaRepository = tipoCuentaRepository;
    }

    @Override
    public Page<TipoCuenta> getAll(Optional<String> nombre, Optional<BigDecimal> interes, Pageable pageable) {
        log.info("Obteniendo todos los tipos de cuenta...");

        Specification<TipoCuenta> specNombreTipoCuenta = (root, query, criteriaBuilder) ->
                nombre.map(i -> criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), "%" + i.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<TipoCuenta> criterio = Specification.where(specNombreTipoCuenta);

        return tipoCuentaRepository.findAll(criterio, pageable);
    }

    @Override
    public TipoCuenta getById(String id) {
        log.info("Obteniendo tipo de cuenta con id: " + id + "...");
        var tipoCuenta = tipoCuentaRepository.findById(id).orElseThrow(() -> new TipoCuentaNotFound(id));
        return tipoCuenta;
    }

    @Override
    public TipoCuenta save(TipoCuenta tipoCuenta) {
        log.info("Guardando tipo de cuenta: {}", tipoCuenta);
        if (tipoCuentaRepository.findByNombre(tipoCuenta.getNombre()).isPresent()) {
            throw new CuentaExists(tipoCuenta.getNombre());
        }
        return tipoCuentaRepository.save(tipoCuenta);
    }

    @Override
    public TipoCuenta update(String id, TipoCuenta tipoCuenta) {
        log.info("Actualizando tipo de cuenta con id {}", id);
        var tipoCuentaEncontrada = tipoCuentaRepository.findById(id).orElseThrow(() -> new TipoCuentaNotFound(id));
        tipoCuentaEncontrada.setUpdatedAt(LocalDateTime.now());
        return tipoCuentaRepository.save(tipoCuentaEncontrada);
    }

    @Override
    public void deleteById(String id) {
        log.info("Eliminando tipo de cuenta con id {}", id);
        var tipoCuentaExistente = tipoCuentaRepository.findById(id).orElseThrow(() -> new TipoCuentaNotFound(id));
        tipoCuentaRepository.delete(tipoCuentaExistente);
    }
}
