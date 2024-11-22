package org.example.vivesbankproject.movimientoTransaccion.services;

import org.example.vivesbankproject.movimientoTransaccion.models.MovimientoTransaccion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MovimientoTransaccionService {
    Page<MovimientoTransaccion> getAll(Pageable pageable);

    MovimientoTransaccion getById(String guidMovimientoTransaccion);

    MovimientoTransaccion getByClienteId(String clienteId);

    MovimientoTransaccion save(MovimientoTransaccion movimientoTransaccion);
}