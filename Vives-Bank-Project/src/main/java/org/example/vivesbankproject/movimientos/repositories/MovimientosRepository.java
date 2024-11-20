package org.example.vivesbankproject.movimientos.repositories;

import org.bson.types.ObjectId;
import org.example.vivesbankproject.movimientos.models.Movimientos;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MovimientosRepository extends MongoRepository<Movimientos, ObjectId> {
    Optional<Movimientos> findMovimientosByClienteId(UUID clienteId);
}
