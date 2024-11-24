package org.example.vivesbankproject.cuenta.services;

import jakarta.persistence.criteria.Join;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.cliente.exceptions.ClienteNotFound;
import org.example.vivesbankproject.cliente.mappers.ClienteMapper;
import org.example.vivesbankproject.cliente.repositories.ClienteRepository;
import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaRequest;
import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaRequestUpdate;
import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaResponse;
import org.example.vivesbankproject.cuenta.exceptions.CuentaNotFound;
import org.example.vivesbankproject.cuenta.exceptions.TipoCuentaNotFound;
import org.example.vivesbankproject.cuenta.mappers.CuentaMapper;
import org.example.vivesbankproject.cuenta.mappers.TipoCuentaMapper;
import org.example.vivesbankproject.cuenta.models.Cuenta;
import org.example.vivesbankproject.cuenta.models.TipoCuenta;
import org.example.vivesbankproject.cuenta.repositories.CuentaRepository;
import org.example.vivesbankproject.cuenta.repositories.TipoCuentaRepository;
import org.example.vivesbankproject.tarjeta.exceptions.TarjetaNotFound;
import org.example.vivesbankproject.tarjeta.mappers.TarjetaMapper;
import org.example.vivesbankproject.tarjeta.repositories.TarjetaRepository;
import org.example.vivesbankproject.users.dto.UserResponse;
import org.example.vivesbankproject.users.mappers.UserMapper;
import org.example.vivesbankproject.users.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@CacheConfig(cacheNames = {"cuenta"})
public class CuentaServiceImpl implements CuentaService{
    private final CuentaRepository cuentaRepository;
    private final CuentaMapper cuentaMapper;
    private final TipoCuentaMapper tipoCuentaMapper;
    private final TarjetaMapper tarjetaMapper;
    private final TipoCuentaRepository tipoCuentaRepository;
    private final TarjetaRepository tarjetaRepository;
    private final ClienteMapper clienteMapper;
    private final ClienteRepository clienteRepository;

    @Autowired
    public CuentaServiceImpl(CuentaRepository cuentaRepository, CuentaMapper cuentaMapper, TipoCuentaMapper tipoCuentaMapper, TarjetaMapper tarjetaMapper, TipoCuentaRepository tipoCuentaRepository, TarjetaRepository tarjetaRepository, ClienteMapper clienteMapper, ClienteRepository clienteRepository) {
        this.cuentaRepository = cuentaRepository;
        this.cuentaMapper = cuentaMapper;
        this.tipoCuentaMapper = tipoCuentaMapper;
        this.tarjetaMapper = tarjetaMapper;
        this.tipoCuentaRepository = tipoCuentaRepository;
        this.tarjetaRepository = tarjetaRepository;
        this.clienteMapper = clienteMapper;
        this.clienteRepository = clienteRepository;
    }

    @Override
    public Page<CuentaResponse> getAll(Optional<String> iban, Optional<BigDecimal> saldoMax, Optional<BigDecimal> saldoMin, Optional<String> tipoCuenta, Pageable pageable) {
        log.info("Obteniendo todas las cuentas");

        Specification<Cuenta> specIbanCuenta = (root, query, criteriaBuilder) ->
                iban.map(i -> criteriaBuilder.like(criteriaBuilder.lower(root.get("iban")), "%" + i.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Cuenta> specSaldoMaxCuenta = (root, query, criteriaBuilder) ->
                saldoMax.map(s -> criteriaBuilder.lessThanOrEqualTo(root.get("saldo"), s))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Cuenta> specSaldoMinCuenta = (root, query, criteriaBuilder) ->
                saldoMin.map(s -> criteriaBuilder.greaterThanOrEqualTo(root.get("saldo"), s))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Cuenta> specTipoCuentaFunko = (root, query, criteriaBuilder) ->
                tipoCuenta.map(t -> {
                    Join<Cuenta, TipoCuenta> tipoCuentaJoin = root.join("tipoCuenta");
                    return criteriaBuilder.like(criteriaBuilder.lower(tipoCuentaJoin.get("nombre")), "%" + t.toLowerCase() + "%");
                }).orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Cuenta> criterio = Specification.where(specIbanCuenta)
                .and(specSaldoMaxCuenta)
                .and(specSaldoMinCuenta)
                .and(specTipoCuentaFunko);

        Page<Cuenta> cuentaPage = cuentaRepository.findAll(criterio, pageable);

        return cuentaPage.map(cuenta ->
                cuentaMapper.toCuentaResponse(
                        cuenta,
                        tipoCuentaMapper.toTipoCuentaResponse(cuenta.getTipoCuenta()),
                        tarjetaMapper.toTarjetaResponse(cuenta.getTarjeta()),
                        clienteMapper.toClienteDataResponse(cuenta.getCliente())
                )
        );
    }

    @Override
    @Cacheable
    public CuentaResponse getById(String id) {
        log.info("Obteniendo la cuenta con id: {}", id);
        var cuenta = cuentaRepository.findByGuid(id).orElseThrow(() -> new CuentaNotFound(id));
        var tipoCuentaResponse = tipoCuentaMapper.toTipoCuentaResponse(cuenta.getTipoCuenta());
        var tarjetaResponse = tarjetaMapper.toTarjetaResponse(cuenta.getTarjeta());
        return cuentaMapper.toCuentaResponse(cuenta, tipoCuentaResponse, tarjetaResponse, clienteMapper.toClienteDataResponse(cuenta.getCliente()));
    }

    @Override
    @CachePut
    public CuentaResponse save(CuentaRequest cuentaRequest) {
        log.info("Guardando cuenta: {}", cuentaRequest);
        var tipoCuenta = tipoCuentaRepository.findByGuid(cuentaRequest.getTipoCuentaId()).orElseThrow(
                () -> new TipoCuentaNotFound(cuentaRequest.getTipoCuentaId())
        );
        var tarjeta = tarjetaRepository.findByGuid(cuentaRequest.getTarjetaId()).orElseThrow(
                () -> new TarjetaNotFound(cuentaRequest.getTarjetaId())
        );
        var cliente = clienteRepository.findByGuid(cuentaRequest.getClienteId()).orElseThrow(
                () -> new ClienteNotFound(cuentaRequest.getClienteId())
        );
        var cuentaSaved = cuentaRepository.save(cuentaMapper.toCuenta(tipoCuenta, tarjeta, cliente));

        // Actualizamos el listado de cuentas del cliente
        cliente.getCuentas().add(cuentaSaved);
        clienteRepository.save(cliente);

        // Forzamos sincronización y evitamos cache en siguiente busqueda de cliente
        clienteRepository.flush();
        evictClienteCache(cliente.getGuid());

        var tipoCuentaResponse = tipoCuentaMapper.toTipoCuentaResponse(cuentaSaved.getTipoCuenta());
        var tarjetaResponse = tarjetaMapper.toTarjetaResponse(cuentaSaved.getTarjeta());
        var clienteResponse = clienteMapper.toClienteDataResponse(cliente);
        return cuentaMapper.toCuentaResponse(cuentaSaved, tipoCuentaResponse, tarjetaResponse, clienteResponse);
    }

    @Override
    @CachePut
    public CuentaResponse update(String id, CuentaRequestUpdate cuentaRequestUpdate) {
        log.info("Actualizando cuenta con id {}", id);
        var cuenta = cuentaRepository.findByGuid(id).orElseThrow(
                () -> new CuentaNotFound(id)
        );
        var cuentaSaved = cuentaRepository.save(cuentaMapper.toCuentaUpdate(cuentaRequestUpdate, cuenta, cuenta.getTipoCuenta(), cuenta.getTarjeta()));

        var tipoCuentaResponse = tipoCuentaMapper.toTipoCuentaResponse(cuentaSaved.getTipoCuenta());
        var tarjetaResponse = tarjetaMapper.toTarjetaResponse(cuentaSaved.getTarjeta());
        var clienteResponse = clienteMapper.toClienteDataResponse(cuentaSaved.getCliente());
        return cuentaMapper.toCuentaResponse(cuentaSaved, tipoCuentaResponse, tarjetaResponse, clienteResponse);
    }

    @Override
    @CacheEvict
    public void deleteById(String id) {
        log.info("Eliminando cuenta con id {}", id);
        var cuentaExistente = cuentaRepository.findByGuid(id).orElseThrow(
                () -> new CuentaNotFound(id)
        );
        cuentaExistente.setIsDeleted(true);
        cuentaRepository.save(cuentaExistente);
    }

    @CacheEvict
    public void evictClienteCache(String clienteGuid) {
        log.info("Invalidando la caché del cliente con GUID: {}", clienteGuid);
    }
}