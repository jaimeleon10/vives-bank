package org.example.vivesbankproject.rest.movimientos.repositories;

import org.bson.types.ObjectId;
import org.example.vivesbankproject.rest.movimientos.models.Movimiento;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovimientosRepository extends MongoRepository<Movimiento, ObjectId> {
    Optional<Movimiento> findByGuid(String guid);


    Optional<Movimiento> findByClienteGuid(String clienteId);
}