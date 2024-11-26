package org.example.vivesbankproject.movimientos.services;

import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.example.vivesbankproject.cliente.dto.ClienteResponse;
import org.example.vivesbankproject.cliente.exceptions.ClienteNotFound;
import org.example.vivesbankproject.cliente.mappers.ClienteMapper;
import org.example.vivesbankproject.cliente.models.Cliente;
import org.example.vivesbankproject.cliente.repositories.ClienteRepository;
import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaForClienteResponse;
import org.example.vivesbankproject.cuenta.mappers.CuentaMapper;
import org.example.vivesbankproject.cuenta.mappers.TipoCuentaMapper;
import org.example.vivesbankproject.movimientos.dto.MovimientoRequest;
import org.example.vivesbankproject.movimientos.dto.MovimientoResponse;
import org.example.vivesbankproject.movimientos.exceptions.ClienteHasNoMovements;
import org.example.vivesbankproject.movimientos.exceptions.MovimientoNotFound;
import org.example.vivesbankproject.movimientos.mappers.MovimientoMapper;
import org.example.vivesbankproject.movimientos.mappers.TransaccionMapper;
import org.example.vivesbankproject.movimientos.models.Movimientos;
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
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
    private final UserMapper userMapper;
    private final CuentaMapper cuentaMapper;
    private final TipoCuentaMapper tipoCuentaMapper;
    private final TarjetaMapper tarjetaMapper;

    @Autowired
    public MovimientosServiceImpl( MovimientosRepository movimientosRepository, ClienteRepository clienteRepository, MovimientoMapper movimientoMapper, ClienteMapper clienteMapper, TransaccionMapper transaccionMapper, UserMapper userMapper, CuentaMapper cuentaMapper, TipoCuentaMapper tipoCuentaMapper, TarjetaMapper tarjetaMapper) {
        //this.clienteService = clienteService;
        this.tipoCuentaMapper = tipoCuentaMapper;
        this.tarjetaMapper = tarjetaMapper;
        this.cuentaMapper = cuentaMapper;
        this.userMapper = userMapper;
        this.clienteRepository = clienteRepository;
        this.movimientosRepository = movimientosRepository;
        this.movimientosMapper = movimientoMapper;
        this.clienteMapper = clienteMapper;
        this.transaccionMapper = transaccionMapper;
    }

    @Override
    public Page<MovimientoResponse> getAll(Pageable pageable) {
        log.info("Encontrando todos los Movimientos");
        Page<Movimientos> movimientoPage = movimientosRepository.findAll(pageable);
        return movimientoPage.map( movimientos -> {
            UserResponse userResponse = userMapper.toUserResponse(movimientos.getCliente().getUser());
            Set<CuentaForClienteResponse> cuentasResponse = movimientos.getCliente().getCuentas().stream()
                    .map(cuenta -> cuentaMapper.toCuentaForClienteResponse(cuenta, tipoCuentaMapper.toTipoCuentaResponse(cuenta.getTipoCuenta()), tarjetaMapper.toTarjetaResponse(cuenta.getTarjeta())))
                    .collect(Collectors.toSet());

             var clienteResponse = clienteMapper.toClienteResponse(movimientos.getCliente(), userResponse, cuentasResponse);
             return movimientosMapper.toMovimientoResponse(movimientos, clienteResponse, movimientos.getTransacciones());
        });
    }


    @Override
    @Cacheable(key = "#guidMovimiento")
    public MovimientoResponse getById(String guidMovimiento) {
        log.info("Encontrando Movimiento por id: {}", guidMovimiento);
        var movimiento = movimientosRepository.findByGuid(guidMovimiento).orElseThrow(
                () -> new MovimientoNotFound(guidMovimiento)
        );
        UserResponse userResponse = userMapper.toUserResponse(movimiento.getCliente().getUser());
        Set<CuentaForClienteResponse> cuentasResponse = movimiento.getCliente().getCuentas().stream()
                .map(cuenta -> cuentaMapper.toCuentaForClienteResponse(cuenta, tipoCuentaMapper.toTipoCuentaResponse(cuenta.getTipoCuenta()), tarjetaMapper.toTarjetaResponse(cuenta.getTarjeta())))
                .collect(Collectors.toSet());

        var clienteResponse = clienteMapper.toClienteResponse(movimiento.getCliente(), userResponse, cuentasResponse);

        return movimientosMapper.toMovimientoResponse(movimiento, clienteResponse, movimiento.getTransacciones());
    }

    @Override
    @Cacheable(key = "#idCliente")
    public MovimientoResponse getByClienteId(String idCliente) {
        log.info("Encontrando Movimientos por idCliente: {}", idCliente);
        clienteRepository.findByGuid(idCliente).orElseThrow(() -> new ClienteNotFound(idCliente));
        var movimiento = movimientosRepository.findMovimientosByClienteId(idCliente)
                .orElseThrow(() -> new ClienteHasNoMovements(idCliente));
        UserResponse userResponse = userMapper.toUserResponse(movimiento.getCliente().getUser());
        Set<CuentaForClienteResponse> cuentasResponse = movimiento.getCliente().getCuentas().stream()
                .map(cuenta -> cuentaMapper.toCuentaForClienteResponse(cuenta, tipoCuentaMapper.toTipoCuentaResponse(cuenta.getTipoCuenta()), tarjetaMapper.toTarjetaResponse(cuenta.getTarjeta())))
                .collect(Collectors.toSet());

        var clienteResponse = clienteMapper.toClienteResponse(movimiento.getCliente(), userResponse, cuentasResponse);

        return movimientosMapper.toMovimientoResponse(movimiento, clienteResponse, movimiento.getTransacciones());
    }

    @Override
    @CachePut(key = "#result.id")
    public MovimientoResponse save(MovimientoRequest movimientoRequest) {
        log.info("Guardando Movimiento: {}", movimientoRequest);
        var cliente = clienteRepository.findByGuid(movimientoRequest.getIdCliente()).orElseThrow(() -> new ClienteNotFound(movimientoRequest.getIdCliente()));
        if (cliente.getIdMovimientos() == null) {
            Movimientos movimiento = movimientosMapper.toMovimientos(cliente, movimientoRequest.getTransacciones());
            Movimientos savedMovimiento = movimientosRepository.save(movimiento);
            cliente.setIdMovimientos(savedMovimiento.getId());
            clienteRepository.save(cliente);
            movimiento.setCliente(cliente);
            movimientosRepository.save(movimiento);
            UserResponse userResponse = userMapper.toUserResponse(movimiento.getCliente().getUser());
            Set<CuentaForClienteResponse> cuentasResponse = movimiento.getCliente().getCuentas().stream()
                    .map(cuenta -> cuentaMapper.toCuentaForClienteResponse(cuenta, tipoCuentaMapper.toTipoCuentaResponse(cuenta.getTipoCuenta()), tarjetaMapper.toTarjetaResponse(cuenta.getTarjeta())))
                    .collect(Collectors.toSet());

            var clienteResponse = clienteMapper.toClienteResponse(movimiento.getCliente(), userResponse, cuentasResponse);

            return movimientosMapper.toMovimientoResponse(movimiento, clienteResponse, movimiento.getTransacciones());
        } else {
            Movimientos existingMovimiento = movimientosRepository.findById(new ObjectId(cliente.getIdMovimientos()))
                    .orElseThrow(() -> new MovimientoNotFound(movimientoRequest.getGuid()));
            if (existingMovimiento.getTransacciones() == null) {
                existingMovimiento.setTransacciones(new ArrayList<>());
            }
            existingMovimiento.getTransacciones().addAll(movimientoRequest.getTransacciones());
            existingMovimiento.setUpdatedAt(LocalDateTime.now());
            existingMovimiento.setCliente(cliente);
            movimientosRepository.save(existingMovimiento);
            UserResponse userResponse = userMapper.toUserResponse(existingMovimiento.getCliente().getUser());
            Set<CuentaForClienteResponse> cuentasResponse = existingMovimiento.getCliente().getCuentas().stream()
                    .map(cuenta -> cuentaMapper.toCuentaForClienteResponse(cuenta, tipoCuentaMapper.toTipoCuentaResponse(cuenta.getTipoCuenta()), tarjetaMapper.toTarjetaResponse(cuenta.getTarjeta())))
                    .collect(Collectors.toSet());

            var clienteResponse = clienteMapper.toClienteResponse(existingMovimiento.getCliente(), userResponse, cuentasResponse);

            return movimientosMapper.toMovimientoResponse(existingMovimiento, clienteResponse, existingMovimiento.getTransacciones());
        }
    }
}
