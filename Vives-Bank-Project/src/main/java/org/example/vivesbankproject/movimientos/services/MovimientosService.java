package org.example.vivesbankproject.movimientos.services;

import org.bson.types.ObjectId;
import org.example.vivesbankproject.cliente.models.Cliente;
import org.example.vivesbankproject.movimientos.models.Movimientos;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface MovimientosService {
    Page<Movimientos> getAll(Optional<Cliente> cliente, Pageable pageable);

    Movimientos getById(ObjectId idPedido);

    Movimientos getByIdCliente(UUID idCliente);

    Movimientos save(Movimientos pedido);

    Movimientos update(ObjectId idPedido, Movimientos pedido);

    void delete(ObjectId idPedido);

    void softDelete(ObjectId idPedido);

}
