package org.example.vivesbankproject.movimientos.services;

import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.example.vivesbankproject.cliente.models.Cliente;
import org.example.vivesbankproject.movimientos.models.Movimientos;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@CacheConfig(cacheNames = {"Movimientos"})
public class MovimientosServiceImpl implements MovimientosService {

    private final
    @Override
    public Page<Movimientos> findAll(Optional<Cliente> cliente, Pageable pageable) {
        return null;
    }

    @Override
    public Movimientos findById(ObjectId idPedido) {
        return null;
    }

    @Override
    public Page<Movimientos> findByIdUsuario(Long idUsuario, Pageable pageable) {
        return null;
    }

    @Override
    public Movimientos save(Movimientos pedido) {
        return null;
    }

    @Override
    public void delete(ObjectId idPedido) {

    }

    @Override
    public Movimientos update(ObjectId idPedido, Movimientos pedido) {
        return null;
    }
}
