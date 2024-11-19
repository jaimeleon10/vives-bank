package org.example.vivesbankproject.movimientos.services;

import org.bson.types.ObjectId;
import org.example.vivesbankproject.cliente.models.Cliente;
import org.example.vivesbankproject.movimientos.models.Movimientos;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface MovimientosService {
    Page<Movimientos> findAll(Optional<Cliente> cliente, Pageable pageable);

    Movimientos findById(ObjectId idPedido);

    Page<Movimientos> findByIdUsuario(Long idUsuario, Pageable pageable);

    Movimientos save(Movimientos pedido);

    void delete(ObjectId idPedido);

    Movimientos update(ObjectId idPedido, Movimientos pedido);
}