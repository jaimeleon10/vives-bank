package org.example.vivesbankproject.movimientos.services;

import org.bson.types.ObjectId;
import org.example.vivesbankproject.movimientos.dto.MovimientoRequest;
import org.example.vivesbankproject.movimientos.dto.MovimientoResponse;
import org.example.vivesbankproject.movimientos.models.Domiciliacion;
import org.example.vivesbankproject.movimientos.models.Movimiento;
import org.example.vivesbankproject.users.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MovimientosService {
    Page<MovimientoResponse> getAll(Pageable pageable);

    MovimientoResponse getById(ObjectId _id);

    MovimientoResponse getByGuid(String guidMovimiento);

    MovimientoResponse getByClienteGuid(String idCliente);

    MovimientoResponse save(MovimientoRequest movimientoRequest);

    Domiciliacion saveDomiciliacion(User user, Domiciliacion domiciliacion);

    MovimientoResponse saveIngresoDeNomina(User user, MovimientoRequest movimientoRequest);

    MovimientoResponse savePagoConTarjeta(User user, MovimientoRequest movimientoRequest);

    MovimientoResponse saveTransferencia(User user, MovimientoRequest movimientoRequest);

}
