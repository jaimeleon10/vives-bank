package org.example.vivesbankproject.cuenta.services;

import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.cuenta.dto.CuentaRequest;
import org.example.vivesbankproject.cuenta.exceptions.CuentaNotFound;
import org.example.vivesbankproject.cuenta.mappers.CuentaMapper;
import org.example.vivesbankproject.cuenta.models.Cuenta;
import org.example.vivesbankproject.cuenta.repositories.CuentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
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
    public Page<Cuenta> getAll(Pageable pageable) {
        log.info("Obteniendo todas las cuentas...");
        return cuentaRepository.findAll(pageable);
    }

    @Override
    public Optional<Cuenta> getById(UUID id) {
        log.info("Obteniendo la cuenta con id " + id + "...");
        return cuentaRepository.findById(id);
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
    public Cuenta deleteById(UUID id) {
        log.info("Eliminando cuenta con id " + id + "...");
        var cuentaToDelete = cuentaRepository.findById(id).orElseThrow(() -> new CuentaNotFound(id));
        return cuentaRepository.save(cuentaMapper.toCuentaUpdate(cuentaToDelete));
    }
}