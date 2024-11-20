package org.example.vivesbankproject.cuenta.services;

import jakarta.persistence.criteria.Join;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.cliente.models.Cliente;
import org.example.vivesbankproject.cuenta.exceptions.CuentaNotFound;
import org.example.vivesbankproject.cuenta.mappers.CuentaMapper;
import org.example.vivesbankproject.cuenta.models.Cuenta;
import org.example.vivesbankproject.cuenta.models.TipoCuenta;
import org.example.vivesbankproject.cuenta.repositories.CuentaRepository;
import org.example.vivesbankproject.tarjeta.models.Tarjeta;
import org.example.vivesbankproject.tarjeta.models.Tipo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class CuentaServiceImpl implements CuentaService{
    private final CuentaRepository cuentaRepository;
    private final CuentaMapper cuentaMapper;

    @Autowired
    public CuentaServiceImpl(CuentaRepository cuentaRepository, CuentaMapper cuentaMapper) {
        this.cuentaRepository = cuentaRepository;
        this.cuentaMapper = cuentaMapper;
    }

    @Override
    public Page<Cuenta> getAll(Optional<String> iban, Optional<Double> saldo, Optional<Tarjeta> tarjeta, Optional<TipoCuenta> tipoCuenta, Pageable pageable) {
        log.info("Obteniendo todas las cuentas...");

        Specification<Cuenta> specIbanCuenta = (root, query, criteriaBuilder) ->
                iban.map(m -> criteriaBuilder.like(criteriaBuilder.lower(root.get("iban")), "%" + m.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.conjunction());

        Specification<Cuenta> specSaldoCuenta = (root, query, criteriaBuilder) ->
                saldo.map(m -> criteriaBuilder.lessThanOrEqualTo(root.get("saldo"), m))
                        .orElseGet(() -> criteriaBuilder.conjunction());

        Specification<Cuenta> specTipoCuenta = (root, query, criteriaBuilder) ->
                tipoCuenta.map(tc -> {
                    Join<Cuenta, TipoCuenta> tipoCuentaJoin = root.join("tipoCuenta");
                    return criteriaBuilder.like(criteriaBuilder.lower(tipoCuentaJoin.get("nombre")), "%" + tc.getNombre().toUpperCase() + "%");
                }).orElseGet(() -> criteriaBuilder.conjunction());

        Specification<Cuenta> specTarjetaCuenta = (root, query, criteriaBuilder) ->
                tarjeta.map(t -> {
                    Join<Cuenta, Tarjeta> tarjetaJoin = root.join("tarjeta");
                    return criteriaBuilder.like(criteriaBuilder.lower(tarjetaJoin.get("nombre")), "%" + t.getNumeroTarjeta() + "%");
                }).orElseGet(() -> criteriaBuilder.conjunction());

        Specification<Cuenta> criterio = Specification.where(specIbanCuenta)
                .and(specSaldoCuenta)
                .and(specTipoCuenta)
                .and(specTarjetaCuenta);

        return cuentaRepository.findAll(criterio, pageable);
    }

    @Override
    public Optional<Cuenta> getById(UUID id) {
        log.info("Obteniendo la cuenta con id " + id + "...");
        return Optional.of(cuentaRepository.findById(id).orElseThrow(() -> new CuentaNotFound(id)));
    }

    @Override
    public Cuenta save(Cuenta cuenta) {
        log.info("Guardando cuenta: " + cuenta + "...");
        cuenta.setCreatedAt(LocalDateTime.now());
        cuenta.setUpdatedAt(LocalDateTime.now());
        return cuentaRepository.save(cuenta);
    }

    @Override
    public Cuenta update(UUID id, Cuenta cuenta) {
        log.info("Actualizando cuenta con id " + id + "...");
        var cuentaToUpdate = cuentaRepository.findById(id).orElseThrow(() -> new CuentaNotFound(id));
        cuentaToUpdate.setUpdatedAt(LocalDateTime.now());
        return cuentaRepository.save(cuentaToUpdate);
    }

    @Override
    public Cuenta delete(UUID id) {
        log.info("Eliminando cuenta con id " + id + "...");
        var cuentaToDelete = cuentaRepository.findById(id).orElseThrow(() -> new CuentaNotFound(id));
        return cuentaRepository.save(cuentaMapper.toCuentaUpdate(cuentaToDelete));
    }
}