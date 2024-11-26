package org.example.vivesbankproject.movimientos.services;

import org.bson.types.ObjectId;
import org.example.vivesbankproject.movimientos.models.Movimiento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MovimientosService {
    Page<Movimiento> getAll(Pageable pageable);

    Movimiento getById(ObjectId _id);

    Movimiento getByGuid(String guidMovimiento);

    Movimiento getByClienteGuid(String idCliente);

    Movimiento save(MovimientoRequest movimientoRequest);

}
