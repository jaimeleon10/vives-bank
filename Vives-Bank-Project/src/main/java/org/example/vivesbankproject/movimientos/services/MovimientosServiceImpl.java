package org.example.vivesbankproject.movimientos.services;

import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.example.vivesbankproject.cliente.exceptions.ClienteNotFound;
import org.example.vivesbankproject.cliente.mappers.ClienteMapper;
import org.example.vivesbankproject.cliente.models.Cliente;
import org.example.vivesbankproject.cliente.repositories.ClienteRepository;
import org.example.vivesbankproject.movimientos.dto.MovimientoRequest;
import org.example.vivesbankproject.movimientos.dto.MovimientoResponse;
import org.example.vivesbankproject.movimientos.exceptions.ClienteHasNoMovements;
import org.example.vivesbankproject.movimientos.exceptions.MovimientoNotFound;
import org.example.vivesbankproject.movimientos.mappers.MovimientoMapper;
import org.example.vivesbankproject.movimientos.mappers.TransaccionMapper;
import org.example.vivesbankproject.movimientos.models.Movimientos;
import org.example.vivesbankproject.movimientos.repositories.MovimientosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@CacheConfig(cacheNames = {"Movimientos"})
public class MovimientosServiceImpl implements MovimientosService {

   // private final ClienteService clienteService;
    private final ClienteRepository clienteRepository;
    private final MovimientoMapper movimientosMapper;
    private final ClienteMapper clienteMapper;
    private final TransaccionMapper transaccionMapper;
    private final MovimientosRepository movimientosRepository;

    @Autowired
    public MovimientosServiceImpl( MovimientosRepository movimientosRepository, ClienteRepository clienteRepository, MovimientoMapper movimientoMapper, ClienteMapper clienteMapper, TransaccionMapper transaccionMapper) {
        //this.clienteService = clienteService;
        this.clienteRepository = clienteRepository;
        this.movimientosRepository = movimientosRepository;
        this.movimientosMapper = movimientoMapper;
        this.clienteMapper = clienteMapper;
        this.transaccionMapper = transaccionMapper;
    }

    @Override
    public Page<MovimientoResponse> getAll(Pageable pageable) {
        log.info("Encontrando todos los Movimientos");
        return movimientosMapper.toMovimientoResponse(movimientosRepository.findAll(pageable));
    }


    @Override
    @Cacheable(key = "#guidMovimiento")
    public MovimientoResponse getById(String guidMovimiento) {
        log.info("Encontrando Movimiento por id: {}", guidMovimiento);
        var movimiento = movimientosRepository.findByGuid(guidMovimiento).orElseThrow(
                () -> new MovimientoNotFound(guidMovimiento)
        );
        return movimientosMapper.toMovimientoResponse(movimiento, clienteMapper.toClienteResponse(movimiento.getCliente()), transaccionMapper.toTransaccionResponse(movimiento.getTransacciones()));
    }

    @Override
    @Cacheable(key = "#idCliente")
    public MovimientoResponse getByClienteId(String idCliente) {
        log.info("Encontrando Movimientos por idCliente: {}", idCliente);
        clienteRepository.findByGuid(idCliente).orElseThrow(() -> new ClienteNotFound(idCliente));
        return movimientosRepository.findMovimientosByClienteId(idCliente)
                .orElseThrow(() -> new ClienteHasNoMovements(idCliente));
    }

    @Override
    @CachePut(key = "#result.id")
    public MovimientoResponse save(MovimientoRequest movimientoRequest) {
        log.info("Guardando Movimiento: {}", movimiento);
        var cliente = clienteRepository.findById(movimiento.getCliente().getId()).orElseThrow(() -> new ClienteNotFound(movimiento.getCliente().getGuid()));
        if (cliente.getIdMovimientos() == null) {
            Movimientos savedMovimiento = movimientosRepository.save(movimiento);
            cliente.setIdMovimientos(savedMovimiento.getId());
            clienteRepository.save(cliente);
            movimiento.setCliente(cliente);
            return movimientosRepository.save(movimiento);
        } else {
            Movimientos existingMovimiento = movimientosRepository.findById(new ObjectId(cliente.getIdMovimientos()))
                    .orElseThrow(() -> new MovimientoNotFound(movimiento.getGuid()));
            if (existingMovimiento.getTransacciones() == null) {
                existingMovimiento.setTransacciones(new ArrayList<>());
            }
            existingMovimiento.getTransacciones().addAll(movimiento.getTransacciones());
            existingMovimiento.setUpdatedAt(LocalDateTime.now());
            existingMovimiento.setCliente(cliente);
            return movimientosRepository.save(existingMovimiento);
        }
    }
}
