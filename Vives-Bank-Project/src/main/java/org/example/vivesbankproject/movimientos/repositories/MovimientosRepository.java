package org.example.vivesbankproject.movimientos.repositories;

import org.bson.types.ObjectId;
import org.example.vivesbankproject.movimientos.models.Movimiento;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovimientosRepository extends MongoRepository<Movimiento, ObjectId> {
    Optional<Movimiento> findByGuid(String guid);


    Optional<Movimiento> findByClienteGuid(String clienteId);
}