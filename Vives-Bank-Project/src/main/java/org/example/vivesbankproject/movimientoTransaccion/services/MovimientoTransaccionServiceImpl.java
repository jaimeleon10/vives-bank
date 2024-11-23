package org.example.vivesbankproject.movimientoTransaccion.services;

import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.example.vivesbankproject.cliente.exceptions.ClienteNotFound;
import org.example.vivesbankproject.cliente.repositories.ClienteRepository;
import org.example.vivesbankproject.movimientoTransaccion.exceptions.MovimientoTransaccionNotFound;
import org.example.vivesbankproject.movimientoTransaccion.models.MovimientoTransaccion;
import org.example.vivesbankproject.movimientoTransaccion.repositories.MovimientosTransaccionRepository;
import org.example.vivesbankproject.movimientos.exceptions.ClienteHasNoMovements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@CacheConfig(cacheNames = {"MovimientosTransaccion"})
public class MovimientoTransaccionServiceImpl implements MovimientoTransaccionService {
    private final ClienteRepository clienteRepository;
    private final MovimientosTransaccionRepository movimientosTransaccionRepository;

    @Autowired
    public MovimientoTransaccionServiceImpl(ClienteRepository clienteRepository, MovimientosTransaccionRepository movimientosTransaccionRepository) {
        this.clienteRepository = clienteRepository;
        this.movimientosTransaccionRepository = movimientosTransaccionRepository;
    }

    @Override
    public Page<MovimientoTransaccion> getAll(Pageable pageable) {
        log.info("Encontrando todos los Movimientos Transaccion");
        return movimientosTransaccionRepository.findAll(pageable);
    }

    @Override
    public MovimientoTransaccion getById(String guidMovimientoTransaccion) {
        log.info("Encontrando Movimiento Transaccion por id: {}", guidMovimientoTransaccion);
        return movimientosTransaccionRepository.findByGuid(guidMovimientoTransaccion).orElseThrow(
                () -> new MovimientoTransaccionNotFound(guidMovimientoTransaccion)
        );
    }

    @Override
    public MovimientoTransaccion getByClienteId(String clienteId) {
        log.info("Encontrando Movimiento Transaccion por id de cliente: {}", clienteId);
        clienteRepository.findByGuid(clienteId).orElseThrow(
                () -> new ClienteNotFound(clienteId)
        );
        return movimientosTransaccionRepository.findMovimientoTransaccionByClienteId(clienteId)
                .orElseThrow(() -> new ClienteHasNoMovements(clienteId));
    }

    @Override
    public MovimientoTransaccion save(MovimientoTransaccion movimientoTransaccion) {
        log.info("Creando Movimiento Transaccion: " + movimientoTransaccion);
        var cliente = clienteRepository.findById(movimientoTransaccion.getCliente().getId()).orElseThrow(
                () -> new ClienteNotFound(movimientoTransaccion.getCliente().getGuid())
        );
        if (cliente.getIdMovimientos() == null) {
            MovimientoTransaccion savedMovimiento = movimientosTransaccionRepository.save(movimientoTransaccion);
            cliente.setIdMovimientos(savedMovimiento.getId());
            clienteRepository.save(cliente);
            movimientoTransaccion.setCliente(cliente);
            return movimientosTransaccionRepository.save(movimientoTransaccion);
        } else {
            MovimientoTransaccion existingMovimiento = movimientosTransaccionRepository.findById(new ObjectId(cliente.getIdMovimientos()))
                    .orElseThrow(() -> new MovimientoTransaccionNotFound(movimientoTransaccion.getGuid()));
            existingMovimiento.setTransacciones(movimientoTransaccion.getTransacciones());
            existingMovimiento.setUpdatedAt(LocalDateTime.now());
            existingMovimiento.setCliente(cliente);
            return movimientosTransaccionRepository.save(existingMovimiento);
        }
    }
}
