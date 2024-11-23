package org.example.vivesbankproject.movimientoTransaccion.repositories;

import org.bson.types.ObjectId;
import org.example.vivesbankproject.movimientoTransaccion.models.MovimientoTransaccion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovimientosTransaccionRepository extends MongoRepository<MovimientoTransaccion, ObjectId> {
    Optional<MovimientoTransaccion> findMovimientoTransaccionByClienteId(String clienteId);
    Optional<MovimientoTransaccion> findByGuid(String guid);
}