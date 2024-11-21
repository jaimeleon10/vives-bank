package org.example.vivesbankproject.movimientos.services;

import org.bson.types.ObjectId;
import org.example.vivesbankproject.cliente.models.Cliente;
import org.example.vivesbankproject.movimientos.models.Movimientos;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface MovimientosService {
    Page<Movimientos> getAll(Pageable pageable);

    Movimientos getById(String guidMovimiento);

    Movimientos getByClienteId(String idCliente);

    Movimientos save(Movimientos movimiento);

}
