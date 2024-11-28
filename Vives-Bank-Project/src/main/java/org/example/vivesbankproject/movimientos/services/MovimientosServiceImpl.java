package org.example.vivesbankproject.movimientos.services;

import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;

import org.example.vivesbankproject.cliente.service.ClienteService;
import org.example.vivesbankproject.movimientos.dto.MovimientoRequest;
import org.example.vivesbankproject.movimientos.dto.MovimientoResponse;
import org.example.vivesbankproject.movimientos.exceptions.ClienteHasNoMovements;
import org.example.vivesbankproject.movimientos.exceptions.MovimientoNotFound;
import org.example.vivesbankproject.movimientos.mappers.MovimientoMapper;
import org.example.vivesbankproject.movimientos.models.Movimiento;
import org.example.vivesbankproject.movimientos.repositories.MovimientosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@CacheConfig(cacheNames = {"Movimientos"})
public class MovimientosServiceImpl implements MovimientosService {

    private final ClienteService clienteService;
    private final MovimientosRepository movimientosRepository;
    private final MovimientoMapper movimientosMapper;



    @Autowired
    public MovimientosServiceImpl( MovimientosRepository movimientosRepository, ClienteService clienteService, MovimientoMapper movimientosMapper) {
        this.clienteService = clienteService;
        this.movimientosRepository = movimientosRepository;
        this.movimientosMapper = movimientosMapper;
    }

    @Override
    public Page<MovimientoResponse> getAll(Pageable pageable) {
        log.info("Encontrando todos los Movimientos");
        return movimientosRepository.findAll(pageable).map(movimientosMapper::toMovimientoResponse);
    }


    @Override
    @Cacheable
    public MovimientoResponse getById(ObjectId _id) {
        log.info("Encontrando Movimiento por id: {}", _id);
        return movimientosRepository.findById(_id)
                .map(movimientosMapper::toMovimientoResponse)
                .orElseThrow(() -> new MovimientoNotFound(_id));
    }

    @Override
    @Cacheable
    public MovimientoResponse getByGuid(String guidMovimiento) {
        log.info("Encontrando Movimiento por guid: {}", guidMovimiento);
        return movimientosRepository.findByGuid(guidMovimiento)
                .map(movimientosMapper::toMovimientoResponse)
                .orElseThrow(() -> new MovimientoNotFound(guidMovimiento));
    }

    @Override
    @Cacheable
    public MovimientoResponse getByClienteGuid(String ClienteGuid) {
        log.info("Encontrando Movimientos por idCliente: {}", ClienteGuid);
        clienteService.getById(ClienteGuid);
        return movimientosRepository.findMovimientosByClienteGuid(ClienteGuid)
                .map(movimientosMapper::toMovimientoResponse)
                .orElseThrow(() -> new ClienteHasNoMovements(ClienteGuid));
    }

    @Override
    @CachePut
    public MovimientoResponse save(MovimientoRequest movimientoRequest) {
        log.info("Guardando Movimiento: {}", movimientoRequest);
        clienteService.getById(movimientoRequest.getClienteGuid());
        Movimiento movimiento = movimientosMapper.toMovimiento(movimientoRequest);
        var savedMovimiento = movimientosRepository.save(movimiento);
        return movimientosMapper.toMovimientoResponse(savedMovimiento);
    }
}
