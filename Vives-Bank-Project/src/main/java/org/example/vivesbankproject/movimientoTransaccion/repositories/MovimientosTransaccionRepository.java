package org.example.vivesbankproject.movimientoTransaccion.repositories;

import org.bson.types.ObjectId;
import org.example.vivesbankproject.movimientoTransaccion.models.MovimientoTransaccion;
import org.example.vivesbankproject.movimientos.models.Transacciones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovimientosTransaccionRepository extends JpaRepository<MovimientoTransaccion, ObjectId> {
    Optional<MovimientoTransaccion> getMovimientoTransaccionsByTransacciones(Transacciones transacciones);
}