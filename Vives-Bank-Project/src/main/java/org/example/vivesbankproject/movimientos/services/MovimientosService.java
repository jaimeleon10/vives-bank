package org.example.vivesbankproject.movimientos.services;

import org.example.vivesbankproject.movimientos.dto.MovimientoRequest;
import org.example.vivesbankproject.movimientos.dto.MovimientoResponse;
import org.example.vivesbankproject.movimientos.models.Movimientos;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MovimientosService {
    Page<MovimientoResponse> getAll(Pageable pageable);

    MovimientoResponse getById(String guidMovimiento);

    MovimientoResponse getByClienteId(String idCliente);

    MovimientoResponse save(MovimientoRequest movimientoRequest);

}
