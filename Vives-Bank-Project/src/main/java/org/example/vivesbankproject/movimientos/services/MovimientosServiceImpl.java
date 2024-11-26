package org.example.vivesbankproject.movimientos.services;

import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.example.vivesbankproject.cliente.dto.ClienteResponse;
import org.example.vivesbankproject.cliente.exceptions.ClienteNotFound;
import org.example.vivesbankproject.cliente.mappers.ClienteMapper;
import org.example.vivesbankproject.cliente.repositories.ClienteRepository;
import org.example.vivesbankproject.cliente.service.ClienteService;
import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaForClienteResponse;
import org.example.vivesbankproject.cuenta.mappers.CuentaMapper;
import org.example.vivesbankproject.cuenta.mappers.TipoCuentaMapper;
import org.example.vivesbankproject.movimientos.exceptions.ClienteHasNoMovements;
import org.example.vivesbankproject.movimientos.exceptions.MovimientoNotFound;
import org.example.vivesbankproject.movimientos.models.Movimiento;
import org.example.vivesbankproject.movimientos.repositories.MovimientosRepository;
import org.example.vivesbankproject.tarjeta.mappers.TarjetaMapper;
import org.example.vivesbankproject.users.dto.UserResponse;
import org.example.vivesbankproject.users.mappers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@CacheConfig(cacheNames = {"Movimientos"})
public class MovimientosServiceImpl implements MovimientosService {

    private final ClienteService clienteService;
    private final ClienteMapper clienteMapper;
    private final MovimientosRepository movimientosRepository;
    private final UserMapper userMapper;
    private final CuentaMapper cuentaMapper;
    private final TipoCuentaMapper tipoCuentaMapper;
    private final TarjetaMapper tarjetaMapper;

    @Autowired
    public MovimientosServiceImpl( MovimientosRepository movimientosRepository, ClienteRepository clienteRepository, ClienteMapper clienteMapper, UserMapper userMapper, CuentaMapper cuentaMapper, TipoCuentaMapper tipoCuentaMapper, TarjetaMapper tarjetaMapper, ClienteService clienteService) {
        this.clienteService = clienteService;
        this.tipoCuentaMapper = tipoCuentaMapper;
        this.tarjetaMapper = tarjetaMapper;
        this.cuentaMapper = cuentaMapper;
        this.userMapper = userMapper;
        this.movimientosRepository = movimientosRepository;

        this.clienteMapper = clienteMapper;

    }

    @Override
    public Page<Movimiento> getAll(Pageable pageable) {
        log.info("Encontrando todos los Movimientos");
        return movimientosRepository.findAll(pageable);
    }


    @Override
    @Cacheable(key = "#guidMovimiento")
    public Movimiento getById(ObjectId _id) {
        log.info("Encontrando Movimiento por id: {}", _id);
        return movimientosRepository.findById(_id).orElseThrow(
                () -> new MovimientoNotFound(_id)
        );
    }

    @Override
    @Cacheable(key = "#guidMovimiento")
    public Movimiento getByGuid(String guidMovimiento) {
        log.info("Encontrando Movimiento por id: {}", guidMovimiento);
        return movimientosRepository.findByGuid(guidMovimiento).orElseThrow(
                () -> new MovimientoNotFound(guidMovimiento)
        );
    }

    @Override
    @Cacheable(key = "#idCliente")
    public Movimiento getByClienteGuid(String idCliente) {
        log.info("Encontrando Movimientos por idCliente: {}", idCliente);
        var cliente = clienteService.getById(idCliente);
        if (cliente == null ) {
            throw new ClienteNotFound(idCliente);
        }
        return movimientosRepository.findMovimientosByClienteGuid(idCliente)
                .orElseThrow(() -> new ClienteHasNoMovements(idCliente));
    }

    @Override
    @CachePut(key = "#result.id")
    public Movimiento save(MovimientoRequest movimientoRequest) {
        log.info("Guardando Movimiento: {}", movimientoRequest);
        var cliente = clienteService.getById(movimientoRequest.getIdCliente()).orElseThrow(() -> new ClienteNotFound(movimientoRequest.getIdCliente()));
            // Se crea un nuevo movimiento
            Movimiento movimiento = movimientoMapper.toMovimiento(movimientoRequest);
            // Se guarda el movimiento
            Movimiento savedMovimiento = movimientosRepository.save(movimiento);
            // se setea cliente al movimiento
            movimiento.setCliente(cliente);
            // se guarda el movimiento
           return movimientosRepository.save(movimiento);
    }
}
